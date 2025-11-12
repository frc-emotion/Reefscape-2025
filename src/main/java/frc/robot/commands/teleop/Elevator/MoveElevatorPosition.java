package frc.robot.commands.teleop.Elevator;

import edu.wpi.first.units.measure.Distance;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.elevator.ElevatorSubsystem;

public class MoveElevatorPosition extends Command {
    private final Command yamsCommand;

    public MoveElevatorPosition(ElevatorSubsystem elevatorSubsystem, Distance targetDistance) {
        this.yamsCommand = elevatorSubsystem.setHeight(targetDistance);
        addRequirements(elevatorSubsystem);
    }

    @Override
    public void initialize() {
        yamsCommand.initialize();
    }

    @Override
    public void execute() {
        yamsCommand.execute();
    }

    @Override
    public boolean isFinished() {
        return yamsCommand.isFinished();
    }

    @Override
    public void end(boolean interrupted) {
        yamsCommand.end(interrupted);
    }
}
