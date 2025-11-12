package frc.robot.commands.teleop.arm;

import static edu.wpi.first.units.Units.Degrees;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.arm.ArmSubsystem;

public class MoveArmPosition extends Command {
    private final Command yamsCommand;

    public MoveArmPosition(ArmSubsystem armSubsystem, Rotation2d targetRotation2d) {
        this.yamsCommand = armSubsystem.setAngle(Degrees.of(targetRotation2d.getDegrees()));
        addRequirements(armSubsystem);
    }

    @Override
    public void initialize() {
        yamsCommand.initialize();
        System.out.println("Arm command started");
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
        System.out.println("Arm command ended");
    }
}
