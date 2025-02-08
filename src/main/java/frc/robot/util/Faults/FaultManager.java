package frc.robot.util.Faults;

import com.revrobotics.REVLibError;
import com.revrobotics.spark.SparkBase;
import com.revrobotics.spark.SparkBase.Faults;
import com.studica.frc.AHRS;

import edu.wpi.first.hal.PowerDistributionFaults;
import edu.wpi.first.hal.PowerDistributionStickyFaults;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.StringArrayPublisher;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DutyCycleEncoder;
import edu.wpi.first.wpilibj.PowerDistribution;
import frc.robot.util.Constants.Ports;
import frc.robot.util.Constants.Ports.CANID;
import frc.robot.util.Faults.FaultTypes.FaultType;
import frc.robot.util.Faults.FaultTypes.PDFaults;
import frc.robot.util.Faults.FaultTypes.PDStickyFaults;
import frc.robot.util.Faults.FaultTypes.SparkFaults;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

/**
 * FaultManager is a utility class for monitoring and reporting faults from
 * various robot components.
 * It aggregates faults from different sources and publishes them to Network
 * Tables for real-time monitoring.
 */
public final class FaultManager {

    // Record to represent a Fault
    public static record Fault(String name, String description, FaultType type) {
        @Override
        public String toString() {
            return name + ": " + description;
        }
    }

    // Functional Interface for reporting faults
    @FunctionalInterface
    public static interface FaultReporter {
        void report();
    }

    /**
     * Alerts class handles the publishing of faults to Network Tables categorized
     * by FaultType.
     */
    public static class Alerts {
        private final StringArrayPublisher errors;
        private final StringArrayPublisher warnings;
        private final StringArrayPublisher infos;

        /**
         * Constructs an Alerts instance for a specific category.
         *
         * @param base The base NetworkTable.
         * @param name The name of the alerts category.
         */
        public Alerts(NetworkTable base, String name) {
            NetworkTable table = base.getSubTable(name);
            table.getStringTopic(".type").publish().set("Alerts");
            errors = table.getStringArrayTopic("errors").publish();
            warnings = table.getStringArrayTopic("warnings").publish();
            infos = table.getStringArrayTopic("infos").publish();
        }

        /**
         * Sets the faults in their respective categories.
         *
         * @param faults The set of faults to categorize and publish.
         */
        public void set(Set<Fault> faults) {
            errors.set(filteredStrings(faults, FaultType.ERROR));
            warnings.set(filteredStrings(faults, FaultType.WARNING));
            infos.set(filteredStrings(faults, FaultType.INFO));
        }

        /**
         * Filters faults based on their type and returns their string representations.
         *
         * @param faults The set of faults to filter.
         * @param type   The type to filter for.
         * @return An array of description strings.
         */
        private String[] filteredStrings(Set<Fault> faults, FaultType type) {
            return faults.stream()
                    .filter(fault -> fault.type() == type)
                    .map(Fault::toString)
                    .toArray(String[]::new);
        }
    }

    // DATA
    private static final List<FaultReporter> faultReporters = new ArrayList<>();
    private static final Set<Fault> newFaults = new HashSet<>();
    private static final Set<Fault> activeFaults = new HashSet<>();
    private static final Set<Fault> totalFaults = new HashSet<>();

    // NETWORK TABLES
    private static final NetworkTable base = NetworkTableInstance.getDefault().getTable("Faults");
    private static final Alerts activeAlerts = new Alerts(base, "Active Faults");
    private static final Alerts totalAlerts = new Alerts(base, "Total Faults");

    /**
     * Polls registered fault reporters. This method should be called periodically.
     */
    public static void update() {
        activeFaults.clear();

        // Execute all fault reporters to populate newFaults
        faultReporters.forEach(FaultReporter::report);
        activeFaults.addAll(newFaults);
        newFaults.clear();

        // Update total faults
        totalFaults.addAll(activeFaults);

        // Publish faults to NetworkTables
        activeAlerts.set(activeFaults);
        totalAlerts.set(totalFaults);
    }

    /**
     * Clears total faults.
     */
    public static void clear() {
        totalFaults.clear();
        activeFaults.clear();
        newFaults.clear();
    }

    /**
     * Clears fault reporters.
     */
    public static void unregisterAll() {
        faultReporters.clear();
    }

    /**
     * Returns the set of all current active faults.
     *
     * @return The set of all current active faults.
     */
    public static Set<Fault> getActiveFaults() {
        return activeFaults;
    }

    /**
     * Returns the set of all total faults.
     *
     * @return The set of all total faults.
     */
    public static Set<Fault> getTotalFaults() {
        return totalFaults;
    }

    /**
     * Reports a fault.
     *
     * @param fault The fault to report.
     */
    public static void report(Fault fault) {
        newFaults.add(fault);
        switch (fault.type()) {
            case ERROR:
                DriverStation.reportError(fault.toString(), false);
                break;
            case WARNING:
                DriverStation.reportWarning(fault.toString(), false);
                break;
            case INFO:
                System.out.println(fault.toString());
                break;
        }
    }

    /**
     * Reports a fault.
     *
     * @param name        The name of the fault.
     * @param description The description of the fault.
     * @param type        The type of fault.
     */
    public static void report(String name, String description, FaultType type) {
        report(new Fault(name, description, type));
    }

    /**
     * Registers a new fault supplier.
     *
     * @param supplier A supplier of an optional fault.
     */
    public static void register(Supplier<Optional<Fault>> supplier) {
        faultReporters.add(() -> supplier.get().ifPresent(FaultManager::report));
    }

    /**
     * Registers a new fault supplier based on a condition.
     *
     * @param condition   Whether a failure is occurring.
     * @param name        The name of the fault.
     * @param description The description of the fault.
     * @param type        The type of fault.
     */
    public static void register(BooleanSupplier condition, String name, String description, FaultType type) {
        faultReporters.add(() -> {
            if (condition.getAsBoolean()) {
                report(name, description, type);
            }
        });
    }

    /**
     * Generates a unique identifier for a Spark motor controller.
     *
     * @param spark The SparkBase instance.
     * @return A formatted identifier string.
     */
    public static String getIdentifier(SparkBase spark) {
        return String.format(
                "Spark [%d] - %s",
                spark.getDeviceId(),
                Ports.CANID.getNameById(spark.getDeviceId()));
    }

    /**
     * Registers fault reporters for a Spark motor controller.
     *
     * @param spark The SparkMax or Spark Flex to manage.
     */
    public static void register(SparkBase spark) {
        // Register Spark-specific faults
        faultReporters.add(() -> {
            if (spark.hasActiveFault()) {
                Faults faults = spark.getFaults();
                String identifier = getIdentifier(spark);

                for (SparkFaults fault : SparkFaults.values()) {
                    if (fault.isPresent(faults)) {
                        report(
                                identifier,
                                fault.getDescription(),
                                fault.getType());
                    }
                }
            }
        });
    }

    /**
     * Registers fault reporters for a duty cycle encoder.
     *
     * @param encoder The duty cycle encoder to manage.
     */
    public static void register(DutyCycleEncoder encoder) {
        register(
                () -> !encoder.isConnected(),
                "Duty Cycle Encoder [" + encoder.getSourceChannel() + "]",
                "Disconnected",
                FaultType.ERROR);
    }

    /**
     * Registers fault reporters for a NavX.
     *
     * @param ahrs The NavX to manage.
     */
    public static void register(AHRS ahrs) {
        register(
                () -> !ahrs.isConnected(),
                "NavX",
                "Disconnected",
                FaultType.ERROR);
    }

    /**
     * Registers fault reporters for a Power Distribution Hub/Panel.
     *
     * @param powerDistribution The PowerDistribution to manage.
     */
    public static void register(PowerDistribution powerDistribution) {
        faultReporters.add(() -> {
            PowerDistributionFaults faults = powerDistribution.getFaults();
            String identifier = "Power Distribution Hub";

            for (PDFaults fault : PDFaults.values()) {
                if (fault.isPresent(faults)) {
                    report(
                            identifier,
                            fault.getDescription(),
                            fault.getType());
                }
            }

            PowerDistributionStickyFaults stickyFaults = powerDistribution.getStickyFaults();
            for (PDStickyFaults fault : PDStickyFaults.values()) {
                if (fault.isPresent(stickyFaults)) {
                    report(
                            identifier,
                            "Sticky: " + fault.getDescription(),
                            fault.getType());
                }
            }
        });

        // Example: Register a warning if battery voltage is below a threshold
        // register(
        //         () -> powerDistribution.getVoltage() < 11.5,
        //         "Power Distribution Hub",
        //         "Battery voltage below 11.5V",
        //         FaultType.WARNING);
    }

    /**
     * Reports REVLibErrors from a Spark motor controller.
     *
     * This should be called immediately after any call to the spark.
     *
     * @param spark The SparkBase to report REVLibErrors from.
     * @return If the spark is working without errors.
     */
    public static boolean check(SparkBase spark) {
        REVLibError error = spark.getLastError();
        return check(spark, error);
    }

    /**
     * Reports REVLibErrors from a Spark motor controller.
     *
     * This should be called immediately after any call to the spark.
     *
     * @param spark The SparkBase to report REVLibErrors from.
     * @param error Any REVLibErrors that may be returned from a method for a spark.
     * @return If the spark is working without errors.
     */
    public static boolean check(SparkBase spark, REVLibError error) {
        if (error != REVLibError.kOk) {
            report(
                    getIdentifier(spark),
                    error.name(),
                    FaultType.ERROR);
            return false;
        }
        return true;
    }

    /**
     * Reports faults from a REVLibError.
     *
     * @param spark The SparkBase instance.
     * @param error The REVLibError to report.
     */
    public static void reportError(SparkBase spark, REVLibError error) {
        if (error != REVLibError.kOk) {
            report(
                    getIdentifier(spark),
                    "REVLibError: " + error.name(),
                    FaultType.ERROR);
        }
    }
}