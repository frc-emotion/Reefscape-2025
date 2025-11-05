package frc.robot.commands.macros.scoring;

import static edu.wpi.first.units.Units.Inches;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitUntilCommand;
import frc.robot.commands.atomic.arm.MoveArmToAngle;
import frc.robot.commands.atomic.elevator.MoveElevatorToHeight;
import frc.robot.commands.atomic.grabber.EjectGamePiece;
import frc.robot.game.GameElement.CoralLevel;
import frc.robot.game.tasks.ScoreCoral;
import frc.robot.statemachine.SuperstructureStateMachine;
import frc.robot.subsystems.arm.ArmSubsystem;
import frc.robot.subsystems.elevator.ElevatorSubsystem;
import frc.robot.subsystems.grabber.GrabberSubsystem;

/**
 * Macro sequence to score coral at a specific level.
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
public class ScoreCoralSequence extends SequentialCommandGroup {
    
    /**
     * Creates a coral scoring sequence for a specific level.
     * 
     * @param stateMachine The state machine coordinator
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
            // 1. Transition to positioning state
            stateMachine.transitionToPositioning(task),
            
            // 2. Move to scoring position (parallel)
            new ParallelCommandGroup(
                new MoveElevatorToHeight(elevatorSubsystem, task.getElevatorHeight(), true),
                new MoveArmToAngle(armSubsystem, task.getArmAngle(), () -> Inches.of(90), true)
            ),
            
            // 3. Transition to scoring
            stateMachine.transitionToScoring(),
            
            // 4. Eject game piece (run for 0.5 seconds)
            new EjectGamePiece(grabberSubsystem).withTimeout(0.5),
            
            // 5. Return to idle
            stateMachine.transitionScoringComplete()
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
