package frc.robot.subsystems.arm;

import org.dyn4j.geometry.Rotation;

import com.revrobotics.spark.ClosedLoopSlot;
import com.revrobotics.spark.SparkAbsoluteEncoder;
import com.revrobotics.spark.SparkClosedLoopController;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkMaxConfig;
import com.revrobotics.spark.SparkBase.ControlType;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;

import edu.wpi.first.epilogue.Logged;
import edu.wpi.first.math.controller.ArmFeedforward;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.ArmConstants;
import frc.robot.Constants.ElevatorConstants;
import frc.robot.Constants.Ports;
import frc.robot.util.PIDHelper;
import frc.robot.util.Faults.FaultManager;
import frc.robot.util.Faults.FaultTypes.FaultType;

@Logged
public class ArmSubsystem extends SubsystemBase {
    private final SparkMax armMotor;

    private final SparkAbsoluteEncoder armEncoder;

    private final SparkClosedLoopController pidController;

    private final ArmFeedforward feedforward;

    private final PIDHelper pidHelper;

    private final SparkMaxConfig config = new SparkMaxConfig();

    private double currentGoal;

    public ArmSubsystem() {
        armMotor = new SparkMax(Ports.CANID.ARM_ANGLE.getId(), MotorType.kBrushless);
        
        armMotor.configure(config, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

        armEncoder = armMotor.getAbsoluteEncoder();
        
        pidController = armMotor.getClosedLoopController();

        feedforward = new ArmFeedforward(ArmConstants.kS, ArmConstants.kG, ArmConstants.kV, ArmConstants.kA);
    
        FaultManager.register(armMotor);

        pidHelper = new PIDHelper(
                "Arm",
                ArmConstants.kP,
                ArmConstants.kI,
                ArmConstants.kD,
                (newP) -> config.closedLoop.p(newP),
                (newI) -> config.closedLoop.i(newI),
                (newD) -> config.closedLoop.d(newD),
                ArmConstants.kMaxVelocity,
                ArmConstants.kMaxAcceleration,
                2,
                (newMaxVel) -> config.closedLoop.maxMotion.maxVelocity(newMaxVel),
                (newMaxAccel) -> config.closedLoop.maxMotion.maxAcceleration(newMaxAccel),
                (newAllowedError) -> config.closedLoop.maxMotion.allowedClosedLoopError(newAllowedError));

        safetyChecks();

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

    public void setTargetAngle(Rotation2d angle) {
        currentGoal = angle.getDegrees();
        pidController.setReference(
            angle.getDegrees(),
            ControlType.kMAXMotionPositionControl,
            ClosedLoopSlot.kSlot0,
            feedforward.calculate(getPosition().getRadians(), getVelocity().getRadians())
        );
    }

    public void set(double power) {
        armMotor.set(power);
    }

    public Rotation2d getPosition() {
        return Rotation2d.fromDegrees(armEncoder.getPosition());
    }

    public Rotation2d getVelocity() {
        return Rotation2d.fromDegrees(armEncoder.getVelocity());
    }

    public void stop() {
        set(0);
    }

    @Override
    public void periodic() {
        pidHelper.update();

        SmartDashboard.putNumber("Arm/1/Output", armMotor.get());
        SmartDashboard.putNumber("Arm/1/Voltage", armMotor.getBusVoltage());
        SmartDashboard.putNumber("Arm/1/Current", armMotor.getOutputCurrent());
        SmartDashboard.putNumber("Arm/1/Temp", armMotor.getMotorTemperature());
        SmartDashboard.putNumber("Arm/1/Target", currentGoal);
    }

}
