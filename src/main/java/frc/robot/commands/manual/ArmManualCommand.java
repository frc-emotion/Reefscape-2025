package frc.robot.commands.manual;

import java.util.function.Supplier;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.constants.subsystems.ArmConstants;
import frc.robot.subsystems.arm.ArmSubsystem;

/**
 * Manual command for direct joystick control of the arm.
 * Bypasses the state machine - used in manual control mode.
 */
public class ArmManualCommand extends Command {
    private final ArmSubsystem armSubsystem;
    private final Supplier<Double> inputSupplier;
    private Command currentCommand;
    
    /**
     * Creates a manual arm control command.
     * 
     * @param armSubsystem The arm subsystem
     * @param inputSupplier Supplier for joystick input (-1.0 to 1.0)
     */
    public ArmManualCommand(ArmSubsystem armSubsystem, Supplier<Double> inputSupplier) {
        this.armSubsystem = armSubsystem;
        this.inputSupplier = inputSupplier;
        
        addRequirements(armSubsystem);
    }
    
    @Override
    public void execute() {
        double input = MathUtil.applyDeadband(inputSupplier.get(), 0.1);
        double output = input * ArmConstants.kMaxOutput;
        
        if (currentCommand != null) {
            currentCommand.end(true);
        }
        currentCommand = armSubsystem.armCmd(output);
        currentCommand.initialize();
        currentCommand.execute();
    }
    
    @Override
    public void end(boolean interrupted) {
        if (currentCommand != null) {
            currentCommand.end(true);
        }
        armSubsystem.armCmd(0).schedule();
    }
}
