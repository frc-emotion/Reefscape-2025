package frc.robot.commands.macros.scoring;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.commands.atomic.SafeMoveToPosition;
import frc.robot.commands.atomic.grabber.EjectGamePiece;
import frc.robot.game.field.AlgaeScorePosition;
import frc.robot.game.tasks.ScoreAlgae;
import frc.robot.statemachine.SuperstructureStateMachine;
import frc.robot.subsystems.arm.ArmSubsystem;
import frc.robot.subsystems.elevator.ElevatorSubsystem;
import frc.robot.subsystems.grabber.GrabberSubsystem;

/**
 * Macro sequence to score algae at a specific position.
 * 
 * Sequence:
 * 1. Transition state machine to POSITIONING
 * 2. Move arm and elevator to target position (parallel)
 * 3. Wait until at setpoint
 * 4. Transition to SCORING
 * 5. Eject game piece
 * 6. Transition back to IDLE
 * 
 * Works in both teleop and autonomous modes.
 */
public class ScoreAlgaeSequence extends SequentialCommandGroup {
    
    /**
     * Creates an algae scoring sequence for a specific position.
     * 
     * @param stateMachine The state machine coordinator
     * @param armSubsystem The arm subsystem
     * @param elevatorSubsystem The elevator subsystem
     * @param grabberSubsystem The grabber subsystem
     * @param scorePosition The algae score position (P, R1, R2, R3)
     */
    public ScoreAlgaeSequence(
            SuperstructureStateMachine stateMachine,
            ArmSubsystem armSubsystem,
            ElevatorSubsystem elevatorSubsystem,
            GrabberSubsystem grabberSubsystem,
            AlgaeScorePosition scorePosition) {
        
        // Create task to get positions
        ScoreAlgae task = new ScoreAlgae(scorePosition);
        
        addCommands(
            // 1. Safe anti-tip movement: vertical -> elevator -> dunk (YAMS handles completion)
            new SafeMoveToPosition(
                armSubsystem,
                elevatorSubsystem,
                task.getArmAngle(),
                task.getElevatorHeight()
            ),
            
            // 2. Eject game piece (run for 0.5 seconds)
            new EjectGamePiece(grabberSubsystem).withTimeout(0.5)
            
            // That's it! hasGamePiece auto-updates from sensors in state machine periodic()
        );
    }
}
