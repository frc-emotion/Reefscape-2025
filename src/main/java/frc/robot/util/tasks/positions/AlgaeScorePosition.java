package frc.robot.util.tasks.positions;

/**
 * The position to score the algae. Note: Barge position does not matter in teleop.
 */
public enum AlgaeScorePosition implements Position {
    P, // Processor
    R1, // Upper Barge
    R2, // Middle Barge
    R3; // Lower Barge

    @Override
    public String getName() {
        return name();
    }
}
