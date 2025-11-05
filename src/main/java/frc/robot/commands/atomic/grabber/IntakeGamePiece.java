package frc.robot.commands.atomic.grabber;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.constants.subsystems.GrabberConstants;
import frc.robot.subsystems.grabber.GrabberSubsystem;

/**
 * Atomic command to intake a game piece with the grabber.
 * Speed depends on the target game piece type (coral vs algae).
 */
public class IntakeGamePiece extends Command {
    private final GrabberSubsystem grabberSubsystem;
    private final boolean finishWhenAcquired;
    
    /**
     * Creates a command to intake a game piece.
     * 
     * @param grabberSubsystem The grabber subsystem
     * @param finishWhenAcquired If true, ends when game piece is detected
     */
    public IntakeGamePiece(GrabberSubsystem grabberSubsystem, boolean finishWhenAcquired) {
        this.grabberSubsystem = grabberSubsystem;
        this.finishWhenAcquired = finishWhenAcquired;
        
        addRequirements(grabberSubsystem);
    }
    
    /**
     * Creates a command that runs until manually interrupted.
     * 
     * @param grabberSubsystem The grabber subsystem
     */
    public IntakeGamePiece(GrabberSubsystem grabberSubsystem) {
        this(grabberSubsystem, false);
    }
    
    @Override
    public void execute() {
        // Speed depends on what we're trying to grab
        switch (grabberSubsystem.getTargetGrabType()) {
            case CORAL:
                grabberSubsystem.set(GrabberConstants.GRABBER_CORAL_INTAKE);
                break;
            case ALGAE:
                grabberSubsystem.set(GrabberConstants.GRABBER_ALGAE_SPEED);
                break;
            case NONE:
                grabberSubsystem.set(0);
                break;
        }
    }
    
    @Override
    public boolean isFinished() {
        return finishWhenAcquired && grabberSubsystem.hasGamePiece();
    }
    
    @Override
    public void end(boolean interrupted) {
        grabberSubsystem.stop();
    }
}
