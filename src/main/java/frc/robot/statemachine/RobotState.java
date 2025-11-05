package frc.robot.statemachine;

/**
 * Represents the current operational state of the robot's mechanisms.
 * The state machine uses these states to coordinate subsystems and ensure safe transitions.
 */
public enum RobotState {
    /**
     * Robot is idle, mechanisms at rest or in default positions.
     * Safe to transition to any other state.
     */
    IDLE,
    
    /**
     * Robot mechanisms are stowed compactly for driving.
     * Optimized for low center of gravity and maneuverability.
     */
    STOWED,
    
    /**
     * Robot is actively intaking a game piece.
     * Arm/elevator moving to pickup position, grabber activating.
     */
    INTAKING,
    
    /**
     * Robot has successfully picked up and is holding a game piece.
     * Ready to transition to scoring or stowing.
     */
    HOLDING,
    
    /**
     * Robot is moving to a scoring position.
     * Arm and elevator moving to target heights for placement.
     */
    POSITIONING,
    
    /**
     * Robot is actively scoring a game piece.
     * At target position, releasing game piece.
     */
    SCORING,
    
    /**
     * Robot is preparing for end-game climb.
     * Moving mechanisms to climb-ready configuration.
     */
    CLIMBING_PREP,
    
    /**
     * Robot is actively climbing.
     * Climb hooks engaged, lifting robot.
     */
    CLIMBING,
    
    /**
     * Manual control override is active.
     * State machine monitoring only, no automatic control.
     */
    MANUAL_OVERRIDE;
    
    /**
     * Checks if this state represents an active operation (not idle/stowed).
     * @return True if the robot is actively doing something
     */
    public boolean isActive() {
        return this != IDLE && this != STOWED && this != MANUAL_OVERRIDE;
    }
    
    /**
     * Checks if this state allows manual override.
     * @return True if manual control can be enabled from this state
     */
    public boolean allowsManualOverride() {
        return this != CLIMBING && this != CLIMBING_PREP;
    }
    
    /**
     * Checks if it's safe to drive at high speed in this state.
     * @return True if the robot can safely drive quickly
     */
    public boolean isSafeForHighSpeed() {
        return this == IDLE || this == STOWED || this == HOLDING;
    }
}
