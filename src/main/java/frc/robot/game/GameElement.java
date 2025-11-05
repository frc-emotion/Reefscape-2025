package frc.robot.game;

/**
 * Defines the game elements and their levels for the 2025 FRC Reefscape game.
 */
public final class GameElement {
    
    /** Coral scoring levels */
    public enum CoralLevel {
        L1, L2, L3, L4
    }
    
    /** Algae scoring/pickup levels */
    public enum AlgaeLevel {
        L2, L3
    }
    
    private GameElement() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
