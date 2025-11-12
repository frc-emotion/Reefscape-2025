package frc.robot.statemachine;

/**
 * Simplified robot state tracking.
 * 
 * Since YAMS commands handle all mechanism coordination (intaking, positioning, scoring),
 * we only need to track high-level operational modes.
 */
public enum RobotState {
    /**
     * Normal operation mode.
     * Robot is ready for any task - YAMS commands handle the specifics.
     * This replaces: IDLE, STOWED, INTAKING, HOLDING, POSITIONING, SCORING.
     */
    READY,
    
    /**
     * End-game climbing mode.
     * Locks out all other mechanism operations for safety.
     */
    CLIMBING,
    
    /**
     * Manual control override mode.
     * State machine monitors only, operator has direct control.
     */
    MANUAL;
    
    /**
     * Checks if manual override can be enabled from this state.
     * @return True if manual control is allowed
     */
    public boolean allowsManualOverride() {
        return this != CLIMBING;
    }
}
