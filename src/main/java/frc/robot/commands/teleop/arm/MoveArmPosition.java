package frc.robot.commands.teleop.arm;

import java.util.function.Supplier;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.units.measure.Distance;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.arm.ArmSubsystem;

public class MoveArmPosition extends Command {
    private final ArmSubsystem m_ArmSubsystem;

    private final Rotation2d targetRotation;

    private final Supplier<Distance> elevatorHeightSupplier;

    public MoveArmPosition(ArmSubsystem armSubsystem, Rotation2d targetRotation2d, Supplier<Distance> elevatorHeight) {
        this.m_ArmSubsystem = armSubsystem;
        this.targetRotation = targetRotation2d;
        this.elevatorHeightSupplier = elevatorHeight;
        
        addRequirements(armSubsystem);
    }

    @Override
    public void execute() {
        m_ArmSubsystem.setTargetAngle(
            targetRotation,
            elevatorHeightSupplier.get()
        );
    }
}
