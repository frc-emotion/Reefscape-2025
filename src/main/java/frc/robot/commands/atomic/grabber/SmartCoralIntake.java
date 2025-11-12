package frc.robot.commands.atomic.grabber;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.constants.subsystems.GrabberConstants;
import frc.robot.subsystems.grabber.GrabberSubsystem;
import frc.robot.subsystems.grabber.GrabberSubsystem.CoralState;

/**
 * Smart coral intake that uses dual Time-of-Flight sensors to center the coral.
 * 
 * Strategy:
 * - NONE: Fast intake (nothing detected yet)
 * - FRONT: Slow intake (coral entering, slow down to center)
 * - BOTH: Hold speed (coral perfectly centered!)
 * - BACK: Slight reverse (coral shifted too far, re-center)
 */
public class SmartCoralIntake extends Command {
    private final GrabberSubsystem grabber;
    private boolean wasCentered = false;
    
    public SmartCoralIntake(GrabberSubsystem grabber) {
        this.grabber = grabber;
        addRequirements(grabber);
    }
    
    @Override
    public void initialize() {
        wasCentered = false;
    }
    
    @Override
    public void execute() {
        CoralState state = grabber.getCoralState();
        
        switch(state) {
            case NONE:
                // Nothing detected yet - intake at full speed
                grabber.set(GrabberConstants.CORAL_FAST_INTAKE);
                break;
                
            case FRONT:
                // Coral entering - slow down to carefully center it
                grabber.set(GrabberConstants.CORAL_SLOW_INTAKE);
                break;
                
            case BOTH:
                // Perfect! Both sensors see it - coral is centered
                grabber.set(GrabberConstants.GRABBER_CORAL_HOLD_SPEED);
                wasCentered = true;
                break;
                
            case BACK:
                // Coral went too far back - gently reverse to re-center
                grabber.set(GrabberConstants.CORAL_REVERSE_SLOW);
                break;
        }
    }
    
    @Override
    public boolean isFinished() {
        // Finish when coral is centered and stable
        return wasCentered && grabber.getCoralState() == CoralState.BOTH;
    }
    
    @Override
    public void end(boolean interrupted) {
        if (!interrupted) {
            // Hold the coral in place
            grabber.set(GrabberConstants.GRABBER_CORAL_HOLD_SPEED);
        } else {
            grabber.stop();
        }
    }
}
