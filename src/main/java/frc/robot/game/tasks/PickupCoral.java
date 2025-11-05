package frc.robot.game.tasks;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.units.measure.Distance;
import frc.robot.constants.subsystems.ArmConstants;
import frc.robot.constants.subsystems.ElevatorConstants;
import frc.robot.game.field.HumanPlayerPosition;

/**
 * Task for picking up coral from the human player station.
 */
public class PickupCoral extends PickupTask {
    public HumanPlayerPosition pickupPosition;

    public PickupCoral(HumanPlayerPosition pickupPosition) {
        this.pickupPosition = pickupPosition;
    }

    @Override
    public Distance getElevatorHeight() {
        return ElevatorConstants.CORAL_INTAKE_HEIGHT;
    }

    @Override
    public Rotation2d getArmAngle() {
        return ArmConstants.CORAL_INTAKE_ANGLE;
    }
}
