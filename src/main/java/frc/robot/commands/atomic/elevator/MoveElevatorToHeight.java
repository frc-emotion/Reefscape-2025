package frc.robot.commands.atomic.elevator;

import edu.wpi.first.units.measure.Distance;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.elevator.ElevatorSubsystem;

/**
 * Atomic command to move the elevator to a specific height using YAMS.
 * This is a building block command used in macros and sequences.
 */
public class MoveElevatorToHeight extends Command {
    private final Command yamsCommand;
    
    /**
     * Creates a command to move the elevator to a target height.
     * 
     * @param elevatorSubsystem The elevator subsystem
     * @param targetHeight Target height for the elevator
     */
    public MoveElevatorToHeight(ElevatorSubsystem elevatorSubsystem, Distance targetHeight) {
        this.yamsCommand = elevatorSubsystem.setHeight(targetHeight);
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
