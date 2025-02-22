package frc.robot.commands.teleop.Climb;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants;
import frc.robot.subsystems.climb.ClimbSubsystem;

public class ClimbMoveToPosCommand extends Command {
    private final ClimbSubsystem climbSubsystem;

    private double targetPosition;

    public ClimbMoveToPosCommand(ClimbSubsystem cs, double targetPosition) {
        this.climbSubsystem = cs;
        this.targetPosition = targetPosition;

        addRequirements(cs);
    }

    @Override
    public void execute() {
        if(climbSubsystem.getPosition() < targetPosition)
            climbSubsystem.setRawSpeed(Constants.ClimbConstants.SET_SPEED);
        else if (climbSubsystem.getPosition() > targetPosition)
            climbSubsystem.setRawSpeed(-Constants.ClimbConstants.SET_SPEED);
    }
    
    @Override
    public boolean isFinished() {
        climbSubsystem.stop();
        return Math.abs(climbSubsystem.getPosition() - targetPosition) < Constants.ClimbConstants.POSITION_ERROR;   
    }
}

