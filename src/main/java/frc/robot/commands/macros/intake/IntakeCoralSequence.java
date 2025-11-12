package frc.robot.commands.macros.intake;

import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.commands.atomic.grabber.IntakeGamePiece;
import frc.robot.commands.atomic.SafeMoveToPosition;
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
 * 2. Transition state machine to POSITIONING
 * 3. Safe anti-tip movement to intake position
 * 4. Transition to INTAKING
 * 5. Activate intake
 * 6. Wait for game piece detection (or timeout)
 * 7. Transition to HOLDING
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
        
        var intakeCommand = new IntakeGamePiece(grabberSubsystem, true).withTimeout(2.0);
        
        addCommands(
            // 1. Set target type
            Commands.runOnce(() -> grabberSubsystem.setTargetType(GrabType.CORAL)),
            
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
