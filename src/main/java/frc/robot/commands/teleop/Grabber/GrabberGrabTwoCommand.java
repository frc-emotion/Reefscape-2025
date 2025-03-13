package frc.robot.commands.teleop.Grabber;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants.GrabberConstants;
import frc.robot.subsystems.grabber.GrabberSubsystem;

public class GrabberGrabTwoCommand extends Command {
    private GrabberSubsystem grabberSubsystem;

    public GrabberGrabTwoCommand(GrabberSubsystem grabberSubsystem) {
        this.grabberSubsystem = grabberSubsystem;

        addRequirements(grabberSubsystem);
    }

    @Override
    public void execute() {
        grabberSubsystem.set(GrabberConstants.GRABBER_CORAL_INTAKE);
    }

    @Override
    public boolean isFinished() {
        return !grabberSubsystem.getFrontCoralState();
    }

    @Override
    public void end(boolean interrupted) {
        grabberSubsystem.stop();
    }
}
