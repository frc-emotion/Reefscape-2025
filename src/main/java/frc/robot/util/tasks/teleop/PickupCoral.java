package frc.robot.util.tasks.teleop;

import frc.robot.util.tasks.positions.HumanPlayerPosition;

public class PickupCoral extends PickupTask {
    public HumanPlayerPosition pickupPosition;

    public PickupCoral(HumanPlayerPosition pickupPosition) {
        this.pickupPosition = pickupPosition;
    }
}
