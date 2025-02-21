package frc.robot.commands.teleop.Climb;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.climb.ClimbSubsystem;
import frc.robot.Constants;

public class MoveClimbPosition extends Command {
    private ClimbSubsystem m_ClimbSubsystem;

    private double targetPosition;

    public MoveClimbPosition(ClimbSubsystem climbSubsystem, double targetPosition) {
        this.m_ClimbSubsystem = climbSubsystem;
        this.targetPosition = targetPosition;

        addRequirements(climbSubsystem);
    }

    @Override
    public void execute() {
        if(m_ClimbSubsystem.getPosition() < targetPosition)
            m_ClimbSubsystem.setRawSpeed(Constants.ClimbConstants.SET_SPEED);
        else if (m_ClimbSubsystem.getPosition() > targetPosition)
            m_ClimbSubsystem.setRawSpeed(-Constants.ClimbConstants.SET_SPEED);
    }
    
    @Override
    public boolean isFinished() {
        m_ClimbSubsystem.stop();
        return Math.abs(m_ClimbSubsystem.getPosition() - targetPosition) < Constants.ClimbConstants.POSITION_ERROR;   
    }
    
}
