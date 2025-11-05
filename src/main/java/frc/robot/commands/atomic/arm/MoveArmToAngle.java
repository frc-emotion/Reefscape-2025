package frc.robot.commands.atomic.arm;

import java.util.function.Supplier;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.units.measure.Distance;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.arm.ArmSubsystem;

/**
 * Atomic command to move the arm to a specific angle.
 * This is a building block command used in macros and sequences.
 */
public class MoveArmToAngle extends Command {
    private final ArmSubsystem armSubsystem;
    private final Rotation2d targetAngle;
    private final Supplier<Distance> elevatorHeightSupplier;
    private final boolean finishWhenAtSetpoint;
    
    /**
     * Creates a command to move the arm to a target angle.
     * 
     * @param armSubsystem The arm subsystem
     * @param targetAngle Target angle for the arm
     * @param elevatorHeightSupplier Supplier for current elevator height (for feedforward)
     * @param finishWhenAtSetpoint If true, command ends when setpoint is reached
     */
    public MoveArmToAngle(
            ArmSubsystem armSubsystem,
            Rotation2d targetAngle,
            Supplier<Distance> elevatorHeightSupplier,
            boolean finishWhenAtSetpoint) {
        this.armSubsystem = armSubsystem;
        this.targetAngle = targetAngle;
        this.elevatorHeightSupplier = elevatorHeightSupplier;
        this.finishWhenAtSetpoint = finishWhenAtSetpoint;
        
        addRequirements(armSubsystem);
    }
    
    /**
     * Creates a command that runs until manually interrupted.
     * 
     * @param armSubsystem The arm subsystem
     * @param targetAngle Target angle for the arm
     * @param elevatorHeightSupplier Supplier for current elevator height
     */
    public MoveArmToAngle(
            ArmSubsystem armSubsystem,
            Rotation2d targetAngle,
            Supplier<Distance> elevatorHeightSupplier) {
        this(armSubsystem, targetAngle, elevatorHeightSupplier, false);
    }
    
    @Override
    public void execute() {
        armSubsystem.setTargetAngle(targetAngle, elevatorHeightSupplier.get());
    }
    
    @Override
    public boolean isFinished() {
        return finishWhenAtSetpoint && armSubsystem.isAtSetpoint();
    }
    
    @Override
    public void end(boolean interrupted) {
        if (interrupted) {
            armSubsystem.stop();
        }
        // If not interrupted, keep holding position
    }
}
