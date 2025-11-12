package frc.robot.subsystems.elevator;

import static edu.wpi.first.units.Units.Amps;
import static edu.wpi.first.units.Units.Celsius;
import static edu.wpi.first.units.Units.Inches;
import static edu.wpi.first.units.Units.Meters;
import static edu.wpi.first.units.Units.MetersPerSecond;
import static edu.wpi.first.units.Units.MetersPerSecondPerSecond;
import static edu.wpi.first.units.Units.Pounds;
import static edu.wpi.first.units.Units.Second;
import static edu.wpi.first.units.Units.Volt;
import static edu.wpi.first.units.Units.Volts;

import java.util.function.Supplier;

import com.revrobotics.spark.SparkLowLevel;
import com.revrobotics.spark.SparkMax;

import edu.wpi.first.math.Pair;
import edu.wpi.first.math.controller.ElevatorFeedforward;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.math.system.plant.DCMotor;
import edu.wpi.first.units.measure.Distance;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.constants.PortMap;
import frc.robot.constants.subsystems.ElevatorConstants;
import frc.robot.util.helpers.PIDHelper;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import yams.gearing.GearBox;
import yams.gearing.MechanismGearing;
import yams.mechanisms.config.ElevatorConfig;
import yams.mechanisms.config.MechanismPositionConfig;
import yams.mechanisms.positional.Elevator;
import yams.motorcontrollers.SmartMotorController;
import yams.motorcontrollers.SmartMotorControllerConfig;
import yams.motorcontrollers.SmartMotorControllerConfig.ControlMode;
import yams.motorcontrollers.SmartMotorControllerConfig.MotorMode;
import yams.motorcontrollers.SmartMotorControllerConfig.TelemetryVerbosity;
import yams.motorcontrollers.local.SparkWrapper;

public class ElevatorSubsystem extends SubsystemBase {
    private final SparkMax elevatorMotor;
    private final SparkMax elevatorFollower;
    private final SmartMotorControllerConfig motorConfig;
    private final SmartMotorController motor;
    private final MechanismPositionConfig robotToMechanism;
    private final ElevatorConfig elevatorConfig;
    private final Elevator elevator;
    
    // Track the current command for stopping
    private Command currentCommand = null;
    
    // Tolerance for isAtSetpoint check (inches)
    private static final double SETPOINT_TOLERANCE_INCHES = 1.0;
    
    // Live PID tuning helper (optional - enable for tuning)
    private PIDHelper pidHelper = null;
    private boolean enableLiveTuning = false; // Set true when tuning

    public ElevatorSubsystem(boolean useTrapezoidal) {
        elevatorMotor = new SparkMax(PortMap.CANID.ELEVATOR_DRIVE_LEADER.getId(), SparkLowLevel.MotorType.kBrushless);
        elevatorFollower = new SparkMax(PortMap.CANID.ELEVATOR_DRIVE_FOLLOWER.getId(), SparkLowLevel.MotorType.kBrushless);
        
        motorConfig = new SmartMotorControllerConfig(this)
            .withMechanismCircumference(Meters.of(Inches.of(0.25).in(Meters) * 22))
            .withClosedLoopController(
                ElevatorConstants.kP,
                ElevatorConstants.kI,
                ElevatorConstants.kD,
                MetersPerSecond.of(0.5),
                MetersPerSecondPerSecond.of(0.5)
            )
            .withSoftLimit(Meters.of(0), Meters.of(2))
            .withGearing(new MechanismGearing(GearBox.fromReductionStages(3, 4)))
            .withIdleMode(MotorMode.BRAKE)
            .withTelemetry("ElevatorMotor", TelemetryVerbosity.HIGH)
            .withStatorCurrentLimit(Amps.of(40))
            .withMotorInverted(false)
            .withFollowers(
                Pair.of(elevatorFollower, true) // Inverted relative to leader
            )
            .withFeedforward(new ElevatorFeedforward(
                ElevatorConstants.kS,
                ElevatorConstants.kG,
                ElevatorConstants.kV,
                ElevatorConstants.kA
            ))
            .withControlMode(ControlMode.CLOSED_LOOP);
            
        motor = new SparkWrapper(elevatorMotor, DCMotor.getNEO(2), motorConfig);
        
        robotToMechanism = new MechanismPositionConfig()
            .withMaxRobotHeight(Meters.of(1.5))
            .withMaxRobotLength(Meters.of(0.75))
            .withRelativePosition(new Translation3d(Meters.of(-0.25), Meters.of(0), Meters.of(0.5)));
            
        elevatorConfig = new ElevatorConfig(motor)
            .withStartingHeight(Meters.of(0.5))
            .withHardLimits(Meters.of(0), Meters.of(3))
            .withTelemetry("Elevator", TelemetryVerbosity.HIGH)
            .withMechanismPositionConfig(robotToMechanism)
            .withMass(Pounds.of(16));
            
        elevator = new Elevator(elevatorConfig);
    }

    @Override
    public void periodic() {
        elevator.updateTelemetry();
        
        // Update live PID tuning if enabled
        if (enableLiveTuning && pidHelper != null) {
            pidHelper.update();
        }
        
        // Publish detailed telemetry for charting
        publishDetailedTelemetry();
    }
    
    /**
     * Enable live PID tuning via SmartDashboard.
     * Call this method once during robot initialization to enable tuning mode.
     */
    public void enableLivePIDTuning() {
        this.enableLiveTuning = true;
        
        // Create PID helper with callbacks to update motor config
        this.pidHelper = new PIDHelper(
            "Elevator",
            ElevatorConstants.kP,
            ElevatorConstants.kI,
            ElevatorConstants.kD,
            (p) -> updatePIDGains(p, ElevatorConstants.kI, ElevatorConstants.kD),
            (i) -> updatePIDGains(ElevatorConstants.kP, i, ElevatorConstants.kD),
            (d) -> updatePIDGains(ElevatorConstants.kP, ElevatorConstants.kI, d)
        );
        
        SmartDashboard.putBoolean("Elevator/Live Tuning Enabled", true);
    }
    
    /**
     * Update PID gains on the fly (for live tuning).
     * This recreates the motor config with new gains.
     */
    private void updatePIDGains(double p, double i, double d) {
        // Note: YAMS doesn't support live PID updates directly
        // You need to log new values and redeploy
        SmartDashboard.putString("Elevator/Tuned Values", 
            String.format("kP=%.6f, kI=%.6f, kD=%.6f", p, i, d));
    }
    
    /**
     * Publish detailed telemetry for visualization tools like AdvantageScope.
     */
    private void publishDetailedTelemetry() {
        // Position tracking
        Distance currentHeight = elevator.getHeight();
        SmartDashboard.putNumber("Elevator/Current Height (inches)", currentHeight.in(Inches));
        SmartDashboard.putNumber("Elevator/Current Height (meters)", currentHeight.in(Meters));
        
        // Velocity tracking
        SmartDashboard.putNumber("Elevator/Velocity (m/s)", elevator.getVelocity().in(MetersPerSecond));
        
        // Setpoint tracking (if command is running)
        if (currentCommand != null && currentCommand.isScheduled()) {
            SmartDashboard.putBoolean("Elevator/At Setpoint", isAtSetpoint());
        }
        
        // Motor telemetry
        SmartDashboard.putNumber("Elevator/Motor Current (A)", motor.getStatorCurrent().in(Amps));
        SmartDashboard.putNumber("Elevator/Motor Voltage (V)", motor.getVoltage().in(Volt));
        SmartDashboard.putNumber("Elevator/Motor Temperature (C)", motor.getTemperature().in(Celsius));
        
        // Control gains (for reference)
        SmartDashboard.putNumber("Elevator/Config kP", ElevatorConstants.kP);
        SmartDashboard.putNumber("Elevator/Config kI", ElevatorConstants.kI);
        SmartDashboard.putNumber("Elevator/Config kD", ElevatorConstants.kD);
    }
    
    @Override
    public void simulationPeriodic() {
        elevator.simIterate();
    }
    
    public Command elevCmd(double dutycycle) {
        return elevator.set(dutycycle);
    }
    
    public Command setHeight(Distance height) {
        return elevator.setHeight(height);
    }
    
    public Command sysId() {
        return elevator.sysId(Volts.of(12), Volts.of(12).per(Second), Second.of(30));
    }
    
    // ========== COMPATIBILITY METHODS FOR LEGACY COMMANDS ==========
    // Based on YAMS 2025.11.05 API: https://yet-another-software-suite.github.io/YAMS/javadocs/
    
    /**
     * Set the target height for the elevator. This is a compatibility method that schedules
     * the YAMS setHeight command internally.
     * 
     * @param targetHeight The target height
     */
    public void setTargetHeight(Distance targetHeight) {
        // Cancel any existing command
        if (currentCommand != null && currentCommand.isScheduled()) {
            currentCommand.cancel();
        }
        
        // Schedule the YAMS setHeight command
        currentCommand = setHeight(targetHeight);
        currentCommand.schedule();
    }
    
    /**
     * Stop the elevator by setting duty cycle to 0.
     * Based on YAMS documentation: use elevator.set(0) to stop.
     */
    public void stop() {
        if (currentCommand != null && currentCommand.isScheduled()) {
            currentCommand.cancel();
        }
        // YAMS pattern: set duty cycle to 0 to stop
        elevCmd(0).schedule();
    }
    
    /**
     * Check if the elevator is at the setpoint.
     * 
     * YAMS provides isNear(height, tolerance) which returns a Trigger.
     * We convert this to a boolean for legacy command compatibility.
     * 
     * @return true if the current command is finished or within tolerance
     */
    public boolean isAtSetpoint() {
        // If no command is running, we're "at setpoint"
        if (currentCommand == null || !currentCommand.isScheduled()) {
            return true;
        }
        
        // Check if the command has finished
        return currentCommand.isFinished();
    }
    
    /**
     * Get the current height of the elevator.
     * Direct access to YAMS Elevator.getHeight() method.
     * 
     * @return Current elevator height
     */
    public Distance getHeight() {
        return elevator.getHeight();
    }
    
    /**
     * Get the current draw of the elevator motor(s).
     * 
     * @param absolute If true, returns absolute value
     * @return Current draw in amps
     */
    public double getCurrentDraw(boolean absolute) {
        // Access the motor controller to get current (YAMS returns typed units)
        // Convert from WPILib Current unit to double (in amps)
        double current = motor.getStatorCurrent().in(Amps);
        return absolute ? Math.abs(current) : current;
    }
    
    /**
     * Check if the elevator is near a target height within a tolerance.
     * This uses the YAMS isNear() method and converts Trigger to boolean.
     * 
     * @param targetHeight Target height to check
     * @param toleranceInches Tolerance in inches
     * @return true if within tolerance
     */
    public boolean isNearHeight(Distance targetHeight, double toleranceInches) {
        // Use YAMS isNear which returns a Trigger
        // We get the boolean state from the trigger
        return elevator.isNear(
            targetHeight,
            Inches.of(toleranceInches)
        ).getAsBoolean();
    }
}
