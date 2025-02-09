package frc.robot.commands.teleop.Grabber;

import java.security.AlgorithmConstraints;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.grabber.GrabberSubsystem;
import frc.robot.subsystems.grabber.GrabberSubsystem.GrabDirection;
import frc.robot.Constants.GrabberConstants;

public class GrabberCommand extends Command {
    private final GrabberSubsystem grabberSubsystem;

    private final boolean isDefault;
    public GrabberCommand(GrabberSubsystem grabberSubsystem, boolean isDefault) {
        // If default is true means is this the default command
        this.isDefault = isDefault;
        this.grabberSubsystem = grabberSubsystem;
        addRequirements(grabberSubsystem);
    }

    @Override
    public void execute() {

        int negation = grabberSubsystem.getTargetGrabDirection() == GrabDirection.OUTTAKE ? -1 : 1;

        // If isnt default then use whatever was manually overrided to
        // Otherwise if default command then detect what is currently grabbed and continue speed
        switch (isDefault ? grabberSubsystem.getCurrentGrabType() : grabberSubsystem.getTargetGrabType()) {
            case ALGAE:
                grabberSubsystem.set(negation * GrabberConstants.GRABBER_ALGAE_SPEED);
                break;
            case CORAL:
                grabberSubsystem.set(negation * GrabberConstants.GRABBER_CORAL_SPEED);
                break;
            case NONE:
                grabberSubsystem.stop();
            default:
                System.out.println("Something weird is happening...");
                System.out.println(grabberSubsystem.getCurrentGrabType());

        }
    }

    @Override
    public boolean isFinished() {
        return !grabberSubsystem.hasGamePiece();
    }

}