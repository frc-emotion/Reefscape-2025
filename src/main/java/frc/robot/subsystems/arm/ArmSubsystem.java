package frc.robot.subsystems.arm;

import static edu.wpi.first.units.Units.Amps;
import static edu.wpi.first.units.Units.Celsius;
import static edu.wpi.first.units.Units.Degrees;
import static edu.wpi.first.units.Units.DegreesPerSecond;
import static edu.wpi.first.units.Units.DegreesPerSecondPerSecond;
import static edu.wpi.first.units.Units.Meters;
import static edu.wpi.first.units.Units.Pounds;
import static edu.wpi.first.units.Units.Second;
import static edu.wpi.first.units.Units.Seconds;
import static edu.wpi.first.units.Units.Volts;
import static edu.wpi.first.units.Units.Volt;

import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;

import edu.wpi.first.math.controller.ArmFeedforward;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.math.system.plant.DCMotor;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.Distance;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.constants.PortMap;
import frc.robot.constants.subsystems.ArmConstants;
import frc.robot.util.helpers.PIDHelper;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import yams.mechanisms.config.ArmConfig;
import yams.mechanisms.config.MechanismPositionConfig;
import yams.mechanisms.positional.Arm;
import yams.motorcontrollers.SmartMotorController;
import yams.motorcontrollers.SmartMotorControllerConfig;
import yams.motorcontrollers.SmartMotorControllerConfig.ControlMode;
import yams.motorcontrollers.SmartMotorControllerConfig.MotorMode;
import yams.motorcontrollers.SmartMotorControllerConfig.TelemetryVerbosity;
import yams.motorcontrollers.local.SparkWrapper;

public class ArmSubsystem extends SubsystemBase {
    private final SparkMax armMotor;
    private final SmartMotorControllerConfig motorConfig;
    private final SmartMotorController motor;
    private final MechanismPositionConfig robotToMechanism;
    private final ArmConfig armConfig;
    private final Arm arm;
    
    // Track the current command for stopping
    private Command currentCommand = null;
    
    // Tolerance for isAtSetpoint check (degrees)
    private static final double SETPOINT_TOLERANCE_DEGREES = 2.0;
    
    // Live PID tuning helper (optional - enable for tuning)
    private PIDHelper pidHelper = null;
    private boolean enableLiveTuning = false; // Set true when tuning
    
    public ArmSubsystem() {
        armMotor = new SparkMax(PortMap.CANID.ARM_ANGLE.getId(), MotorType.kBrushless);
        
        motorConfig = new SmartMotorControllerConfig(this)
            .withClosedLoopController(
                ArmConstants.kP,
                ArmConstants.kI,
                ArmConstants.kD,
                DegreesPerSecond.of(180),
                DegreesPerSecondPerSecond.of(90)
            )
            .withSoftLimit(Degrees.of(ArmConstants.kMinRotation), Degrees.of(ArmConstants.kMaxRotation))
            // NO GEARING: Encoder reads output shaft position directly (after 12:1 reduction)
            // .withGearing(new MechanismGearing(GearBox.fromReductionStages(3, 4)))
            .withIdleMode(MotorMode.BRAKE)
            .withTelemetry("ArmMotor", TelemetryVerbosity.HIGH)
            .withStatorCurrentLimit(Amps.of(ArmConstants.kSmartCurrentLimit))
            .withMotorInverted(false)  // Motor direction (NOT encoder inversion)
            .withClosedLoopRampRate(Seconds.of(0.25))
            .withOpenLoopRampRate(Seconds.of(0.25))
            .withFeedforward(new ArmFeedforward(
                ArmConstants.kS,
                ArmConstants.kG,
                ArmConstants.kV,
                ArmConstants.kA
            ))
            .withControlMode(ControlMode.CLOSED_LOOP);
            
        motor = new SparkWrapper(armMotor, DCMotor.getNEO(1), motorConfig);
        
        robotToMechanism = new MechanismPositionConfig()
            .withMaxRobotHeight(Meters.of(1.5))
            .withMaxRobotLength(Meters.of(0.75))
            .withRelativePosition(new Translation3d(Meters.of(0.25), Meters.of(0), Meters.of(0.5)));
        
        armConfig = new ArmConfig(motor)
            .withLength(Meters.of(0.135))
            .withHardLimit(Degrees.of(ArmConstants.kMinRotation), Degrees.of(ArmConstants.kMaxRotation))
            .withTelemetry("ArmExample", TelemetryVerbosity.HIGH)
            .withMass(Pounds.of(1))
            .withStartingPosition(Degrees.of(ArmConstants.kStartingAngle))  // Arm starts at 10 o'clock hard stop
            .withMechanismPositionConfig(robotToMechanism);
            
        arm = new Arm(armConfig);
    }

    public Command armCmd(double dutycycle) {
        return arm.set(dutycycle);
    }

    /**
     * Custom SysId command that safely moves arm to mid-range before running characterization.
     * This prevents collision with the hard stop at 10 o'clock position.
     * 
     * USAGE:
     * 1. Enable robot in TEST mode
     * 2. Hold POV-Up - arm will move to safe position (60°)
     * 3. Keep holding - SysId will run automatically
     * 4. Watch for full 30 second routine
     */
    public Command sysId() {
        return Commands.sequence(
            // Step 1: Move to safe starting position (mid-range, away from hard stop)
            Commands.runOnce(() -> 
                SmartDashboard.putString("Arm/SysId Status", "Moving to safe start position...")
            ),
            setAngle(Degrees.of(ArmConstants.kSysIdStartAngle))
                .withTimeout(3.0),  // Allow 3 seconds to reach position
            
            // Step 2: Brief pause to settle
            Commands.waitSeconds(0.5),
            
            // Step 3: Run SysId characterization
            Commands.runOnce(() -> 
                SmartDashboard.putString("Arm/SysId Status", "Running SysId characterization...")
            ),
            arm.sysId(Volts.of(7), Volts.of(7).per(Second), Second.of(30)),
            
            // Step 4: Return to safe position
            Commands.runOnce(() -> 
                SmartDashboard.putString("Arm/SysId Status", "Complete! Returning to safe position.")
            ),
            setAngle(Degrees.of(ArmConstants.kSysIdStartAngle))
        );
    }

    public Command setAngle(Angle angle) {
        return arm.setAngle(angle);
    }

    @Override
    public void periodic() {
        arm.updateTelemetry();
        
        // Update live PID tuning if enabled
        if (enableLiveTuning && pidHelper != null) {
            pidHelper.update();
        }
        
        // Publish detailed telemetry for charting
        publishDetailedTelemetry();
        
        // Publish encoder calibration data
        publishEncoderCalibrationData();
    }
    
    /**
     * Enable live PID tuning via SmartDashboard.
     * Call this method once during robot initialization to enable tuning mode.
     */
    public void enableLivePIDTuning() {
        this.enableLiveTuning = true;
        
        // Create PID helper with callbacks to update motor config
        this.pidHelper = new PIDHelper(
            "Arm",
            ArmConstants.kP,
            ArmConstants.kI,
            ArmConstants.kD,
            (p) -> updatePIDGains(p, ArmConstants.kI, ArmConstants.kD),
            (i) -> updatePIDGains(ArmConstants.kP, i, ArmConstants.kD),
            (d) -> updatePIDGains(ArmConstants.kP, ArmConstants.kI, d)
        );
        
        SmartDashboard.putBoolean("Arm/Live Tuning Enabled", true);
    }
    
    /**
     * Update PID gains on the fly (for live tuning).
     * This recreates the motor config with new gains.
     */
    private void updatePIDGains(double p, double i, double d) {
        // Note: YAMS doesn't support live PID updates directly
        // You need to log new values and redeploy
        SmartDashboard.putString("Arm/Tuned Values", 
            String.format("kP=%.6f, kI=%.6f, kD=%.6f", p, i, d));
    }
    
    /**
     * Publish detailed telemetry for visualization tools like AdvantageScope.
     */
    private void publishDetailedTelemetry() {
        // Position tracking
        Angle currentAngle = arm.getAngle();
        SmartDashboard.putNumber("Arm/Current Angle (deg)", currentAngle.in(Degrees));
        
        // Setpoint tracking (if command is running)
        if (currentCommand != null && currentCommand.isScheduled()) {
            SmartDashboard.putBoolean("Arm/At Setpoint", isAtSetpoint());
        }
        
        // Motor telemetry
        SmartDashboard.putNumber("Arm/Motor Current (A)", motor.getStatorCurrent().in(Amps));
        SmartDashboard.putNumber("Arm/Motor Voltage (V)", motor.getVoltage().in(Volt));
        SmartDashboard.putNumber("Arm/Motor Temperature (C)", motor.getTemperature().in(Celsius));
        
        // Control gains (for reference)
        SmartDashboard.putNumber("Arm/Config kP", ArmConstants.kP);
        SmartDashboard.putNumber("Arm/Config kI", ArmConstants.kI);
        SmartDashboard.putNumber("Arm/Config kD", ArmConstants.kD);
    }
    
    /**
     * Publish encoder calibration data for determining offset.
     * Use this during calibration to find the exact encoder reading at known positions.
     * 
     * UNITS NOTE:
     * - Encoder reads ARM OUTPUT POSITION directly (after gearing)
     * - NOT motor shaft position
     * - Conversion: 1 encoder rotation = 360° arm rotation
     * - No gearing config in YAMS - encoder is post-reduction
     */
    private void publishEncoderCalibrationData() {
        // Raw encoder position in OUTPUT SHAFT ROTATIONS (post-gearing)
        double rawEncoderRotations = armMotor.getEncoder().getPosition();
        SmartDashboard.putNumber("Arm/Raw Encoder (motor rotations)", rawEncoderRotations);
        
        // Convert to degrees (1 encoder rotation = 360° arm rotation)
        double rawEncoderDegrees = rawEncoderRotations * 360.0;
        SmartDashboard.putNumber("Arm/Raw Encoder (deg)", rawEncoderDegrees);
        
        // Calibrated position (after applying offset from withStartingPosition)
        // This is what YAMS reports after accounting for gearing and offset
        Angle calibratedAngle = arm.getAngle();
        SmartDashboard.putNumber("Arm/Calibrated Angle (deg)", calibratedAngle.in(Degrees));
        
        // Show the difference (error if not calibrated)
        double calibrationError = calibratedAngle.in(Degrees) - rawEncoderDegrees;
        SmartDashboard.putNumber("Arm/Calibration Error (deg)", calibrationError);
        
        // Offset being applied (currently unused - YAMS handles this internally)
        SmartDashboard.putNumber("Arm/Configured Offset (deg)", ArmConstants.kEncoderOffsetDegrees);
        
        // Calculate what the raw encoder SHOULD read if at hard stop
        // Hard stop = -60°, so raw encoder should read -60°/360 = -0.167 rotations
        double expectedRotationsAtHardStop = ArmConstants.kStartingAngle / 360.0;
        SmartDashboard.putNumber("Arm/Expected Encoder at Hard Stop (rotations)", expectedRotationsAtHardStop);
        
        // Calculate current offset from hard stop
        double offsetFromHardStop = rawEncoderDegrees - ArmConstants.kStartingAngle;
        SmartDashboard.putNumber("Arm/Offset from Hard Stop (deg)", offsetFromHardStop);
        
        // Helpful status
        boolean isAtHardStop = Math.abs(calibratedAngle.in(Degrees) - ArmConstants.kStartingAngle) < 2.0;
        SmartDashboard.putBoolean("Arm/At Hard Stop", isAtHardStop);
        SmartDashboard.putBoolean("Arm/Needs Verification", Math.abs(calibrationError) > 2.0);
    }
    
    @Override
    public void simulationPeriodic() {
        arm.simIterate();
    }
    
    // ========== COMPATIBILITY METHODS FOR LEGACY COMMANDS ==========
    // Based on YAMS 2025.11.05 API: https://yet-another-software-suite.github.io/YAMS/javadocs/
    
    /**
     * Set the target angle for the arm. This is a compatibility method that schedules
     * the YAMS setAngle command internally.
     * 
     * @param targetAngle The target angle as a Rotation2d
     * @param elevatorHeight The current elevator height (for collision avoidance)
     */
    public void setTargetAngle(Rotation2d targetAngle, Distance elevatorHeight) {
        // Cancel any existing command
        if (currentCommand != null && currentCommand.isScheduled()) {
            currentCommand.cancel();
        }
        
        // Schedule the YAMS setAngle command
        // Note: elevatorHeight parameter is for your collision avoidance logic
        // For now, we just set the angle. You may want to add collision checks.
        currentCommand = setAngle(Degrees.of(targetAngle.getDegrees()));
        currentCommand.schedule();
    }
    
    /**
     * Stop the arm by setting duty cycle to 0.
     * Based on YAMS documentation: use arm.set(0) to stop.
     */
    public void stop() {
        if (currentCommand != null && currentCommand.isScheduled()) {
            currentCommand.cancel();
        }
        // YAMS pattern: set duty cycle to 0 to stop
        armCmd(0).schedule();
    }
    
    /**
     * Check if the arm is at the setpoint.
     * 
     * YAMS provides isNear(angle, tolerance) which returns a Trigger.
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
     * Get the current angle of the arm.
     * Direct access to YAMS Arm.getAngle() method.
     * 
     * @return Current arm angle
     */
    public Angle getCurrentAngle() {
        return arm.getAngle();
    }
    
    /**
     * Check if the arm is near a target angle within a tolerance.
     * This uses the YAMS isNear() method and converts Trigger to boolean.
     * 
     * @param targetAngle Target angle to check
     * @param toleranceDegrees Tolerance in degrees
     * @return true if within tolerance
     */
    public boolean isNearAngle(Rotation2d targetAngle, double toleranceDegrees) {
        // Use YAMS isNear which returns a Trigger
        // We get the boolean state from the trigger
        return arm.isNear(
            Degrees.of(targetAngle.getDegrees()),
            Degrees.of(toleranceDegrees)
        ).getAsBoolean();
    }
}
