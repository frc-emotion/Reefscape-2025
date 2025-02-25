package frc.robot.commands.teleop.Climb;

import java.util.function.Supplier;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants;
import frc.robot.subsystems.climb.ClimbSubsystem;



public class ClimbManualCommand extends Command {

    private final ClimbSubsystem climbSubsystem;
    private final Supplier<Double> input;

    
    public ClimbManualCommand(ClimbSubsystem cs, Supplier<Double> input) {
        this.climbSubsystem = cs;
        this.input = input;

        addRequirements(cs);
    }

    @Override
    public void execute() {
        // if (input.get() < -Constants.OperatorConstants.DEADBAND) {
        //     climbSubsystem.setRawSpeed(-Constants.ClimbConstants.kSpeed);
        // } else if (input.get() > Constants.OperatorConstants.DEADBAND
        //         && climbSubsystem.getPosition() < Constants.ClimbConstants.EXTENSION_LIMIT) {
        //     climbSubsystem.setRawSpeed(Constants.ClimbConstants.kSpeed);
        // } else {
        //     climbSubsystem.stop();
        // }
        if (climbSubsystem.getPosition() > Constants.ClimbConstants.EXTENSION_LIMIT) {
            climbSubsystem.stop();
            return;
        }

        if (Math.abs(input.get()) > Constants.OperatorConstants.DEADBAND) {
            int direction = (input.get() > 0) ? 1 : -1;
            climbSubsystem.setRawSpeed(direction * Constants.ClimbConstants.kSpeed);
        } else {
            climbSubsystem.stop();
        }
    }

    @Override
    public void end(boolean interrupted) {
        climbSubsystem.stop();
    }
    
    @Override
    public boolean isFinished() {
        return false;
    }


}
