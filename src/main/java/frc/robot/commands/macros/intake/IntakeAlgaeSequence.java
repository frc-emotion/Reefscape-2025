package frc.robot.commands.macros.intake;

import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.commands.atomic.grabber.IntakeGamePiece;
import frc.robot.commands.atomic.SafeMoveToPosition;
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
 * 2. Transition state machine to POSITIONING
 * 3. Safe anti-tip movement to intake position
 * 4. Transition state machine to INTAKING
 * 5. Activate intake
 * 6. Wait for game piece detection (or timeout)
 * 7. Transition to HOLDING
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
        
        var intakeCommand = new IntakeGamePiece(grabberSubsystem, true).withTimeout(2.0);
        
        addCommands(
            // 1. Set target type
            Commands.runOnce(() -> grabberSubsystem.setTargetType(GrabType.ALGAE)),
            
            // 2. Safe anti-tip movement to intake position (YAMS handles completion)
            new SafeMoveToPosition(
                armSubsystem,
                elevatorSubsystem,
                task.getArmAngle(),
                task.getElevatorHeight()
            ),
            
            // 3. Run intake until game piece detected (timeout after 2s)
            intakeCommand
            
            // That's it! hasGamePiece auto-updates from sensors in state machine periodic()
        );
    }
}
