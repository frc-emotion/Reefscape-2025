package frc.robot.commands.teleop.Elevator;

import java.util.function.Supplier;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.elevator.ElevatorSubsystem;

public class MoveElevatorManual extends Command {
    private final ElevatorSubsystem elevatorSubsystem;
    private final Supplier<Double> inputSupplier;
    private Command currentCommand;

    public MoveElevatorManual(ElevatorSubsystem elevatorSubsystem, Supplier<Double> inSupplier) {
        this.elevatorSubsystem = elevatorSubsystem;
        this.inputSupplier = inSupplier;

        addRequirements(elevatorSubsystem);
    }

    @Override
    public void execute() {
        double input = MathUtil.applyDeadband(inputSupplier.get(), 0.1);
        
        if (currentCommand != null) {
            currentCommand.end(true);
        }
        currentCommand = elevatorSubsystem.elevCmd(input);
        currentCommand.initialize();
        currentCommand.execute();
    }

    @Override
    public void end(boolean interrupted) {
        if (currentCommand != null) {
            currentCommand.end(true);
        }
        elevatorSubsystem.elevCmd(0).schedule();
    }
}
