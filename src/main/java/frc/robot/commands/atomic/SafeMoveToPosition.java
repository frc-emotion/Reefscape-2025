package frc.robot.commands.atomic;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.units.measure.Distance;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.commands.atomic.arm.MoveArmToAngle;
import frc.robot.commands.atomic.elevator.MoveElevatorToHeight;
import frc.robot.constants.subsystems.ArmConstants;
import frc.robot.subsystems.arm.ArmSubsystem;
import frc.robot.subsystems.elevator.ElevatorSubsystem;

/**
 * Safe movement command that prevents robot tipping during transitions.
 * 
 * STRATEGY (Anti-Tip):
 * 1. Move arm to VERTICAL position (~25°) - arm is upright, stable
 * 2. Move elevator to target height - robot stays balanced
 * 3. "Dunk" arm to final angle - controlled, won't tip
 * 
 * This simple sequence prevents jerky movements and keeps the robot stable.
 * No complex path planning - just safe, predictable movements.
 */
public class SafeMoveToPosition extends SequentialCommandGroup {
    
    /**
     * Creates safe movement sequence.
     * Always sequences: vertical arm -> elevator move -> arm dunk.
     * 
     * @param arm Arm subsystem
     * @param elevator Elevator subsystem
     * @param targetArm Final arm angle (will "dunk" to this)
     * @param targetElev Target elevator height
     */
    public SafeMoveToPosition(
        ArmSubsystem arm,
        ElevatorSubsystem elevator,
        Rotation2d targetArm,
        Distance targetElev
    ) {
        addCommands(
            // 1. Move arm to safe vertical position (won't tip robot)
            new MoveArmToAngle(arm, ArmConstants.SAFE_TRAVEL_ANGLE),
            
            // 2. Move elevator to target height (arm is stable/vertical)
            new MoveElevatorToHeight(elevator, targetElev),
            
            // 3. "Dunk" arm to final scoring position (elevator already in place)
            new MoveArmToAngle(arm, targetArm)
        );
    }
}
