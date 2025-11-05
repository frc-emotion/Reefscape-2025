package frc.robot.game.tasks;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.units.measure.Distance;
import frc.robot.constants.subsystems.ArmConstants;
import frc.robot.constants.subsystems.ElevatorConstants;
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

    @Override
    public Distance getElevatorHeight() {
        return switch (scorePosition) {
            case P -> ElevatorConstants.ALGAE_PREP_PROCESSOR_HEIGHT;
            case R1, R2, R3 -> ElevatorConstants.ALGAE_PREP_NET;
        };
    }

    @Override
    public Rotation2d getArmAngle() {
        return switch (scorePosition) {
            case P -> ArmConstants.ALGAE_PRO_ANGLE;
            case R1, R2, R3 -> ArmConstants.ALGAE_NET_ANGLE;
        };
    }
}
