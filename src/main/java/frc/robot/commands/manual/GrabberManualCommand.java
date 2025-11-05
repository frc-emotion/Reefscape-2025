package frc.robot.commands.manual;

import java.util.function.Supplier;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.grabber.GrabberSubsystem;

/**
 * Manual command for direct control of the grabber.
 * Bypasses the state machine - used in manual control mode.
 */
public class GrabberManualCommand extends Command {
    private final GrabberSubsystem grabberSubsystem;
    private final Supplier<Double> speedSupplier;
    
    /**
     * Creates a manual grabber control command.
     * 
     * @param grabberSubsystem The grabber subsystem
     * @param speedSupplier Supplier for motor speed (-1.0 to 1.0)
     */
    public GrabberManualCommand(GrabberSubsystem grabberSubsystem, Supplier<Double> speedSupplier) {
        this.grabberSubsystem = grabberSubsystem;
        this.speedSupplier = speedSupplier;
        
        addRequirements(grabberSubsystem);
    }
    
    @Override
    public void execute() {
        grabberSubsystem.set(speedSupplier.get());
    }
    
    @Override
    public void end(boolean interrupted) {
        grabberSubsystem.stop();
    }
}
