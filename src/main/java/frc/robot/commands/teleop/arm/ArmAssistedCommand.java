package frc.robot.commands.teleop.arm;

import java.util.function.Supplier;

import edu.wpi.first.math.filter.SlewRateLimiter;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.units.measure.Distance;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants.ArmConstants;
import frc.robot.subsystems.arm.ArmSubsystem;

public class ArmAssistedCommand extends Command {
    private final ArmSubsystem m_ArmSubsystem;
    private final Supplier<Double> armSupplier;

    private final Supplier<Distance> elevatorHeightSupplier;

    public ArmAssistedCommand(ArmSubsystem armSubsystem, Supplier<Double> armSupplier, Supplier<Distance> elevatorHeight) {
        this.m_ArmSubsystem = armSubsystem;
        this.armSupplier = armSupplier;
        this.elevatorHeightSupplier = elevatorHeight;

        addRequirements(armSubsystem);
    }

    @Override
    public void execute() {
        Rotation2d targetRotation = Rotation2d.fromDegrees(
            m_ArmSubsystem.getRotation().getDegrees() + (armSupplier.get() * 360)
        );

        m_ArmSubsystem.setTargetAngle(targetRotation, elevatorHeightSupplier.get());
    }

    @Override
    public void end(boolean interrupted) {
        m_ArmSubsystem.stop();
    }
}
