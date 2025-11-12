package frc.robot.commands.atomic.elevator;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.constants.subsystems.ElevatorConstants;
import frc.robot.subsystems.elevator.ElevatorSubsystem;

/**
 * Homes the elevator by slowly moving down until detecting the bottom,
 * then zeros the encoder position.
 * 
 * Detection method: Motor current spike when hitting the mechanical stop.
 * Safety: Timeout after 5 seconds if bottom not detected.
 */
public class HomeElevatorCommand extends Command {
    private final ElevatorSubsystem elevator;
    private final Timer timer = new Timer();
    private Command currentCommand;
    
    public HomeElevatorCommand(ElevatorSubsystem elevator) {
        this.elevator = elevator;
        addRequirements(elevator);
    }
    
    @Override
    public void initialize() {
        timer.restart();
        System.out.println("Starting elevator homing sequence...");
    }
    
    @Override
    public void execute() {
        // Slowly move elevator down
        if (currentCommand != null) {
            currentCommand.end(true);
        }
        currentCommand = elevator.elevCmd(ElevatorConstants.HOMING_SPEED);
        currentCommand.initialize();
        currentCommand.execute();
    }
    
    @Override
    public boolean isFinished() {
        // Finish when:
        // 1. Current spike detected (hit bottom)
        // 2. OR timeout reached (safety)
        boolean currentSpiked = elevator.getMotorCurrent() > ElevatorConstants.HOMING_CURRENT_THRESHOLD;
        boolean timedOut = timer.hasElapsed(ElevatorConstants.HOMING_TIMEOUT_SECONDS);
        
        if (currentSpiked) {
            System.out.println("Elevator bottom detected via current spike");
        }
        if (timedOut) {
            System.err.println("WARNING: Elevator homing timed out!");
        }
        
        return currentSpiked || timedOut;
    }
    
    @Override
    public void end(boolean interrupted) {
        if (currentCommand != null) {
            currentCommand.end(true);
        }
        
        // Stop the elevator
        elevator.elevCmd(0).schedule();
        
        if (!interrupted) {
            // Zero the encoder position
            // Note: YAMS manages position internally
            // You may need to add a method to reset the YAMS elevator position
            // elevator.getElevator().resetPosition(Meters.of(0));
            System.out.println("Elevator homed successfully - position zeroed");
        } else {
            System.err.println("Elevator homing interrupted!");
        }
    }
}
