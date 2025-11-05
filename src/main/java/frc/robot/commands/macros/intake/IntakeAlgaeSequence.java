package frc.robot.commands.macros.intake;

import static edu.wpi.first.units.Units.Inches;

import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.commands.atomic.arm.MoveArmToAngle;
import frc.robot.commands.atomic.elevator.MoveElevatorToHeight;
import frc.robot.commands.atomic.grabber.IntakeGamePiece;
import frc.robot.game.GameElement.AlgaeLevel;
import frc.robot.game.field.AlgaePosition;
import frc.robot.game.tasks.PickupAlgae;
import frc.robot.statemachine.SuperstructureStateMachine;
import frc.robot.subsystems.arm.ArmSubsystem;
import frc.robot.subsystems.elevator.ElevatorSubsystem;
import frc.robot.subsystems.grabber.GrabberSubsystem;
import frc.robot.subsystems.grabber.GrabberSubsystem.GrabType;

/**
 * Macro sequence to intake algae from the field.
 * 
 * Sequence:
 * 1. Set grabber type to ALGAE
 * 2. Transition state machine to INTAKING
 * 3. Move arm and elevator to intake position (parallel)
 * 4. Activate intake
 * 5. Wait for game piece detection (or timeout)
 * 6. Transition to HOLDING
 * 
 * Works in both teleop and autonomous modes.
 */
public class IntakeAlgaeSequence extends SequentialCommandGroup {
    
    /**
     * Creates an algae intake sequence.
     * 
     * @param stateMachine The state machine coordinator
     * @param armSubsystem The arm subsystem
     * @param elevatorSubsystem The elevator subsystem
     * @param grabberSubsystem The grabber subsystem
     * @param algaePosition The algae field position
     * @param targetLevel The target algae level (L2, L3, or ground)
     */
    public IntakeAlgaeSequence(
            SuperstructureStateMachine stateMachine,
            ArmSubsystem armSubsystem,
            ElevatorSubsystem elevatorSubsystem,
            GrabberSubsystem grabberSubsystem,
            AlgaePosition algaePosition,
            AlgaeLevel targetLevel) {
        
        // Create task to get positions
        PickupAlgae task = new PickupAlgae(algaePosition, targetLevel);
        
        addCommands(
            // 1. Set target type
            Commands.runOnce(() -> grabberSubsystem.setTargetType(GrabType.ALGAE)),
            
            // 2. Transition to intaking state
            stateMachine.transitionToIntaking(task),
            
            // 3. Move to intake position (parallel)
            new ParallelCommandGroup(
                new MoveElevatorToHeight(elevatorSubsystem, task.getElevatorHeight(), true),
                new MoveArmToAngle(armSubsystem, task.getArmAngle(), () -> Inches.of(90), true)
            ),
            
            // 4. Activate intake until game piece acquired (max 2 seconds)
            new IntakeGamePiece(grabberSubsystem, true).withTimeout(2.0),
            
            // 5. Transition to holding
            stateMachine.transitionToHolding()
        );
    }
}
