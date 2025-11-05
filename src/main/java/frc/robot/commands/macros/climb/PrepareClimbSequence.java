package frc.robot.commands.macros.climb;

import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.commands.atomic.arm.MoveArmToAngle;
import frc.robot.commands.atomic.climb.MoveClimbToPosition;
import frc.robot.constants.subsystems.ArmConstants;
import frc.robot.constants.subsystems.ClimbConstants;
import frc.robot.statemachine.SuperstructureStateMachine;
import frc.robot.subsystems.arm.ArmSubsystem;
import frc.robot.subsystems.climb.ClimbSubsystem;
import frc.robot.subsystems.climb.ClimbSubsystem.ClimbState;
import frc.robot.subsystems.elevator.ElevatorSubsystem;

/**
 * Macro sequence to prepare the robot for climbing.
 * 
 * Sequence:
 * 1. Transition state machine to CLIMBING_PREP
 * 2. Move arm to climb angle
 * 3. Extend climb hooks to reach bar
 * 4. Move arm back to safe position
 * 5. Mark climb as ready
 * 
 * Works in both teleop and end-game autonomous.
 */
public class PrepareClimbSequence extends SequentialCommandGroup {
    
    /**
     * Creates a climb preparation sequence.
     * 
     * @param stateMachine The state machine coordinator
     * @param armSubsystem The arm subsystem
     * @param elevatorSubsystem The elevator subsystem
     * @param climbSubsystem The climb subsystem
     */
    public PrepareClimbSequence(
            SuperstructureStateMachine stateMachine,
            ArmSubsystem armSubsystem,
            ElevatorSubsystem elevatorSubsystem,
            ClimbSubsystem climbSubsystem) {
        
        addCommands(
            // 1. Transition to climb prep state
            stateMachine.transitionToClimbPrep(),
            
            // 2. Move arm to climb angle
            new MoveArmToAngle(
                armSubsystem,
                ArmConstants.CLIMB_ANGLE,
                () -> elevatorSubsystem.getHeight(),
                true),
            
            // 3. Extend climb hooks
            new MoveClimbToPosition(climbSubsystem, ClimbConstants.EXTENSION_LIMIT),
            
            // 4. Move arm to safe position
            new MoveArmToAngle(
                armSubsystem,
                ArmConstants.CORAL_INTAKE_ANGLE,
                () -> elevatorSubsystem.getHeight(),
                true),
            
            // 5. Mark as ready
            Commands.runOnce(() -> climbSubsystem.setClimbState(ClimbState.READY))
        );
    }
}
