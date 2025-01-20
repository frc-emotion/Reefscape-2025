package frc.robot.util.Faults;

import com.revrobotics.spark.SparkBase.Faults;

import edu.wpi.first.hal.PowerDistributionFaults;
import edu.wpi.first.hal.PowerDistributionStickyFaults;

public class FaultTypes {
    /**
     * Represents the severity type of a fault.
     */
    public enum FaultType {
        INFO,
        WARNING,
        ERROR,
    }

    /**
     * Enum representing faults from Spark motor controllers.
     */
    public enum SparkFaults {
        CAN("CAN Fault", FaultType.ERROR),
        ESC_EEPROM("EEPROM Memory Error", FaultType.ERROR),
        FIRMWARE("Firmware Fault", FaultType.ERROR),
        GATE_DRIVER("Gate Driver Fault", FaultType.ERROR),
        MOTOR_TYPE("Motor Type Fault", FaultType.ERROR),
        OTHER("Unknown Fault", FaultType.ERROR),
        SENSOR("Sensor Fault", FaultType.ERROR),
        TEMPERATURE("Temperature Fault", FaultType.ERROR);

        private final String description;
        private final FaultType type;

        SparkFaults(String description, FaultType type) {
            this.description = description;
            this.type = type;
        }

        public String getDescription() {
            return description;
        }

        public FaultType getType() {
            return type;
        }

        /**
         * Checks if the fault is present in the given Faults object.
         *
         * @param faults The Faults object containing current faults.
         * @return True if the fault is present, false otherwise.
         */
        public boolean isPresent(Faults faults) {
            switch (this) {
                case CAN:
                    return faults.can;
                case ESC_EEPROM:
                    return faults.escEeprom;
                case FIRMWARE:
                    return faults.firmware;
                case GATE_DRIVER:
                    return faults.gateDriver;
                case MOTOR_TYPE:
                    return faults.motorType;
                case OTHER:
                    return faults.other;
                case SENSOR:
                    return faults.sensor;
                case TEMPERATURE:
                    return faults.temperature;
                default:
                    return false;
            }
        }
    }

    /**
     * Enum representing faults from the Power Distribution Hub (PDH).
     */
    public enum PDFaults {
        BROWN_OUT("Brownout Fault", FaultType.ERROR),
        CAN_WARNING("CAN Warning Fault", FaultType.ERROR),
        HARDWARE("Hardware Fault", FaultType.ERROR),
        CHANNEL_0_BREAKER("Breaker fault on channel 0", FaultType.ERROR),
        CHANNEL_1_BREAKER("Breaker fault on channel 1", FaultType.ERROR),
        CHANNEL_2_BREAKER("Breaker fault on channel 2", FaultType.ERROR),
        CHANNEL_3_BREAKER("Breaker fault on channel 3", FaultType.ERROR),
        CHANNEL_4_BREAKER("Breaker fault on channel 4", FaultType.ERROR),
        CHANNEL_5_BREAKER("Breaker fault on channel 5", FaultType.ERROR),
        CHANNEL_6_BREAKER("Breaker fault on channel 6", FaultType.ERROR),
        CHANNEL_7_BREAKER("Breaker fault on channel 7", FaultType.ERROR),
        CHANNEL_8_BREAKER("Breaker fault on channel 8", FaultType.ERROR),
        CHANNEL_9_BREAKER("Breaker fault on channel 9", FaultType.ERROR),
        CHANNEL_10_BREAKER("Breaker fault on channel 10", FaultType.ERROR),
        CHANNEL_11_BREAKER("Breaker fault on channel 11", FaultType.ERROR),
        CHANNEL_12_BREAKER("Breaker fault on channel 12", FaultType.ERROR),
        CHANNEL_13_BREAKER("Breaker fault on channel 13", FaultType.ERROR),
        CHANNEL_14_BREAKER("Breaker fault on channel 14", FaultType.ERROR),
        CHANNEL_15_BREAKER("Breaker fault on channel 15", FaultType.ERROR),
        CHANNEL_16_BREAKER("Breaker fault on channel 16", FaultType.ERROR),
        CHANNEL_17_BREAKER("Breaker fault on channel 17", FaultType.ERROR),
        CHANNEL_18_BREAKER("Breaker fault on channel 18", FaultType.ERROR),
        CHANNEL_19_BREAKER("Breaker fault on channel 19", FaultType.ERROR),
        CHANNEL_20_BREAKER("Breaker fault on channel 20", FaultType.ERROR),
        CHANNEL_21_BREAKER("Breaker fault on channel 21", FaultType.ERROR),
        CHANNEL_22_BREAKER("Breaker fault on channel 22", FaultType.ERROR),
        CHANNEL_23_BREAKER("Breaker fault on channel 23", FaultType.ERROR);

        private final String description;
        private final FaultType type;

        PDFaults(String description, FaultType type) {
            this.description = description;
            this.type = type;
        }

        public String getDescription() {
            return description;
        }

        public FaultType getType() {
            return type;
        }

        /**
         * Checks if the fault is present in the given PowerDistributionFaults object.
         *
         * @param faults The PowerDistributionFaults object containing current faults.
         * @return True if the fault is present, false otherwise.
         */
        public boolean isPresent(PowerDistributionFaults faults) {
            switch (this) {
                case BROWN_OUT:
                    return faults.Brownout;
                case CAN_WARNING:
                    return faults.CanWarning;
                case HARDWARE:
                    return faults.HardwareFault;
                case CHANNEL_0_BREAKER:
                    return faults.Channel0BreakerFault;
                case CHANNEL_1_BREAKER:
                    return faults.Channel1BreakerFault;
                case CHANNEL_2_BREAKER:
                    return faults.Channel2BreakerFault;
                case CHANNEL_3_BREAKER:
                    return faults.Channel3BreakerFault;
                case CHANNEL_4_BREAKER:
                    return faults.Channel4BreakerFault;
                case CHANNEL_5_BREAKER:
                    return faults.Channel5BreakerFault;
                case CHANNEL_6_BREAKER:
                    return faults.Channel6BreakerFault;
                case CHANNEL_7_BREAKER:
                    return faults.Channel7BreakerFault;
                case CHANNEL_8_BREAKER:
                    return faults.Channel8BreakerFault;
                case CHANNEL_9_BREAKER:
                    return faults.Channel9BreakerFault;
                case CHANNEL_10_BREAKER:
                    return faults.Channel10BreakerFault;
                case CHANNEL_11_BREAKER:
                    return faults.Channel11BreakerFault;
                case CHANNEL_12_BREAKER:
                    return faults.Channel12BreakerFault;
                case CHANNEL_13_BREAKER:
                    return faults.Channel13BreakerFault;
                case CHANNEL_14_BREAKER:
                    return faults.Channel14BreakerFault;
                case CHANNEL_15_BREAKER:
                    return faults.Channel15BreakerFault;
                case CHANNEL_16_BREAKER:
                    return faults.Channel16BreakerFault;
                case CHANNEL_17_BREAKER:
                    return faults.Channel17BreakerFault;
                case CHANNEL_18_BREAKER:
                    return faults.Channel18BreakerFault;
                case CHANNEL_19_BREAKER:
                    return faults.Channel19BreakerFault;
                case CHANNEL_20_BREAKER:
                    return faults.Channel20BreakerFault;
                case CHANNEL_21_BREAKER:
                    return faults.Channel21BreakerFault;
                case CHANNEL_22_BREAKER:
                    return faults.Channel22BreakerFault;
                case CHANNEL_23_BREAKER:
                    return faults.Channel23BreakerFault;
                default:
                    return false;
            }
        }
    }
    
    /**
     * Enum representing STICKY faults from the Power Distribution Hub (PDH).
     */
    public enum PDStickyFaults {
        BROWN_OUT("Brownout Fault", FaultType.ERROR),
        CAN_BUS_OFF("Bus Off event Fault", FaultType.ERROR),
        CAN_WARNING("CAN Warning Fault", FaultType.ERROR),
        HARDWARE("Hardware Fault", FaultType.ERROR),
        FIRMWARE("Firmware Fault", FaultType.ERROR),
        RESET("Device rebooted", FaultType.WARNING),
        CHANNEL_0_BREAKER("Breaker fault on channel 0", FaultType.ERROR),
        CHANNEL_1_BREAKER("Breaker fault on channel 1", FaultType.ERROR),
        CHANNEL_2_BREAKER("Breaker fault on channel 2", FaultType.ERROR),
        CHANNEL_3_BREAKER("Breaker fault on channel 3", FaultType.ERROR),
        CHANNEL_4_BREAKER("Breaker fault on channel 4", FaultType.ERROR),
        CHANNEL_5_BREAKER("Breaker fault on channel 5", FaultType.ERROR),
        CHANNEL_6_BREAKER("Breaker fault on channel 6", FaultType.ERROR),
        CHANNEL_7_BREAKER("Breaker fault on channel 7", FaultType.ERROR),
        CHANNEL_8_BREAKER("Breaker fault on channel 8", FaultType.ERROR),
        CHANNEL_9_BREAKER("Breaker fault on channel 9", FaultType.ERROR),
        CHANNEL_10_BREAKER("Breaker fault on channel 10", FaultType.ERROR),
        CHANNEL_11_BREAKER("Breaker fault on channel 11", FaultType.ERROR),
        CHANNEL_12_BREAKER("Breaker fault on channel 12", FaultType.ERROR),
        CHANNEL_13_BREAKER("Breaker fault on channel 13", FaultType.ERROR),
        CHANNEL_14_BREAKER("Breaker fault on channel 14", FaultType.ERROR),
        CHANNEL_15_BREAKER("Breaker fault on channel 15", FaultType.ERROR),
        CHANNEL_16_BREAKER("Breaker fault on channel 16", FaultType.ERROR),
        CHANNEL_17_BREAKER("Breaker fault on channel 17", FaultType.ERROR),
        CHANNEL_18_BREAKER("Breaker fault on channel 18", FaultType.ERROR),
        CHANNEL_19_BREAKER("Breaker fault on channel 19", FaultType.ERROR),
        CHANNEL_20_BREAKER("Breaker fault on channel 20", FaultType.ERROR),
        CHANNEL_21_BREAKER("Breaker fault on channel 21", FaultType.ERROR),
        CHANNEL_22_BREAKER("Breaker fault on channel 22", FaultType.ERROR),
        CHANNEL_23_BREAKER("Breaker fault on channel 23", FaultType.ERROR);

        private final String description;
        private final FaultType type;

        PDStickyFaults(String description, FaultType type) {
            this.description = description;
            this.type = type;
        }

        public String getDescription() {
            return description;
        }

        public FaultType getType() {
            return type;
        }

        /**
         * Checks if the fault is present in the given PowerDistributionFaults object.
         *
         * @param faults The PowerDistributionFaults object containing current faults.
         * @return True if the fault is present, false otherwise.
         */
        public boolean isPresent(PowerDistributionStickyFaults faults) {
            switch (this) {
                case BROWN_OUT:
                    return faults.Brownout;
                case CAN_WARNING:
                    return faults.CanWarning;
                case HARDWARE:
                    return faults.HardwareFault;
                case FIRMWARE:
                    return faults.FirmwareFault;
                case RESET:
                    return faults.HasReset;
                case CHANNEL_0_BREAKER:
                    return faults.Channel0BreakerFault;
                case CHANNEL_1_BREAKER:
                    return faults.Channel1BreakerFault;
                case CHANNEL_2_BREAKER:
                    return faults.Channel2BreakerFault;
                case CHANNEL_3_BREAKER:
                    return faults.Channel3BreakerFault;
                case CHANNEL_4_BREAKER:
                    return faults.Channel4BreakerFault;
                case CHANNEL_5_BREAKER:
                    return faults.Channel5BreakerFault;
                case CHANNEL_6_BREAKER:
                    return faults.Channel6BreakerFault;
                case CHANNEL_7_BREAKER:
                    return faults.Channel7BreakerFault;
                case CHANNEL_8_BREAKER:
                    return faults.Channel8BreakerFault;
                case CHANNEL_9_BREAKER:
                    return faults.Channel9BreakerFault;
                case CHANNEL_10_BREAKER:
                    return faults.Channel10BreakerFault;
                case CHANNEL_11_BREAKER:
                    return faults.Channel11BreakerFault;
                case CHANNEL_12_BREAKER:
                    return faults.Channel12BreakerFault;
                case CHANNEL_13_BREAKER:
                    return faults.Channel13BreakerFault;
                case CHANNEL_14_BREAKER:
                    return faults.Channel14BreakerFault;
                case CHANNEL_15_BREAKER:
                    return faults.Channel15BreakerFault;
                case CHANNEL_16_BREAKER:
                    return faults.Channel16BreakerFault;
                case CHANNEL_17_BREAKER:
                    return faults.Channel17BreakerFault;
                case CHANNEL_18_BREAKER:
                    return faults.Channel18BreakerFault;
                case CHANNEL_19_BREAKER:
                    return faults.Channel19BreakerFault;
                case CHANNEL_20_BREAKER:
                    return faults.Channel20BreakerFault;
                case CHANNEL_21_BREAKER:
                    return faults.Channel21BreakerFault;
                case CHANNEL_22_BREAKER:
                    return faults.Channel22BreakerFault;
                case CHANNEL_23_BREAKER:
                    return faults.Channel23BreakerFault;
                default:
                    return false;
            }
        }
    }
}
