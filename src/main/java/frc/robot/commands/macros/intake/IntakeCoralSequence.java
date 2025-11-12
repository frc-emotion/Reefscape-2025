package frc.robot.commands.macros.intake;

import static edu.wpi.first.units.Units.Inches;

import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.commands.atomic.arm.MoveArmToAngle;
import frc.robot.commands.atomic.elevator.MoveElevatorToHeight;
import frc.robot.commands.atomic.grabber.IntakeGamePiece;
import frc.robot.game.field.HumanPlayerPosition;
import frc.robot.game.tasks.PickupCoral;
import frc.robot.statemachine.SuperstructureStateMachine;
import frc.robot.subsystems.arm.ArmSubsystem;
import frc.robot.subsystems.elevator.ElevatorSubsystem;
import frc.robot.subsystems.grabber.GrabberSubsystem;
import frc.robot.subsystems.grabber.GrabberSubsystem.GrabType;

/**
 * Macro sequence to intake coral from human player station.
 * 
 * Sequence:
 * 1. Set grabber type to CORAL
 * 2. Transition state machine to INTAKING
 * 3. Move arm and elevator to intake position (parallel)
 * 4. Activate intake
 * 5. Wait for game piece detection (or timeout)
 * 6. Transition to HOLDING
 * 
 * Works in both teleop and autonomous modes.
 */
public class IntakeCoralSequence extends SequentialCommandGroup {
    
    /**
     * Creates a coral intake sequence.
     * 
     * @param stateMachine The state machine coordinator
     * @param armSubsystem The arm subsystem
     * @param elevatorSubsystem The elevator subsystem
     * @param grabberSubsystem The grabber subsystem
     * @param humanPlayerPosition The human player station position
     */
    public IntakeCoralSequence(
            SuperstructureStateMachine stateMachine,
            ArmSubsystem armSubsystem,
            ElevatorSubsystem elevatorSubsystem,
            GrabberSubsystem grabberSubsystem,
            HumanPlayerPosition humanPlayerPosition) {
        
        // Create task to get positions
        PickupCoral task = new PickupCoral(humanPlayerPosition);
        
        addCommands(
            // 1. Set target type
            Commands.runOnce(() -> grabberSubsystem.setTargetType(GrabType.CORAL)),
            
            // 2. Transition to intaking state
            stateMachine.transitionToIntaking(task),
            
            // 3. Move to intake position (parallel)
            new ParallelCommandGroup(
                new MoveElevatorToHeight(elevatorSubsystem, task.getElevatorHeight()),
                new MoveArmToAngle(armSubsystem, task.getArmAngle())
            ),
            
            // 4. Activate intake until game piece acquired (max 2 seconds)
            new IntakeGamePiece(grabberSubsystem, true).withTimeout(2.0),
            
            // 5. Transition to holding
            stateMachine.transitionToHolding()
        );
    }
}
