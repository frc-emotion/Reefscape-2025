package frc.robot.commands.teleop.arm;

import java.util.function.Supplier;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants.ArmConstants;
import frc.robot.subsystems.arm.ArmSubsystem;

public class ArmManualCommand extends Command {
    private final ArmSubsystem m_ArmSubsystem;
    private Supplier<Double> armSupplier;

    public ArmManualCommand(ArmSubsystem armSubsystem, Supplier<Double> armSupplier) {
        m_ArmSubsystem = armSubsystem;
        this.armSupplier = armSupplier;

        addRequirements(armSubsystem);
    }

    @Override
    public void execute() {
        m_ArmSubsystem.setWithFeedforward(MathUtil.applyDeadband(armSupplier.get(), 0.1) * ArmConstants.kMaxOutput);
    }

    @Override
    public void end(boolean interrupted) {
        m_ArmSubsystem.stop();
    }
}
