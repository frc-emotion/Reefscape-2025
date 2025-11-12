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
        double input = MathUtil.applyDeadband(inputSupplier.get(), 0.1);
        m_ElevatorSubsystem.elevCmd(input).schedule();
    }
    
    @Override
    public void end(boolean interrupted) {
        m_ElevatorSubsystem.stop();
    }
}
