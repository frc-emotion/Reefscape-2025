package frc.robot.commands.teleop.arm;

import java.util.function.Supplier;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.constants.subsystems.ArmConstants;
import frc.robot.subsystems.arm.ArmSubsystem;

public class ArmManualCommand extends Command {
    private final ArmSubsystem armSubsystem;
    private final Supplier<Double> armSupplier;
    private Command currentCommand;

    public ArmManualCommand(ArmSubsystem armSubsystem, Supplier<Double> armSupplier) {
        this.armSubsystem = armSubsystem;
        this.armSupplier = armSupplier;

        addRequirements(armSubsystem);
    }

    @Override
    public void execute() {
        double input = MathUtil.applyDeadband(armSupplier.get(), 0.1);
        double output = input * ArmConstants.kMaxOutput;
        
        // End previous command and create new one
        if (currentCommand != null) {
            currentCommand.end(true);
        }
        currentCommand = armSubsystem.armCmd(output);
        currentCommand.initialize();
        currentCommand.execute();
    }

    @Override
    public void end(boolean interrupted) {
        if (currentCommand != null) {
            currentCommand.end(true);
        }
        Command stopCommand = armSubsystem.armCmd(0);
        stopCommand.schedule();
    }
}
