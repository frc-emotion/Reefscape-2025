package frc.robot.subsystems.arm;

import static edu.wpi.first.units.Units.KilogramMetersPerSecond;
import static edu.wpi.first.units.Units.Meter;

import org.dyn4j.geometry.Rotation;

import com.revrobotics.RelativeEncoder;
import com.revrobotics.spark.ClosedLoopSlot;
import com.revrobotics.spark.SparkAbsoluteEncoder;
import com.revrobotics.spark.SparkClosedLoopController;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkRelativeEncoder;
import com.revrobotics.spark.SparkBase.ControlType;
import com.revrobotics.spark.SparkLowLevel.MotorType;

import edu.wpi.first.math.controller.ArmFeedforward;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.units.measure.Distance;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.ArmConstants;
import frc.robot.Constants.ElevatorConstants;
import frc.robot.Constants.Ports;
import frc.robot.util.Configs.ArmConfigs;

public class ArmSubsystem extends SubsystemBase {
    private final SparkMax armMotor;

    private final RelativeEncoder armEncoder;

    private final SparkClosedLoopController pidController;

    private final ArmFeedforward feedforward;

    private Rotation2d targetAngle;

    public ArmSubsystem() {
        armMotor = new SparkMax(Ports.CANID.ARM_ANGLE.getId(), MotorType.kBrushless);
        
        armEncoder = armMotor.getEncoder();
        
        pidController = armMotor.getClosedLoopController();

        feedforward = new ArmFeedforward(ArmConstants.kS, ArmConstants.kG, ArmConstants.kV, ArmConstants.kA);

        targetAngle = getRotation();
    }

    /**
     * Sets the target angle for the arm. Constrained by the limits defined in currentConstraints.
     * 
     * @param angle The target angle to rotate to
     */
    public void setTargetAngle(Rotation2d angle, Distance elevatorHeight) {
        targetAngle = constrain(angle, elevatorHeight);

        pidController.setReference(
            targetAngle.getDegrees(),
            ControlType.kMAXMotionPositionControl,
            ClosedLoopSlot.kSlot0,
            feedforward.calculate(getRotation().getRadians(), getVelocity().getRadians())
        );
    }

     /**
     * Sets the target angle for the arm. Constrained by the limits defined in currentConstraints.
     * 
     * @param angle The target angle to rotate to
     */
    public void setTargetAngle(Rotation2d angle) {
        targetAngle = constrain(angle, ElevatorConstants.CORAL_INTAKE_HEIGHT);

        pidController.setReference(
            targetAngle.getDegrees(),
            ControlType.kMAXMotionPositionControl,
            ClosedLoopSlot.kSlot0,
            feedforward.calculate(getRotation().getRadians(), getVelocity().getRadians())
        );
    }

    /**
     * Sends raw input to the motor
     * 
     * @param power The power input to the motor within [-1, 1].
     */
    public void set(double power) {
        armMotor.set(power);
    }

    /**
     * Constrains the rotation of the arm depending on the height of the elevator.
     * 
     * @param targetRotation    The desired rotation for the arm.
     * @param elevatorHeight    The current height of the elevator.
     * @return                  The new target rotation, taking into account limits.
     */
    public Rotation2d constrain(Rotation2d targetRotation, Distance elevatorHeight) {
        if(
            elevatorHeight.isNear(
                Meter.of(0),
                Meter.of(ArmConstants.kMaxHeightConstrained)
            )
        ) {
            if(targetRotation.getDegrees() > ArmConstants.kMaxRotationConstrained) {
                return Rotation2d.fromDegrees(ArmConstants.kMaxRotationConstrained);
            } else if(targetRotation.getDegrees() < ArmConstants.kMinRotationConstrained) {
                return Rotation2d.fromDegrees(ArmConstants.kMinRotationConstrained);
            }
        } else {
            if(targetRotation.getDegrees() > ArmConstants.kMaxRotation) {
                return Rotation2d.fromDegrees(ArmConstants.kMaxRotation);
            } else if(targetRotation.getDegrees() < ArmConstants.kMinRotation) {
                return Rotation2d.fromDegrees(ArmConstants.kMinRotation);
            }
        }
        
        return targetRotation;
    }

    public Rotation2d getRotation() {
        return Rotation2d.fromDegrees(armEncoder.getPosition());
    }

    public Rotation2d getTargetRotation() {
        return targetAngle;
    }

    public Rotation2d getVelocity() {
        return Rotation2d.fromDegrees(armEncoder.getVelocity());
    }

    public void stop() {
        set(0);
    }

    public double getCurrent() {
        return armMotor.getOutputCurrent();
    }
}
