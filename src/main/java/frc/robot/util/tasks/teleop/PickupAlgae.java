package frc.robot.util.tasks.teleop;

import frc.robot.util.tasks.Task;
import frc.robot.util.tasks.general.AlgaeLevel;

public class PickupAlgae extends PickupTask {
    public AlgaeLevel targetAlgaeLevel;

    public PickupAlgae(AlgaeLevel algaeLevel) {
        targetAlgaeLevel = algaeLevel;
    }
}
