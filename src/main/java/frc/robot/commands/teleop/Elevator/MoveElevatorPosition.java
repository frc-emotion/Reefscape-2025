package frc.robot.commands.teleop.Elevator;

import edu.wpi.first.units.measure.Distance;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.elevator.ElevatorSubsystem;

public class MoveElevatorPosition extends Command {
    private final ElevatorSubsystem m_ElevatorSubsystem;
    private Distance targetDistance;

    private boolean shouldFinish;

    public MoveElevatorPosition(ElevatorSubsystem elevatorSubsystem, Distance targetDistance, boolean shouldFinish) {
        this.m_ElevatorSubsystem = elevatorSubsystem;
        this.targetDistance = targetDistance;
        this.shouldFinish = shouldFinish;

        addRequirements(m_ElevatorSubsystem);
    }

    public MoveElevatorPosition(ElevatorSubsystem elevatorSubsystem, Distance targetDistance) {
        this(elevatorSubsystem, targetDistance, false);
    }

    @Override
    public void execute() {
        m_ElevatorSubsystem.setTargetHeight(targetDistance);
    }

    @Override
    public void end(boolean interrupted) {
        m_ElevatorSubsystem.stop();
    }

    @Override
    public boolean isFinished() {
        return shouldFinish && m_ElevatorSubsystem.controllerAtSetpoint();
    }
}
