package frc.robot.game.tasks;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.units.measure.Distance;
import frc.robot.constants.subsystems.ArmConstants;
import frc.robot.constants.subsystems.ElevatorConstants;
import frc.robot.game.GameElement.AlgaeLevel;
import frc.robot.game.field.AlgaePosition;

/**
 * Task for picking up algae from the field.
 */
public class PickupAlgae extends PickupTask {
    public final AlgaePosition algaePosition;
    public final AlgaeLevel targetAlgaeLevel;

    public PickupAlgae(AlgaePosition algaePosition, AlgaeLevel targetAlgaeLevel) {
        this.algaePosition = algaePosition;
        this.targetAlgaeLevel = targetAlgaeLevel;
    }

    @Override
    public Distance getElevatorHeight() {
        if (targetAlgaeLevel == null) {
            return ElevatorConstants.ALGAE_GROUND_INTAKE;
        }
        return switch (targetAlgaeLevel) {
            case L2 -> ElevatorConstants.ALGAE_L2_CLEANING;
            case L3 -> ElevatorConstants.ALGAE_L3_CLEANING;
            default -> ElevatorConstants.ALGAE_GROUND_INTAKE;
        };
    }

    @Override
    public Rotation2d getArmAngle() {
        if (targetAlgaeLevel == null) {
            return ArmConstants.CORAL_INTAKE_ANGLE;
        }
        return switch (targetAlgaeLevel) {
            case L2 -> ArmConstants.ALGAE_L2_ANGLE;
            case L3 -> ArmConstants.ALGAE_L3_ANGLE;
            default -> ArmConstants.CORAL_INTAKE_ANGLE;
        };
    }
}
