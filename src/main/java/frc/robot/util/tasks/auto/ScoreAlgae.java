package frc.robot.util.tasks.auto;

import frc.robot.util.tasks.Task;
import frc.robot.util.tasks.general.AlgaeLevel;
import frc.robot.util.tasks.positions.AlgaePosition;
import frc.robot.util.tasks.positions.AlgaeScorePosition;

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
