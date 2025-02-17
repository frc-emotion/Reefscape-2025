package frc.robot.commands.functional;

import static edu.wpi.first.units.Units.Inches;

import java.util.function.Supplier;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.units.measure.Distance;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.Constants.ArmConstants;
import frc.robot.Constants.ElevatorConstants;
import frc.robot.commands.teleop.Elevator.MoveElevatorPosition;
import frc.robot.commands.teleop.Grabber.GrabberGrabCommand;
import frc.robot.commands.teleop.Grabber.GrabberPlaceCommand;
import frc.robot.commands.teleop.arm.MoveArmPosition;
import frc.robot.subsystems.arm.ArmSubsystem;
import frc.robot.subsystems.elevator.ElevatorSubsystem;
import frc.robot.subsystems.grabber.GrabberSubsystem;
import frc.robot.subsystems.grabber.GrabberSubsystem.GrabType;
import frc.robot.util.Configs.ArmConfigs;
import frc.robot.util.Configs.ElevatorConfigs;
import frc.robot.util.tasks.Task;
import frc.robot.util.tasks.auto.ScoreAlgae;
import frc.robot.util.tasks.general.ScoreCoral;
import frc.robot.util.tasks.teleop.PickupAlgae;
import frc.robot.util.tasks.teleop.PickupCoral;
import frc.robot.util.tasks.teleop.PickupTask;

public class MainCommandFactory {
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

    public static Command getPlacePrepCommand(
        ArmSubsystem armSubsystem,
        ElevatorSubsystem elevatorSubsystem,
        Task scoreTask
    ) {
        Distance targetElevatorHeight = Inches.of(0);
        Rotation2d targetArmAngle = new Rotation2d();

        if(scoreTask instanceof ScoreCoral) {
            ScoreCoral scoreCoral = (ScoreCoral) scoreTask;
            
            switch(scoreCoral.level) {
                case L1:
                    targetElevatorHeight = ElevatorConstants.CORAL_L1_HEIGHT;
                    targetArmAngle = ArmConstants.CORAL_L1_ANGLE;
                    break;
                case L2:
                    targetElevatorHeight = ElevatorConstants.CORAL_L2_HEIGHT;
                    targetArmAngle = ArmConstants.CORAL_L2_ANGLE;
                    break;
                case L3:
                    targetElevatorHeight = ElevatorConstants.CORAL_L3_HEIGHT;
                    targetArmAngle = ArmConstants.CORAL_L3_ANGLE;
                    break;
                case L4:
                    targetElevatorHeight = ElevatorConstants.CORAL_L4_HEIGHT;
                    targetArmAngle = ArmConstants.CORAL_L4_ANGLE;
                    break;
            }

        } else if(scoreTask instanceof ScoreAlgae) {
            ScoreAlgae scoreAlgae = (ScoreAlgae) scoreTask;

            switch(scoreAlgae.scorePosition) {
                case P:
                    targetElevatorHeight = ElevatorConstants.ALGAE_PREP_PROCESSOR_HEIGHT;
                    targetArmAngle = ArmConstants.ALGAE_PRO_ANGLE;
                    break;
                case R1, R2, R3:
                    targetElevatorHeight = ElevatorConstants.ALGAE_PREP_NET;
                    targetArmAngle = ArmConstants.ALGAE_NET_ANGLE;
                    break;
            }
        } else {
            return Commands.none();
        }

        return getArmElevatorPositionCommand(armSubsystem, elevatorSubsystem, targetElevatorHeight, targetArmAngle);
    }

    public static Command getPlaceCommand(
        ArmSubsystem armSubsystem,
        ElevatorSubsystem elevatorSubsystem,
        GrabberSubsystem grabberSubsystem,
        Task scoreTask,
        Supplier<Boolean> placeCondition
    ) {
        return new ParallelCommandGroup(
            getPlacePrepCommand(armSubsystem, elevatorSubsystem, scoreTask),
            new SequentialCommandGroup(
                Commands.waitUntil(() -> placeCondition.get()),
                new GrabberPlaceCommand(grabberSubsystem)
            )
        );
    }

    public static Command getIntakeCommand(
        ArmSubsystem armSubsystem,
        ElevatorSubsystem elevatorSubsystem,
        GrabberSubsystem grabberSubsystem,
        PickupTask pickupTask
    ) {
        Distance targetElevatorHeight = null;
        Rotation2d targetArmRotation = null;
        GrabType targetGrabType = GrabType.NONE;

        if(pickupTask instanceof PickupCoral) {
            targetElevatorHeight = ElevatorConstants.CORAL_INTAKE_HEIGHT;
            targetArmRotation = ArmConstants.CORAL_INTAKE_ANGLE;
            targetGrabType = GrabType.CORAL;
        } else if(pickupTask instanceof PickupAlgae) {
            PickupAlgae pickupAlgae = (PickupAlgae) pickupTask;
            targetGrabType = GrabType.ALGAE;
            
            switch(pickupAlgae.targetAlgaeLevel) {
                case L2:
                    targetElevatorHeight = ElevatorConstants.ALGAE_L2_CLEANING;
                    targetArmRotation = ArmConstants.ALGAE_L2_ANGLE;
                    break;
                case L3:
                    targetElevatorHeight = ElevatorConstants.ALGAE_L3_CLEANING;
                    targetArmRotation = ArmConstants.ALGAE_L3_ANGLE;
                    break;
            }
        } else {
            return Commands.none();
        }

        return new SequentialCommandGroup(
            getArmElevatorPositionCommand(armSubsystem, elevatorSubsystem, targetElevatorHeight, targetArmRotation),
            new GrabberGrabCommand(grabberSubsystem, targetGrabType)
        );
    }
}
