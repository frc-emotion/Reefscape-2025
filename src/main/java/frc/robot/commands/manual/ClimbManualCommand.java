package frc.robot.commands.manual;

import java.util.function.Supplier;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.constants.subsystems.ClimbConstants;
import frc.robot.constants.OperatorConstants;
import frc.robot.subsystems.climb.ClimbSubsystem;

/**
 * Manual command for direct joystick control of the climb mechanism.
 * Bypasses the state machine - used in manual control mode.
 */
public class ClimbManualCommand extends Command {
    private final ClimbSubsystem climbSubsystem;
    private final Supplier<Double> inputSupplier;
    
    /**
     * Creates a manual climb control command.
     * 
     * @param climbSubsystem The climb subsystem
     * @param inputSupplier Supplier for joystick input (-1.0 to 1.0)
     */
    public ClimbManualCommand(ClimbSubsystem climbSubsystem, Supplier<Double> inputSupplier) {
        this.climbSubsystem = climbSubsystem;
        this.inputSupplier = inputSupplier;
        
        addRequirements(climbSubsystem);
    }
    
    @Override
    public void execute() {
        double input = inputSupplier.get();
        
        if (Math.abs(input) > OperatorConstants.DEADBAND) {
            climbSubsystem.setRawSpeed(input * ClimbConstants.kSpeed);
        } else {
            climbSubsystem.stop();
        }
    }
    
    @Override
    public void end(boolean interrupted) {
        climbSubsystem.stop();
    }
}
