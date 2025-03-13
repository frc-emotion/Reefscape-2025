package frc.robot.commands.teleop.Elevator;

import java.util.function.Supplier;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.elevator.ElevatorSubsystem;

public class MoveElevatorManual extends Command {
    private ElevatorSubsystem m_ElevatorSubsystem;
    private Supplier<Double> inputSupplier;

    public MoveElevatorManual(ElevatorSubsystem elevatorSubsystem, Supplier<Double> inSupplier) {
        m_ElevatorSubsystem = elevatorSubsystem;
        inputSupplier = inSupplier;

        addRequirements(m_ElevatorSubsystem);
    }

    @Override
    public void execute() {
        m_ElevatorSubsystem.setWithFeedforward(MathUtil.applyDeadband(inputSupplier.get(), 0.1));
    }
}
