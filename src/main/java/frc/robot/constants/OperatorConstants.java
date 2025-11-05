// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.constants;

/**
 * Constants for operator input including deadbands and sensitivity values.
 */
public final class OperatorConstants {
    
    /** General joystick deadband */
    public static final double DEADBAND = 0.1;
    
    /** Left Y-axis deadband */
    public static final double LEFT_Y_DEADBAND = 0.1;
    
    /** Right X-axis deadband */
    public static final double RIGHT_X_DEADBAND = 0.1;
    
    /** Turn scaling constant */
    public static final double TURN_CONSTANT = 6;

    private OperatorConstants() {
        throw new UnsupportedOperationException("This is a utility class!");
    }
}
