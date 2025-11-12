package frc.robot.commands.macros.scoring;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.commands.atomic.SafeMoveToPosition;
import frc.robot.commands.atomic.grabber.EjectGamePiece;
import frc.robot.game.GameElement.CoralLevel;
import frc.robot.game.tasks.ScoreCoral;
import frc.robot.statemachine.SuperstructureStateMachine;
import frc.robot.subsystems.arm.ArmSubsystem;
import frc.robot.subsystems.elevator.ElevatorSubsystem;
import frc.robot.subsystems.grabber.GrabberSubsystem;

/**
 * Simplified macro sequence to score coral at a specific level.
 * 
 * Sequence:
 * 1. Safe anti-tip movement: Arm vertical -> Elevator move -> Arm dunk (prevents tipping!)
 * 2. Eject game piece
 * 
 * Game piece detection automatically updates in state machine periodic().
 * YAMS commands handle their own completion and lifecycle.
 * Works in both teleop and autonomous modes.
 */
public class ScoreCoralSequence extends SequentialCommandGroup {
    
    /**
     * Creates a coral scoring sequence for a specific level.
     * 
     * @param stateMachine The state machine coordinator (kept for compatibility)
     * @param armSubsystem The arm subsystem
     * @param elevatorSubsystem The elevator subsystem
     * @param grabberSubsystem The grabber subsystem
     * @param level The coral level to score at (L1-L4)
     */
    public ScoreCoralSequence(
            SuperstructureStateMachine stateMachine,
            ArmSubsystem armSubsystem,
            ElevatorSubsystem elevatorSubsystem,
            GrabberSubsystem grabberSubsystem,
            CoralLevel level) {
        
        // Create task to get positions
        ScoreCoral task = new ScoreCoral(level);
        
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
    
    /**
     * Convenience constructor that auto-finishes when complete.
     * Used primarily in autonomous.
     * 
     * @param stateMachine The state machine coordinator
     * @param armSubsystem The arm subsystem
     * @param elevatorSubsystem The elevator subsystem
     * @param grabberSubsystem The grabber subsystem
     * @param level The coral level to score at
     * @param waitForCompletion If true, waits for scoring to complete
     */
    public ScoreCoralSequence(
            SuperstructureStateMachine stateMachine,
            ArmSubsystem armSubsystem,
            ElevatorSubsystem elevatorSubsystem,
            GrabberSubsystem grabberSubsystem,
            CoralLevel level,
            boolean waitForCompletion) {
        this(stateMachine, armSubsystem, elevatorSubsystem, grabberSubsystem, level);
        
        if (!waitForCompletion) {
            // Exit early for teleop - operator can interrupt
            this.withTimeout(0.01);
        }
    }
}
