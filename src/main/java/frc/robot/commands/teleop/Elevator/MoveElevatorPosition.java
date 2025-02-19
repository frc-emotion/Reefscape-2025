package frc.robot.commands.teleop.Elevator;

import edu.wpi.first.units.measure.Distance;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.elevator.ElevatorSubsystem;

public class MoveElevatorPosition extends Command {
    private final ElevatorSubsystem m_ElevatorSubsystem;
    private Distance targetDistance;

    public MoveElevatorPosition(ElevatorSubsystem elevatorSubsystem, Distance targetDistance) {
        this.m_ElevatorSubsystem = elevatorSubsystem;
        this.targetDistance = targetDistance;

        addRequirements(m_ElevatorSubsystem);
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
        return m_ElevatorSubsystem.isAtSetpoint();
    }
}
