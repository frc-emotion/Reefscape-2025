package frc.robot.commands.teleop.Grabber;

import java.security.AlgorithmConstraints;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.grabber.GrabberSubsystem;
import frc.robot.constants.subsystems.GrabberConstants;

public class GrabberHoldCommand extends Command {
    private final GrabberSubsystem grabberSubsystem;

    public GrabberHoldCommand(GrabberSubsystem grabberSubsystem) {
        this.grabberSubsystem = grabberSubsystem;

        addRequirements(grabberSubsystem);
    }

    @Override
    public void execute() {

        switch (grabberSubsystem.getCurrentGrabType()) {
            case ALGAE:
                grabberSubsystem.set(GrabberConstants.GRABBER_ALGAE_HOLD_SPEED);
                break;
            case CORAL:
                grabberSubsystem.set(GrabberConstants.GRABBER_CORAL_HOLD_SPEED);
                break;
            case NONE:
                grabberSubsystem.stop();
            default:
                System.out.println("GrabberHoldCommand Error! Current Type: " + grabberSubsystem.getCurrentGrabType());

        }
    }
}