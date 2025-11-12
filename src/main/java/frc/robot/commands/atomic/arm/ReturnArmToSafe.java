package frc.robot.commands.atomic.arm;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.constants.subsystems.ArmConstants;
import frc.robot.subsystems.arm.ArmSubsystem;

/**
 * Emergency command to quickly return arm to safe vertical position.
 * 
 * Use this if:
 * - Arm accidentally goes too low while elevator is up
 * - Driver needs to quickly reset to safe position
 * - Recovering from a manual control mistake
 * 
 * This is a one-shot instant command - fires once and finishes immediately.
 */
public class ReturnArmToSafe extends Command {
    private final ArmSubsystem arm;
    
    public ReturnArmToSafe(ArmSubsystem arm) {
        this.arm = arm;
        addRequirements(arm);
    }
    
    @Override
    public void initialize() {
        // Immediately command arm to safe vertical position
        new MoveArmToAngle(arm, ArmConstants.SAFE_TRAVEL_ANGLE).schedule();
    }
    
    @Override
    public boolean isFinished() {
        // Finish immediately - the MoveArmToAngle command will handle the rest
        return true;
    }
}
