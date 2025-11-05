package frc.robot.commands.atomic.elevator;

import edu.wpi.first.units.measure.Distance;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.elevator.ElevatorSubsystem;

/**
 * Atomic command to move the elevator to a specific height.
 * This is a building block command used in macros and sequences.
 */
public class MoveElevatorToHeight extends Command {
    private final ElevatorSubsystem elevatorSubsystem;
    private final Distance targetHeight;
    private final boolean finishWhenAtSetpoint;
    
    /**
     * Creates a command to move the elevator to a target height.
     * 
     * @param elevatorSubsystem The elevator subsystem
     * @param targetHeight Target height for the elevator
     * @param finishWhenAtSetpoint If true, command ends when setpoint is reached
     */
    public MoveElevatorToHeight(
            ElevatorSubsystem elevatorSubsystem,
            Distance targetHeight,
            boolean finishWhenAtSetpoint) {
        this.elevatorSubsystem = elevatorSubsystem;
        this.targetHeight = targetHeight;
        this.finishWhenAtSetpoint = finishWhenAtSetpoint;
        
        addRequirements(elevatorSubsystem);
    }
    
    /**
     * Creates a command that runs until manually interrupted.
     * 
     * @param elevatorSubsystem The elevator subsystem
     * @param targetHeight Target height for the elevator
     */
    public MoveElevatorToHeight(ElevatorSubsystem elevatorSubsystem, Distance targetHeight) {
        this(elevatorSubsystem, targetHeight, false);
    }
    
    @Override
    public void execute() {
        elevatorSubsystem.setTargetHeight(targetHeight);
    }
    
    @Override
    public boolean isFinished() {
        return finishWhenAtSetpoint && elevatorSubsystem.isAtSetpoint();
    }
    
    @Override
    public void end(boolean interrupted) {
        if (interrupted) {
            elevatorSubsystem.stop();
        }
        // If not interrupted, keep holding position
    }
}
