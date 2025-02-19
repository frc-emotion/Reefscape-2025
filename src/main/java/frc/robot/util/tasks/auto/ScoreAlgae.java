package frc.robot.util.tasks.auto;

import frc.robot.util.positions.AlgaePosition;
import frc.robot.util.positions.AlgaeScorePosition;
import frc.robot.util.tasks.AlgaeLevel;
import frc.robot.util.tasks.Task;

public class ScoreAlgae extends Task {
    public final AlgaePosition position;
    public final AlgaeScorePosition scorePosition;
    public final AlgaeLevel level;

    public ScoreAlgae(AlgaePosition position, AlgaeLevel level, AlgaeScorePosition scorePosition) {
        this.position = position;
        this.scorePosition = scorePosition;
        this.level = level;
    }
}
