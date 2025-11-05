package frc.robot.game.tasks;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.units.measure.Distance;
import frc.robot.constants.subsystems.ArmConstants;
import frc.robot.constants.subsystems.ElevatorConstants;
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

    @Override
    public Distance getElevatorHeight() {
        return switch (level) {
            case L1 -> ElevatorConstants.CORAL_L1_HEIGHT;
            case L2 -> ElevatorConstants.CORAL_L2_HEIGHT;
            case L3 -> ElevatorConstants.CORAL_L3_HEIGHT;
            case L4 -> ElevatorConstants.CORAL_L4_HEIGHT;
        };
    }

    @Override
    public Rotation2d getArmAngle() {
        return switch (level) {
            case L1 -> ArmConstants.CORAL_L1_ANGLE;
            case L2 -> ArmConstants.CORAL_L2_ANGLE;
            case L3 -> ArmConstants.CORAL_L3_ANGLE;
            case L4 -> ArmConstants.CORAL_L4_ANGLE;
        };
    }
}
