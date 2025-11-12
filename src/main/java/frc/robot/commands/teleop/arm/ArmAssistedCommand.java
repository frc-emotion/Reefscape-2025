package frc.robot.commands.teleop.arm;

import java.util.function.Supplier;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.arm.ArmSubsystem;

public class ArmAssistedCommand extends Command {
    private final ArmSubsystem armSubsystem;
    private final Supplier<Double> armSupplier;
    private Command currentCommand;

    public ArmAssistedCommand(ArmSubsystem armSubsystem, Supplier<Double> armSupplier) {
        this.armSubsystem = armSubsystem;
        this.armSupplier = armSupplier;
        addRequirements(armSubsystem);
    }

    @Override
    public void execute() {
        // Use YAMS manual control with duty cycle
        double input = armSupplier.get();
        if (currentCommand != null) {
            currentCommand.end(true);
        }
        currentCommand = armSubsystem.armCmd(input);
        currentCommand.initialize();
        currentCommand.execute();
    }

    @Override
    public void end(boolean interrupted) {
        if (currentCommand != null) {
            currentCommand.end(true);
        }
        // Stop the arm
        Command stopCmd = armSubsystem.armCmd(0);
        stopCmd.schedule();
    }
}
