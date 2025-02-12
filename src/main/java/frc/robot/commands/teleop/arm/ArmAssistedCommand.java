package frc.robot.commands.teleop.arm;

import java.util.function.Supplier;

import edu.wpi.first.math.filter.SlewRateLimiter;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants.ArmConstants;
import frc.robot.subsystems.arm.ArmSubsystem;

public class ArmAssistedCommand extends Command {
    private final ArmSubsystem m_ArmSubsystem;
    private final Supplier<Double> armSupplier;

    private final SlewRateLimiter rateLimiter;

    public ArmAssistedCommand(ArmSubsystem armSubsystem, Supplier<Double> armSupplier) {
        this.m_ArmSubsystem = armSubsystem;
        this.armSupplier = armSupplier;

        rateLimiter = new SlewRateLimiter(ArmConstants.kMaxInputAccel);

        addRequirements(armSubsystem);
    }

    @Override
    public void execute() {
        m_ArmSubsystem.setTargetAngle(
            m_ArmSubsystem.getPosition().plus(
                Rotation2d.fromDegrees(
                    rateLimiter.calculate(armSupplier.get() * 360)
                )
            )
        );
    }

    @Override
    public void end(boolean interrupted) {
        m_ArmSubsystem.stop();
    }
}
