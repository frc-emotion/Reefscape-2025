package frc.robot.subsystems.arm;

import static edu.wpi.first.units.Units.Amps;
import static edu.wpi.first.units.Units.Degrees;
import static edu.wpi.first.units.Units.DegreesPerSecond;
import static edu.wpi.first.units.Units.DegreesPerSecondPerSecond;
import static edu.wpi.first.units.Units.Meters;
import static edu.wpi.first.units.Units.Pounds;
import static edu.wpi.first.units.Units.Second;
import static edu.wpi.first.units.Units.Seconds;
import static edu.wpi.first.units.Units.Volts;

import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;

import edu.wpi.first.math.controller.ArmFeedforward;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.math.system.plant.DCMotor;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
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
            .withClosedLoopController(
                ArmConstants.kP,
                ArmConstants.kI,
                ArmConstants.kD,
                DegreesPerSecond.of(180),
                DegreesPerSecondPerSecond.of(90)
            )
            .withSoftLimit(Degrees.of(ArmConstants.kMinRotation), Degrees.of(ArmConstants.kMaxRotation))
            .withGearing(new MechanismGearing(GearBox.fromReductionStages(3, 4)))
            .withIdleMode(MotorMode.BRAKE)
            .withTelemetry("ArmMotor", TelemetryVerbosity.HIGH)
            .withStatorCurrentLimit(Amps.of(ArmConstants.kSmartCurrentLimit))
            .withMotorInverted(false)
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
            .withStartingPosition(Degrees.of(0))
            .withMechanismPositionConfig(robotToMechanism);
            
        arm = new Arm(armConfig);
    }

    public Command armCmd(double dutycycle) {
        return arm.set(dutycycle);
    }

    public Command sysId() {
        return arm.sysId(Volts.of(3), Volts.of(3).per(Second), Second.of(30));
    }

    public Command setAngle(Angle angle) {
        return arm.setAngle(angle);
    }

    @Override
    public void periodic() {
        arm.updateTelemetry();
    }
    
    @Override
    public void simulationPeriodic() {
        arm.simIterate();
    }
}
