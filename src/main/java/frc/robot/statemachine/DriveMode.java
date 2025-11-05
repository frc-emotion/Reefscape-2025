package frc.robot.statemachine;

/**
 * Represents the current drive speed mode.
 * The state machine tracks drive mode independently from mechanism states.
 */
public enum DriveMode {
    /**
     * Slow speed for precise positioning and careful maneuvering.
     * Typically used when scoring or intaking.
     */
    SLOW(0.35, "Slow - Precise Control"),
    
    /**
     * Medium speed for general driving.
     * Default mode for most operations.
     */
    MEDIUM(0.5, "Medium - Default Speed"),
    
    /**
     * Turbo speed for fast field traversal.
     * Maximum speed, use when robot is stowed.
     */
    TURBO(0.8, "Turbo - Maximum Speed"),
    
    /**
     * Wheels locked in X formation for defense.
     * Robot cannot move but is very stable.
     */
    LOCKED(0.0, "Locked - Defense Mode");
    
    private final double speedMultiplier;
    private final String description;
    
    DriveMode(double speedMultiplier, String description) {
        this.speedMultiplier = speedMultiplier;
        this.description = description;
    }
    
    /**
     * Gets the speed multiplier for this drive mode.
     * @return Speed multiplier (0.0 to 1.0)
     */
    public double getSpeedMultiplier() {
        return speedMultiplier;
    }
    
    /**
     * Gets a human-readable description of this drive mode.
     * @return Description string
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Gets the next drive mode in sequence (for cycling through modes).
     * @return Next drive mode, wraps around to SLOW after TURBO
     */
    public DriveMode next() {
        return switch (this) {
            case SLOW -> MEDIUM;
            case MEDIUM -> TURBO;
            case TURBO -> SLOW;
            case LOCKED -> SLOW;
        };
    }
    
    /**
     * Checks if the robot can move in this drive mode.
     * @return True if movement is allowed
     */
    public boolean allowsMovement() {
        return this != LOCKED;
    }
}
