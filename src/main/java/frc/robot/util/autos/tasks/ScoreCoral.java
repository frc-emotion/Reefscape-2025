package frc.robot.util.autos.tasks;

import frc.robot.util.autos.positions.CoralPosition;

public class ScoreCoral extends Task {
    public final CoralPosition position;
    public final CoralLevel level;

    public ScoreCoral(CoralPosition position, CoralLevel level) {
        this.position = position;
        this.level = level;
    }
}
