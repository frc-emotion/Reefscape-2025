package frc.robot.commands.atomic.climb;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.constants.subsystems.ClimbConstants;
import frc.robot.subsystems.climb.ClimbSubsystem;

/**
 * Atomic command to move the climb mechanism to a specific position.
 */
public class MoveClimbToPosition extends Command {
    private final ClimbSubsystem climbSubsystem;
    private final double targetPosition;
    
    /**
     * Creates a command to move the climb to a target position.
     * 
     * @param climbSubsystem The climb subsystem
     * @param targetPosition Target position in inches
     */
    public MoveClimbToPosition(ClimbSubsystem climbSubsystem, double targetPosition) {
        this.climbSubsystem = climbSubsystem;
        this.targetPosition = targetPosition;
        
        addRequirements(climbSubsystem);
    }
    
    @Override
    public void execute() {
        double currentPosition = climbSubsystem.getPosition();
        
        if (currentPosition < targetPosition) {
            climbSubsystem.setRawSpeed(ClimbConstants.SET_SPEED);
        } else if (currentPosition > targetPosition) {
            climbSubsystem.setRawSpeed(-ClimbConstants.SET_SPEED);
        }
    }
    
    @Override
    public boolean isFinished() {
        return Math.abs(climbSubsystem.getPosition() - targetPosition) < ClimbConstants.POSITION_ERROR;
    }
    
    @Override
    public void end(boolean interrupted) {
        climbSubsystem.stop();
    }
}
