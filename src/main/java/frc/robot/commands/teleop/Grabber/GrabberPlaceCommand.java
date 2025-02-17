package frc.robot.commands.teleop.Grabber;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants.GrabberConstants;
import frc.robot.subsystems.grabber.GrabberSubsystem;

public class GrabberPlaceCommand extends Command {
    private final GrabberSubsystem m_GrabberSubsystem;

    public GrabberPlaceCommand(GrabberSubsystem grabberSubsystem) {
        this.m_GrabberSubsystem = grabberSubsystem;

        addRequirements(grabberSubsystem);
    }

    @Override
    public void execute() {
        switch(m_GrabberSubsystem.getCurrentGrabType()) {
            case CORAL:
                m_GrabberSubsystem.set(-GrabberConstants.GRABBER_CORAL_SPEED);
                break;
            case ALGAE:
                m_GrabberSubsystem.set(-GrabberConstants.GRABBER_ALGAE_SPEED);
                break;
            case NONE:
                m_GrabberSubsystem.set(0);
                break;
        }
    }

    @Override
    public boolean isFinished() {
        return !m_GrabberSubsystem.hasGamePiece();
    }

    @Override
    public void end(boolean interrupted) {
        m_GrabberSubsystem.set(0);
    }
    
}
