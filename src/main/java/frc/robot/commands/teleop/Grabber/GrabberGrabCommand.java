package frc.robot.commands.teleop.Grabber;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants.GrabberConstants;
import frc.robot.subsystems.grabber.GrabberSubsystem;
import frc.robot.subsystems.grabber.GrabberSubsystem.GrabType;

public class GrabberGrabCommand extends Command {
    private final GrabberSubsystem m_GrabberSubsystem;

    private final GrabType targetGrabType;

    public GrabberGrabCommand(GrabberSubsystem grabberSubsystem, GrabType targetGrabType) {
        this.m_GrabberSubsystem = grabberSubsystem;
        this.targetGrabType = targetGrabType;

        addRequirements(grabberSubsystem);
    }

    @Override
    public void initialize() {
        m_GrabberSubsystem.setTargetType(targetGrabType);
    }

    @Override
    public void execute() {
        switch(m_GrabberSubsystem.getTargetGrabType()) {
            case CORAL:
                m_GrabberSubsystem.set(GrabberConstants.GRABBER_CORAL_SPEED);
                break;
            case ALGAE:
                m_GrabberSubsystem.set(GrabberConstants.GRABBER_ALGAE_SPEED);
                break;
            case NONE:
                m_GrabberSubsystem.set(0);
                break;
        }
    }

    @Override
    public boolean isFinished() {
        return m_GrabberSubsystem.hasGamePiece();
    }

    @Override
    public void end(boolean interrupted) {
        
    }
}
