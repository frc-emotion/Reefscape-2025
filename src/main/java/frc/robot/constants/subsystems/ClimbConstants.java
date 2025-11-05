// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.constants.subsystems;

/**
 * Constants for the climb mechanism.
 */
public final class ClimbConstants {
    
    // Speed Constants
    public static final double kSpeed = 0.8;
    public static final double SET_SPEED = 0.8;

    // Position Control
    public static final double POSITION_ERROR = 1;
    public static final double EXTENSION_LIMIT = 1;
    public static final double EXTENDED_POSITION = 0;
    public static final double CLIMBED_POSITION = 0;

    // Motor Configuration
    public static final int kSmartCurrentLimit = 45;
    public static final double kSecondaryCurrentLimit = 45;

    private ClimbConstants() {
        throw new UnsupportedOperationException("This is a utility class!");
    }
}
