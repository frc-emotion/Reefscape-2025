package frc.robot.subsystems.arm;

import static edu.wpi.first.units.Units.Amps;
import static edu.wpi.first.units.Units.Degrees;
import static edu.wpi.first.units.Units.Second;
import static edu.wpi.first.units.Units.Volts;

import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;

import edu.wpi.first.math.controller.ArmFeedforward;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.system.plant.DCMotor;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.constants.PortMap;
import frc.robot.constants.subsystems.ArmConstants;
import yams.gearing.GearBox;
import yams.gearing.MechanismGearing;
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

    public ArmSubsystem() {
        armMotor = new SparkMax(PortMap.CANID.ARM_ANGLE.getId(), MotorType.kBrushless);

        motorConfig = new SmartMotorControllerConfig(this)
                .withControlMode(ControlMode.CLOSED_LOOP)
                // Real robot PID
                .withClosedLoopController(
                        ArmConstants.kP,
                        ArmConstants.kI,
                        ArmConstants.kD,
                        ArmConstants.MAX_VELOCITY,
                        ArmConstants.MAX_ACCELERATION)
                // Simulation PID (can be different from real)
                .withSimClosedLoopController(
                        ArmConstants.kP,
                        ArmConstants.kI,
                        ArmConstants.kD,
                        ArmConstants.MAX_VELOCITY,
                        ArmConstants.MAX_ACCELERATION)
                // Real robot feedforward
                .withFeedforward(new ArmFeedforward(
                        ArmConstants.kS,
                        ArmConstants.kG,
                        ArmConstants.kV,
                        ArmConstants.kA))
                // Simulation feedforward
                .withSimFeedforward(new ArmFeedforward(
                        ArmConstants.kS,
                        ArmConstants.kG,
                        ArmConstants.kV,
                        ArmConstants.kA))
                .withTelemetry("ArmMotor", TelemetryVerbosity.HIGH)
                // Gearing from the motor rotor to final shaft
                .withGearing(new MechanismGearing(GearBox.fromReductionStages(
                        ArmConstants.GEAR_RATIO_STAGE_1,
                        ArmConstants.GEAR_RATIO_STAGE_2)))
                .withMotorInverted(true) // CHANGED: Invert motor to fix coordinate system
                .withIdleMode(MotorMode.BRAKE)
                .withStatorCurrentLimit(Amps.of(ArmConstants.kSmartCurrentLimit))
                .withClosedLoopRampRate(ArmConstants.CLOSED_LOOP_RAMP_RATE)
                .withOpenLoopRampRate(ArmConstants.OPEN_LOOP_RAMP_RATE);

        motor = new SparkWrapper(armMotor, DCMotor.getNEO(1), motorConfig);

        robotToMechanism = new MechanismPositionConfig()
                .withMaxRobotHeight(ArmConstants.MAX_ROBOT_HEIGHT)
                .withMaxRobotLength(ArmConstants.MAX_ROBOT_LENGTH)
                .withRelativePosition(ArmConstants.ARM_PIVOT_POSITION);

        armConfig = new ArmConfig(motor)
                .withLength(ArmConstants.ARM_LENGTH)
                .withHardLimit(Degrees.of(ArmConstants.kMinRotation), Degrees.of(ArmConstants.kMaxRotation))
                // SOFT LIMITS: Add 10° buffer above hopper and 10° below max to prevent crashes
                .withSoftLimits(Degrees.of(10), Degrees.of(195))
                .withTelemetry("ArmExample", TelemetryVerbosity.HIGH)
                .withMass(ArmConstants.ARM_MASS)
                .withStartingPosition(ArmConstants.ARM_STARTING_POSITION)
                .withMechanismPositionConfig(robotToMechanism);

        arm = new Arm(armConfig);

        // Publish PID values to SmartDashboard for monitoring
        SmartDashboard.putNumber("Arm PID kP", ArmConstants.kP);
        SmartDashboard.putNumber("Arm PID kI", ArmConstants.kI);
        SmartDashboard.putNumber("Arm PID kD", ArmConstants.kD);

        // Publish feedforward values to SmartDashboard for monitoring
        SmartDashboard.putNumber("Arm FF kS", ArmConstants.kS);
        SmartDashboard.putNumber("Arm FF kG", ArmConstants.kG);
        SmartDashboard.putNumber("Arm FF kV", ArmConstants.kV);
        SmartDashboard.putNumber("Arm FF kA", ArmConstants.kA);
    }

    public Command armCmd(double dutycycle) {
        return arm.set(dutycycle);
    }

    public Command sysId() {
        return arm.sysId(
                ArmConstants.SYSID_STEP_VOLTAGE,
                Volts.of(ArmConstants.SYSID_RAMP_RATE_VALUE).per(Second),
                ArmConstants.SYSID_TIMEOUT);
    }

    public Command setAngle(Angle angle) {
        return arm.setAngle(angle);
    }

    public Arm getArm() {
        return arm;
    }

    /**
     * Gets the current arm angle.
     * Zero (0°) = Arm resting on hopper
     * Negative = Arm extended forward (toward bumper)
     * Positive = Arm rotated back/up
     */
    public Rotation2d getCurrentAngle() {
        // Get current angle from YAMS arm mechanism
        return Rotation2d.fromDegrees(arm.getAngle().in(Degrees));
    }

    /**
     * Gets the current draw of the arm motor.
     * 
     * @return Current in amps
     */
    public double getMotorCurrent() {
        return armMotor.getOutputCurrent();
    }

    @Override
    public void periodic() {
        // YAMS handles telemetry automatically
        arm.updateTelemetry();

        // Note: YAMS doesn't support runtime PID/FF updates
        // Values must be changed in Constants files and redeployed
        // Telemetry is published automatically by YAMS with TelemetryVerbosity.HIGH
    }

    @Override
    public void simulationPeriodic() {
        arm.simIterate();
    }
}
