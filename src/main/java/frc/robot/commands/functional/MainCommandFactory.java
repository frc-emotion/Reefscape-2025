package frc.robot.commands.functional;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.units.measure.Distance;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import frc.robot.commands.atomic.elevator.MoveElevatorToHeight;
import frc.robot.commands.atomic.arm.MoveArmToAngle;
import frc.robot.subsystems.arm.ArmSubsystem;
import frc.robot.subsystems.elevator.ElevatorSubsystem;

/**
 * Factory for creating common arm/elevator position commands.
 * Used primarily for PathPlanner NamedCommands in autonomous.
 */
public class MainCommandFactory {
    /**
     * Creates a command to move the elevator and arm to a specific position in parallel.
     * 
     * @param armSubsystem         The arm subsystem
     * @param elevatorSubsystem    The elevator subsystem
     * @param targetElevatorHeight The target height of the elevator
     * @param targetArmAngle       The target angle for the arm
     * @return The command to move the elevator-arm assembly
     */
    public static Command getArmElevatorPositionCommand(
            ArmSubsystem armSubsystem,
            ElevatorSubsystem elevatorSubsystem,
            Distance targetElevatorHeight,
            Rotation2d targetArmAngle) {
        return new ParallelCommandGroup(
                new MoveElevatorToHeight(elevatorSubsystem, targetElevatorHeight),
                new MoveArmToAngle(armSubsystem, targetArmAngle));
    }
}
