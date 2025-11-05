package frc.robot.game.tasks;

import frc.robot.game.field.HumanPlayerPosition;

/**
 * Task for picking up coral from the human player station.
 */
public class PickupCoral extends PickupTask {
    public HumanPlayerPosition pickupPosition;

    public PickupCoral(HumanPlayerPosition pickupPosition) {
        this.pickupPosition = pickupPosition;
    }
}
