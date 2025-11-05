package frc.robot.game.field;

/**
 * Interface for all field positions.
 * Provides a consistent way to get position names for PathPlanner paths.
 */
public interface Position {
    /**
     * Gets the name of this position for use in path planning.
     * @return The position name
     */
    public String getName();
}
