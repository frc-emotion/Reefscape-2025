package frc.robot.commands.functional;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.Distance;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.RobotContainer;
import frc.robot.commands.teleop.Elevator.MoveElevatorPosition;
import frc.robot.commands.teleop.arm.ArmAssistedCommand;
import frc.robot.commands.teleop.arm.MoveArmPosition;
import frc.robot.subsystems.arm.ArmSubsystem;
import frc.robot.subsystems.elevator.ElevatorSubsystem;
import frc.robot.subsystems.grabber.GrabberSubsystem;

public class MainAssemblyCommandFactory {
    /**
     * Retrives a Command to move the elevator and arm to a specific position.
     * 
     * @param armSubsystem          The arm subsystem
     * @param elevatorSubsystem     The elevator subsystem
     * @param targetElevatorHeight  The target height of the elevator
     * @param targetArmAngle        The target angle for the arm
     * @return                      The command to move the elevator-arm assembly
     */
    public static Command getArmElevatorPositionCommand(
        ArmSubsystem armSubsystem,
        ElevatorSubsystem elevatorSubsystem,
        Distance targetElevatorHeight,
        Rotation2d targetArmAngle
    ) {
        return new ParallelCommandGroup(
            new MoveElevatorPosition(elevatorSubsystem, targetElevatorHeight),
            new MoveArmPosition(armSubsystem, targetArmAngle, elevatorSubsystem::getHeight)
        );
    }
}
