package frc.robot.game.field;

/**
 * Starting positions for autonomous routines.
 */
public enum StartPosition implements Position {
    S0,
    S1,
    S2,
    S3,
    S4;

    @Override
    public String getName() {
        return name();
    }
}
