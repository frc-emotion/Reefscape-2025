// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.constants;

import com.pathplanner.lib.config.PIDConstants;

/**
 * Constants for autonomous mode including PID values for path following.
 */
public final class AutoConstants {
    
    // Default PID values for translation
    private static final double DEFAULT_TRANSLATION_P = 0.005;
    private static final double DEFAULT_TRANSLATION_I = 0.0;
    private static final double DEFAULT_TRANSLATION_D = 0.0;
    
    // Default PID values for rotation
    private static final double DEFAULT_ANGLE_P = 5.0;
    private static final double DEFAULT_ANGLE_I = 0.0;
    private static final double DEFAULT_ANGLE_D = 0.0;
    
    /** PID constants for translation during autonomous */
    public static final PIDConstants TRANSLATION_PID = new PIDConstants(
        DEFAULT_TRANSLATION_P, 
        DEFAULT_TRANSLATION_I, 
        DEFAULT_TRANSLATION_D
    );
    
    /** PID constants for rotation during autonomous */
    public static final PIDConstants ANGLE_PID = new PIDConstants(
        DEFAULT_ANGLE_P, 
        DEFAULT_ANGLE_I, 
        DEFAULT_ANGLE_D
    );

    private AutoConstants() {
        throw new UnsupportedOperationException("This is a utility class!");
    }
}
