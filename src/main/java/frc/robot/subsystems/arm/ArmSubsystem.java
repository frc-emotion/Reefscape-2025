package frc.robot.subsystems.arm;

import org.dyn4j.geometry.Rotation;

import com.revrobotics.spark.ClosedLoopSlot;
import com.revrobotics.spark.SparkAbsoluteEncoder;
import com.revrobotics.spark.SparkClosedLoopController;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkBase.ControlType;
import com.revrobotics.spark.SparkLowLevel.MotorType;

import edu.wpi.first.math.controller.ArmFeedforward;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.ArmConstants;
import frc.robot.Constants.Ports;
import frc.robot.util.Configs.ArmConfigs;

public class ArmSubsystem extends SubsystemBase {
    private final SparkMax armMotor;

    private final SparkAbsoluteEncoder armEncoder;

    private final SparkClosedLoopController pidController;

    private final ArmFeedforward feedforward;

    public ArmSubsystem() {
        armMotor = new SparkMax(Ports.CANID.ARM_ANGLE.getId(), MotorType.kBrushless);
        
        armEncoder = armMotor.getAbsoluteEncoder();
        
        pidController = armMotor.getClosedLoopController();

        feedforward = new ArmFeedforward(ArmConstants.kS, ArmConstants.kG, ArmConstants.kV, ArmConstants.kA);
    }

    public void setTargetAngle(Rotation2d angle) {
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
}
