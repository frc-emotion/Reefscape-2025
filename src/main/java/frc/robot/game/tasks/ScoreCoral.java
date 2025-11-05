package frc.robot.game.tasks;

import frc.robot.game.Task;
import frc.robot.game.GameElement.CoralLevel;
import frc.robot.game.field.CoralPosition;

/**
 * Task for scoring coral at a specific position and level.
 */
public class ScoreCoral extends Task {
    public final CoralPosition position;
    public final CoralLevel level;

    public ScoreCoral(CoralPosition position, CoralLevel level) {
        this.position = position;
        this.level = level;
    }

    public ScoreCoral(CoralLevel coralLevel) {
        this(null, coralLevel);
    }
}
