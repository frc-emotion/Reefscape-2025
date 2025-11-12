package frc.robot.commands.atomic.arm;

import static edu.wpi.first.units.Units.Degrees;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.arm.ArmSubsystem;

/**
 * Atomic command to move the arm to a specific angle using YAMS.
 * This is a building block command used in macros and sequences.
 */
public class MoveArmToAngle extends Command {
    private final Command yamsCommand;
    
    /**
     * Creates a command to move the arm to a target angle.
     * 
     * @param armSubsystem The arm subsystem
     * @param targetAngle Target angle for the arm
     */
    public MoveArmToAngle(ArmSubsystem armSubsystem, Rotation2d targetAngle) {
        this.yamsCommand = armSubsystem.setAngle(Degrees.of(targetAngle.getDegrees()));
        addRequirements(armSubsystem);
    }
    
    @Override
    public void initialize() {
        yamsCommand.initialize();
    }
    
    @Override
    public void execute() {
        yamsCommand.execute();
    }
    
    @Override
    public boolean isFinished() {
        return yamsCommand.isFinished();
    }
    
    @Override
    public void end(boolean interrupted) {
        yamsCommand.end(interrupted);
    }
}
