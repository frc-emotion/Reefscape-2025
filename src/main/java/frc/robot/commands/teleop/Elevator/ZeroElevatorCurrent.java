package frc.robot.commands.teleop.Elevator;

import java.util.function.Supplier;

import edu.wpi.first.units.Units;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants.ElevatorConstants;
import frc.robot.subsystems.elevator.ElevatorSubsystem;

public class ZeroElevatorCurrent extends Command {
    private ElevatorSubsystem elevatorSubsystem;
    private final double slowSpeed = -0.2;
    private final double timeoutSeconds = 5.0; // Safety
    private double startTime;


    public ZeroElevatorCurrent(ElevatorSubsystem elevatorSubsystem) {
        this.elevatorSubsystem = elevatorSubsystem;
        addRequirements(elevatorSubsystem);
    }

    @Override
    public void end(boolean interrupted) {
        elevatorSubsystem.resetSensorPosition(Units.Inches.of(0));
        elevatorSubsystem.stop();
    }

    @Override
    public void execute() {
        elevatorSubsystem.moveSpeed(slowSpeed);
    }

    @Override
    public void initialize() {
        elevatorSubsystem.stop();
        startTime = System.currentTimeMillis() / 1000.0;
    }

    @Override
    public boolean isFinished() {
        double currentTime = System.currentTimeMillis() / 1000.0;
        return elevatorSubsystem.getCurrentDraw(true) > ElevatorConstants.CURRENT_SPIKE_THRESHOLD ||
               (currentTime - startTime) >= timeoutSeconds;
    }

}