package frc.robot.game.tasks;

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
}
