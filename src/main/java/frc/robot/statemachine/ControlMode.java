package frc.robot.statemachine;

/**
 * Represents whether the robot is in manual joystick control or macro/automated control.
 */
public enum ControlMode {
    /**
     * Manual mode - operator has direct joystick control of all mechanisms.
     * State machine monitors but does not control subsystems.
     * Drive controls remain active.
     */
    MANUAL("Manual - Direct Control"),
    
    /**
     * Macro mode - state machine coordinates subsystems automatically.
     * Button presses trigger multi-step sequences.
     * Recommended for competition.
     */
    MACRO("Macro - Automated Sequences");
    
    private final String description;
    
    ControlMode(String description) {
        this.description = description;
    }
    
    /**
     * Gets a human-readable description of this control mode.
     * @return Description string
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Toggles between manual and macro mode.
     * @return The opposite control mode
     */
    public ControlMode toggle() {
        return this == MANUAL ? MACRO : MANUAL;
    }
}
