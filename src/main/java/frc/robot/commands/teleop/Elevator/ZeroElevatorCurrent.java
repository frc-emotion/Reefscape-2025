package frc.robot.commands.teleop.Elevator;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.constants.subsystems.ElevatorConstants;
import frc.robot.subsystems.elevator.ElevatorSubsystem;

public class ZeroElevatorCurrent extends Command {
    private final ElevatorSubsystem elevatorSubsystem;
    private final double slowSpeed = -0.2;
    private final double timeoutSeconds = 5.0; // Safety
    private double startTime;
    private Command currentCommand;

    public ZeroElevatorCurrent(ElevatorSubsystem elevatorSubsystem) {
        this.elevatorSubsystem = elevatorSubsystem;
        addRequirements(elevatorSubsystem);
    }

    @Override
    public void initialize() {
        startTime = System.currentTimeMillis() / 1000.0;
    }

    @Override
    public void execute() {
        if (currentCommand != null) {
            currentCommand.end(true);
        }
        currentCommand = elevatorSubsystem.elevCmd(slowSpeed);
        currentCommand.initialize();
        currentCommand.execute();
    }

    @Override
    public boolean isFinished() {
        double currentTime = System.currentTimeMillis() / 1000.0;
        return elevatorSubsystem.getMotorCurrent() > ElevatorConstants.CURRENT_SPIKE_THRESHOLD ||
               (currentTime - startTime) >= timeoutSeconds;
    }

    @Override
    public void end(boolean interrupted) {
        if (currentCommand != null) {
            currentCommand.end(true);
        }
        // Stop elevator - YAMS handles position internally
        elevatorSubsystem.elevCmd(0).schedule();
    }
}