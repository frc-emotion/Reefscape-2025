package frc.robot.commands.teleop.Elevator;

import java.util.function.Supplier;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.elevator.ElevatorSubsystem;

public class MoveElevatorManual extends Command {
    private ElevatorSubsystem elevatorSubsystem;

    private Supplier<Double> yAxis;

    public MoveElevatorManual(ElevatorSubsystem elevatorSubsystem, Supplier<Double> yAxis) {
        this.yAxis = yAxis;
        this.elevatorSubsystem = elevatorSubsystem;
        addRequirements(elevatorSubsystem);
    }

    @Override
    public void end(boolean interrupted) {
        elevatorSubsystem.stop();
    }

    @Override
    public void execute() {
        elevatorSubsystem.moveSpeed(yAxis.get() * 0.75);
    }

    @Override
    public void initialize() {
        elevatorSubsystem.stop();
    }

    @Override
    public boolean isFinished() {
        return false;
    }

}