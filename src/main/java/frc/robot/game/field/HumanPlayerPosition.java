package frc.robot.game.field;

/**
 * Human player station positions.
 */
public enum HumanPlayerPosition implements Position {
    HP1,
    HP2,
    HP3,
    HP4;

    @Override
    public String getName() {
        return name();
    }
}
