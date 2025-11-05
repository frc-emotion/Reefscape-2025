package frc.robot.commands.manual;

import java.util.function.Supplier;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.elevator.ElevatorSubsystem;

/**
 * Manual command for direct joystick control of the elevator.
 * Bypasses the state machine - used in manual control mode.
 */
public class ElevatorManualCommand extends Command {
    private final ElevatorSubsystem elevatorSubsystem;
    private final Supplier<Double> inputSupplier;
    
    /**
     * Creates a manual elevator control command.
     * 
     * @param elevatorSubsystem The elevator subsystem
     * @param inputSupplier Supplier for joystick input (-1.0 to 1.0)
     */
    public ElevatorManualCommand(ElevatorSubsystem elevatorSubsystem, Supplier<Double> inputSupplier) {
        this.elevatorSubsystem = elevatorSubsystem;
        this.inputSupplier = inputSupplier;
        
        addRequirements(elevatorSubsystem);
    }
    
    @Override
    public void execute() {
        double input = MathUtil.applyDeadband(inputSupplier.get(), 0.1);
        elevatorSubsystem.setWithFeedforward(input);
    }
    
    @Override
    public void end(boolean interrupted) {
        elevatorSubsystem.stop();
    }
}
