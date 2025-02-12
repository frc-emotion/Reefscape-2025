package frc.robot.util.autos.tasks;

import frc.robot.util.autos.positions.AlgaePosition;
import frc.robot.util.autos.positions.AlgaeScorePosition;

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
