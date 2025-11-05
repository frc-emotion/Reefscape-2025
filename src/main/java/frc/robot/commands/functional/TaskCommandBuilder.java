package frc.robot.commands.functional;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.units.measure.Distance;

/**
 * Interface for building commands based on task types.
 * Provides position information for arm and elevator based on the task.
 */
public interface TaskCommandBuilder {
    
    /**
     * Gets the target elevator height for this task.
     * @return Target height for the elevator
     */
    Distance getElevatorHeight();
    
    /**
     * Gets the target arm angle for this task.
     * @return Target angle for the arm
     */
    Rotation2d getArmAngle();
    
    /**
     * Simple data class to hold arm/elevator positions.
     */
    public static class ArmElevatorPosition {
        public final Distance elevatorHeight;
        public final Rotation2d armAngle;
        
        public ArmElevatorPosition(Distance elevatorHeight, Rotation2d armAngle) {
            this.elevatorHeight = elevatorHeight;
            this.armAngle = armAngle;
        }
    }
}
