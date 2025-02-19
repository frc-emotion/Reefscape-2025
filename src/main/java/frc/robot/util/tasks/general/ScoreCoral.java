package frc.robot.util.tasks.general;

import frc.robot.util.tasks.Task;
import frc.robot.util.tasks.positions.CoralPosition;

public class ScoreCoral extends Task {
    public final CoralPosition position;
    public final CoralLevel level;

    public ScoreCoral(CoralPosition position, CoralLevel level) {
        this.position = position;
        this.level = level;
    }
}
