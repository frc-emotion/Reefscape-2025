package frc.robot.game;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.units.measure.Distance;

/**
 * Base class for all robot tasks (scoring, picking up game pieces, etc.).
 * Tasks represent high-level robot objectives that can be executed in autonomous or teleop.
 * Each task provides the required arm angle and elevator height for execution.
 */
public abstract class Task {
    
    /**
     * Gets the required elevator height for this task.
     * @return The target elevator height, or null if not applicable
     */
    public abstract Distance getElevatorHeight();
    
    /**
     * Gets the required arm angle for this task.
     * @return The target arm angle, or null if not applicable
     */
    public abstract Rotation2d getArmAngle();
}
