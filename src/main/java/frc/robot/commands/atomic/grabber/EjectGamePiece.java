package frc.robot.commands.atomic.grabber;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.constants.subsystems.GrabberConstants;
import frc.robot.subsystems.grabber.GrabberSubsystem;

/**
 * Atomic command to eject/place a game piece from the grabber.
 * Speed depends on the current game piece type (coral vs algae).
 */
public class EjectGamePiece extends Command {
    private final GrabberSubsystem grabberSubsystem;
    private final boolean finishWhenReleased;
    
    /**
     * Creates a command to eject a game piece.
     * 
     * @param grabberSubsystem The grabber subsystem
     * @param finishWhenReleased If true, ends when game piece is no longer detected
     */
    public EjectGamePiece(GrabberSubsystem grabberSubsystem, boolean finishWhenReleased) {
        this.grabberSubsystem = grabberSubsystem;
        this.finishWhenReleased = finishWhenReleased;
        
        addRequirements(grabberSubsystem);
    }
    
    /**
     * Creates a command that runs until manually interrupted.
     * 
     * @param grabberSubsystem The grabber subsystem
     */
    public EjectGamePiece(GrabberSubsystem grabberSubsystem) {
        this(grabberSubsystem, false);
    }
    
    @Override
    public void execute() {
        // Speed depends on what we're ejecting
        switch (grabberSubsystem.getCurrentGrabType()) {
            case CORAL:
                grabberSubsystem.set(GrabberConstants.GRABBER_CORAL_OUTTAKE);
                break;
            case ALGAE:
                grabberSubsystem.set(-GrabberConstants.GRABBER_ALGAE_SPEED);
                break;
            case NONE:
                grabberSubsystem.set(0);
                break;
        }
    }
    
    @Override
    public boolean isFinished() {
        return finishWhenReleased && !grabberSubsystem.hasGamePiece();
    }
    
    @Override
    public void end(boolean interrupted) {
        grabberSubsystem.stop();
    }
}
