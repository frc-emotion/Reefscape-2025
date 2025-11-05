package frc.robot.game.tasks;

import frc.robot.game.Task;
import frc.robot.game.GameElement.AlgaeLevel;
import frc.robot.game.field.AlgaePosition;
import frc.robot.game.field.AlgaeScorePosition;

/**
 * Task for scoring algae at a specific position and level.
 */
public class ScoreAlgae extends Task {
    public final AlgaePosition position;
    public final AlgaeScorePosition scorePosition;
    public final AlgaeLevel level;

    public ScoreAlgae(AlgaePosition position, AlgaeLevel level, AlgaeScorePosition scorePosition) {
        this.position = position;
        this.scorePosition = scorePosition;
        this.level = level;
    }

    public ScoreAlgae(AlgaeScorePosition scorePosition) {
        this(null, null, scorePosition);
    }
}
