package frc.robot.commands.functional;

import static edu.wpi.first.units.Units.Inches;

import java.util.function.Supplier;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.units.measure.Distance;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.commands.teleop.Elevator.MoveElevatorManual;
import frc.robot.commands.teleop.Elevator.MoveElevatorPosition;
import frc.robot.commands.teleop.Grabber.GrabberGrabCommand;
import frc.robot.commands.teleop.Grabber.GrabberPlaceCommand;
import frc.robot.commands.teleop.arm.MoveArmPosition;
import frc.robot.constants.subsystems.ArmConstants;
import frc.robot.game.Task;
import frc.robot.game.tasks.PickupTask;
import frc.robot.subsystems.arm.ArmSubsystem;
import frc.robot.subsystems.elevator.ElevatorSubsystem;
import frc.robot.subsystems.grabber.GrabberSubsystem;

public class MainCommandFactory {
    /**
     * Retrives a Command to move the elevator and arm to a specific position.
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
                new MoveElevatorPosition(elevatorSubsystem, targetElevatorHeight),
                new MoveArmPosition(armSubsystem, targetArmAngle));
    }

    public static Command getAutoArmElevatorPositionCommand(
        ArmSubsystem armSubsystem,
        ElevatorSubsystem elevatorSubsystem,
        Distance targetElevatorHeight,
        Rotation2d targetArmAngle
    ) {
        // YAMS Commands handle completion internally, no need for finish boolean
        return new ParallelCommandGroup(
            new MoveElevatorPosition(elevatorSubsystem, targetElevatorHeight),
            new MoveArmPosition(armSubsystem, targetArmAngle));
    }

    /**
     * Gets a command to prepare the arm and elevator for placing a game piece.
     * Uses polymorphism to determine positions based on task type.
     * 
     * @param armSubsystem The arm subsystem
     * @param elevatorSubsystem The elevator subsystem
     * @param scoreTask The scoring task (determines positions)
     * @return Command to move to scoring position, or Commands.none() if task is invalid
     */
    public static Command getPlacePrepCommand(
            ArmSubsystem armSubsystem,
            ElevatorSubsystem elevatorSubsystem,
            Task scoreTask) {
        
        // Use polymorphism to get positions from the task
        Distance targetElevatorHeight = scoreTask.getElevatorHeight();
        Rotation2d targetArmAngle = scoreTask.getArmAngle();
        
        // Return none if invalid task (null positions)
        if (targetElevatorHeight == null || targetArmAngle == null) {
            return Commands.none();
        }

        return getArmElevatorPositionCommand(armSubsystem, elevatorSubsystem, targetElevatorHeight, targetArmAngle);
    }

    public static Command getPlaceCommand(
            ArmSubsystem armSubsystem,
            ElevatorSubsystem elevatorSubsystem,
            GrabberSubsystem grabberSubsystem,
            Task scoreTask,
            Supplier<Boolean> placeCondition) {
        return new ParallelCommandGroup(
                getPlacePrepCommand(armSubsystem, elevatorSubsystem, scoreTask),
                new SequentialCommandGroup(
                        Commands.waitUntil(() -> placeCondition.get()),
                        new GrabberPlaceCommand(grabberSubsystem)));
    }

    /**
     * Gets a command to intake/pickup a game piece.
     * Moves arm/elevator to pickup position, then activates grabber.
     * 
     * @param armSubsystem The arm subsystem
     * @param elevatorSubsystem The elevator subsystem
     * @param grabberSubsystem The grabber subsystem
     * @param pickupTask The pickup task (determines positions)
     * @return Command sequence for pickup, or Commands.none() if task is invalid
     */
    public static Command getIntakeCommand(
            ArmSubsystem armSubsystem,
            ElevatorSubsystem elevatorSubsystem,
            GrabberSubsystem grabberSubsystem,
            PickupTask pickupTask) {
        
        // Use polymorphism to get positions from the task
        Distance targetElevatorHeight = pickupTask.getElevatorHeight();
        Rotation2d targetArmRotation = pickupTask.getArmAngle();
        
        // Return none if invalid task (null positions)
        if (targetElevatorHeight == null || targetArmRotation == null) {
            return Commands.none();
        }

        return new SequentialCommandGroup(
                getArmElevatorPositionCommand(armSubsystem, elevatorSubsystem, targetElevatorHeight, targetArmRotation),
                new GrabberGrabCommand(grabberSubsystem));
    }

    public static class BackupCommands {
        public static Command getElevatorL4Stupid(ElevatorSubsystem elevatorSubsystem, ArmSubsystem armSubsystem) {
            return new SequentialCommandGroup(
                    new ParallelCommandGroup(
                            new SequentialCommandGroup(
                                    new MoveElevatorManual(elevatorSubsystem, () -> {
                                        return 0.4;
                                    }).withTimeout(0.5),
                                    getRunElevatorToCurrentCommand(elevatorSubsystem, 0.4, 30)),
                            new SequentialCommandGroup(
                                    new MoveArmPosition(armSubsystem, Rotation2d.fromDegrees(115))
                                            .withTimeout(0.01),
                                    new MoveArmPosition(armSubsystem, Rotation2d.fromDegrees(100)))));
        }

        public static Command getRunElevatorToCurrentCommand(ElevatorSubsystem elevatorSubsystem, double speed,
                int currentLimit) {
            Command elevCmd = elevatorSubsystem.elevCmd(speed);
            return elevCmd.until(() -> elevatorSubsystem.getMotorCurrent() > currentLimit);
        }
    }
}
