// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.constants;

import com.pathplanner.lib.config.PIDConstants;

/**
 * Constants for autonomous mode including PID values for path following.
 */
public final class AutoConstants {
    
    // Translation PID values (tune these for path following accuracy)
    // Start with kP = 5.0, increase if robot is slow to correct position errors
    // Decrease if robot oscillates around the path
    private static final double DEFAULT_TRANSLATION_P = 5.0;  // Was 0.005 - FIXED!
    private static final double DEFAULT_TRANSLATION_I = 0.0;
    private static final double DEFAULT_TRANSLATION_D = 0.0;
    
    // Rotation PID values (tune these for heading accuracy)
    // kP = 5.0 is a good starting point for most swerve drives
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
