package frc.robot.game.field;

/**
 * Algae scoring positions.
 * Note: Barge position does not matter in teleop.
 */
public enum AlgaeScorePosition implements Position {
    P,  // Processor
    R1, // Upper Barge
    R2, // Middle Barge
    R3; // Lower Barge

    @Override
    public String getName() {
        return name();
    }
}
