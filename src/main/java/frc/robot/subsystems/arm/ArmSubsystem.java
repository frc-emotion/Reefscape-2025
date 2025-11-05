package frc.robot.subsystems.arm;

import static edu.wpi.first.units.Units.Degrees;
import static edu.wpi.first.units.Units.DegreesPerSecond;
import static edu.wpi.first.units.Units.DegreesPerSecondPerSecond;
import static edu.wpi.first.units.Units.Meters;
import static edu.wpi.first.units.Units.Meter;
import static edu.wpi.first.units.Units.Amps;
import static edu.wpi.first.units.Units.Pounds;
import static edu.wpi.first.units.Units.Seconds;
import static edu.wpi.first.units.Units.Second;
import static edu.wpi.first.units.Units.Volts;

import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;

import edu.wpi.first.epilogue.Logged;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.ArmFeedforward;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.math.system.plant.DCMotor;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.Distance;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.constants.PortMap;
import frc.robot.constants.subsystems.ArmConstants;
import frc.robot.constants.subsystems.ElevatorConstants;
import frc.robot.util.diagnostics.Faults.FaultManager;
import frc.robot.util.diagnostics.Faults.FaultTypes.FaultType;
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

@Logged
public class ArmSubsystem extends SubsystemBase {
    private final SparkMax armMotor;
    private final SmartMotorController motor;
    private final Arm arm;
    
    public ArmSubsystem() {
        // Create raw motor
        armMotor = new SparkMax(PortMap.CANID.ARM_ANGLE.getId(), MotorType.kBrushless);
        armMotor.configure(frc.robot.config.subsystems.ArmConfig.ARM_CONFIG, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
        
        // Configure YAMS SmartMotorController
        motor = new SparkWrapper(
            armMotor,
            DCMotor.getNEO(1),
            new SmartMotorControllerConfig(this)
                .withClosedLoopController(
                    ArmConstants.kP,
                    ArmConstants.kI,
                    ArmConstants.kD,
                    ArmConstants.kMaxVelocity,
                    ArmConstants.kMaxAcceleration
                )
                .withFeedforward(new ArmFeedforward(
                    ArmConstants.kS,
                    ArmConstants.kG,
                    ArmConstants.kV,
                    ArmConstants.kA
                ))
                .withGearing(new MechanismGearing(ArmConstants.gearing))
                .withSoftLimit(
                    Degrees.of(ArmConstants.kMinRotation),
                    Degrees.of(ArmConstants.kMaxRotation)
                )
                .withIdleMode(MotorMode.BRAKE)
                .withTelemetry("Arm", TelemetryVerbosity.HIGH)
                .withStatorCurrentLimit(Amps.of(ArmConstants.kSmartCurrentLimit))
                .withControlMode(ControlMode.CLOSED_LOOP)
                .withClosedLoopRampRate(Seconds.of(0.25))
                .withOpenLoopRampRate(Seconds.of(0.25))
                // .withOffset(ArmConstants.kZeroOffset)
        );
        
        // Create YAMS Arm mechanism with robot positioning
        MechanismPositionConfig robotToMechanism = new MechanismPositionConfig()
            .withMaxRobotHeight(Meters.of(1.98)) // Max legal height
            .withMaxRobotLength(Meters.of(1.22)) // Max extension
            .withRelativePosition(new Translation3d(Meters.of(0.25), Meters.of(0), Meters.of(0.5)));
        
        arm = new Arm(
            new ArmConfig(motor)
                .withLength(Meters.of(0.5)) // TODO: Measure actual arm length
                .withMass(Pounds.of(10)) // TODO: Measure actual arm mass
                .withHardLimit(
                    Degrees.of(ArmConstants.kMinRotation),
                    Degrees.of(ArmConstants.kMaxRotation)
                )
                .withStartingPosition(Degrees.of(0))
                .withTelemetry("ArmMechanism", TelemetryVerbosity.HIGH)
                .withMechanismPositionConfig(robotToMechanism)
        );
                
        FaultManager.register(armMotor);
        safetyChecks();
    }

    /**
     * Sets the target angle for the arm.
     * 
     * @param angle The target angle to rotate to
     * @param elevatorHeight The elevator height 
     */
    public void setTargetAngle(Rotation2d angle) {        
        // Schedule the YAMS command
        arm.setAngle(Degrees.of(angle.getDegrees())).schedule();
    }

    private void safetyChecks() {
        FaultManager.register(
                () -> armMotor.getOutputCurrent() > ArmConstants.NORMAL_OPERATION_CURRENT
                        + ArmConstants.CURRENT_SPIKE_THRESHOLD,
                "Arm Drive",
                "Possible unsafe current spike",
                FaultType.WARNING);

        FaultManager.register(
                () -> armMotor.getMotorTemperature() > ArmConstants.NORMAL_OPERATION_TEMP
                        + ArmConstants.TEMP_SPIKE_THRESHOLD,
                "Arm Drive",
                "Possible unsafe motor temperature",
                FaultType.WARNING);

    }

    /**
     * Sends raw input to the motor
     * 
     * @param power The power input to the motor within [-1, 1].
     */
    public void set(double power) {
        arm.set(power);
    }

    /**
     * Constrains the rotation of the arm depending on the height of the elevator.
     * 
     * @param targetRotation The desired rotation for the arm.
     * @param elevatorHeight The current height of the elevator.
     * @return The new target rotation, taking into account limits.
     */
    public Rotation2d constrain(Rotation2d targetRotation, Distance elevatorHeight) {
        System.out.println(targetRotation);

        if (elevatorHeight.isNear(
                Meter.of(0),
                Meter.of(ArmConstants.kMaxHeightConstrained))) {
            return Rotation2d.fromDegrees(MathUtil.clamp(targetRotation.getDegrees(), ArmConstants.kMinRotationConstrained, ArmConstants.kMaxRotationConstrained ));
        } else {
            if (targetRotation.getDegrees() > ArmConstants.kMaxRotation) {
                return Rotation2d.fromDegrees(ArmConstants.kMaxRotation);
            } else if (targetRotation.getDegrees() < ArmConstants.kMinRotation) {
                return Rotation2d.fromDegrees(ArmConstants.kMinRotation);
            }
        }

        return targetRotation;
    }

    public Angle getTargetRotation() {
        return arm.getAngle();
    }

    public void stop() {
        set(0);
    }

    /**
     * Gets the YAMS SysId routine for characterization
     * Uses simplified API from YAMS example
     */
    public Command getSysIdRoutine() {
        return arm.sysId(Volts.of(3), Volts.of(3).per(Second), Second.of(30));
    }

    @Override
    public void periodic() {
        // YAMS handles telemetry automatically
        arm.updateTelemetry();
    }
    
    @Override
    public void simulationPeriodic() {
        // YAMS handles physics simulation
        arm.simIterate();
    }

}
