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
 * Macro sequence to execute the climb.
 * Should only be run after PrepareClimbSequence.
 * 
 * Sequence:
 * 1. Transition state machine to CLIMBING
 * 2. Move arm to climb angle
 * 3. Retract climb hooks to lift robot
 * 4. Mark climb as complete
 * 
 * Works in both teleop and end-game autonomous.
 */
public class ExecuteClimbSequence extends SequentialCommandGroup {
    
    /**
     * Creates a climb execution sequence.
     * 
     * @param stateMachine The state machine coordinator
     * @param armSubsystem The arm subsystem
     * @param elevatorSubsystem The elevator subsystem
     * @param climbSubsystem The climb subsystem
     */
    public ExecuteClimbSequence(
            SuperstructureStateMachine stateMachine,
            ArmSubsystem armSubsystem,
            ElevatorSubsystem elevatorSubsystem,
            ClimbSubsystem climbSubsystem) {
        
        addCommands(
            // 1. Transition to climbing state
            stateMachine.transitionToClimbing(),
            
            // 2. Move arm to climb angle
            new MoveArmToAngle(
                armSubsystem,
                ArmConstants.CLIMB_ANGLE,
                () -> elevatorSubsystem.getHeight(),
                true),
            
            // 3. Retract climb hooks to lift
            new MoveClimbToPosition(climbSubsystem, ClimbConstants.CLIMBED_POSITION),
            
            // 4. Mark as climbed
            Commands.runOnce(() -> climbSubsystem.setClimbState(ClimbState.CLIMBED))
        );
    }
}
