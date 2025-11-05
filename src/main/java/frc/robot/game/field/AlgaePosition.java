package frc.robot.game.field;

/**
 * Algae field positions.
 */
public enum AlgaePosition implements Position {
    AB,
    CD,
    EF,
    GH,
    IJ,
    KL;

    @Override
    public String getName() {
        return name();
    }
}
