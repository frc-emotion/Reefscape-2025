// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.constants.subsystems;

/**
 * Constants for the grabber/intake subsystem for coral and algae game pieces.
 */
public final class GrabberConstants {
    
    // Motor Configuration
    public static final int kSmartCurrentLimit = 35;
    public static final double kSecondaryCurrentLimit = 35;

    // Coral Speeds
    public static final double GRABBER_CORAL_OUTTAKE = -0.65;
    public static final double GRABBER_CORAL_INTAKE = GRABBER_CORAL_OUTTAKE / 2.5;
    public static final double GRABBER_CORAL_HOLD_SPEED = 0.3;

    // Algae Speeds
    public static final double GRABBER_ALGAE_SPEED = -0.65;
    public static final double GRABBER_ALGAE_HOLD_SPEED = 0.1;

    // General
    public static final double ZERO_SPEED = 0;
    public static final double CORAL_DETECT_RANGE = 0;

    // Diagnostic Thresholds
    public static final int NORMAL_OPERATION_CURRENT = 40; // amps
    public static final int CURRENT_SPIKE_THRESHOLD = 20; // amps
    public static final int NORMAL_OPERATION_TEMP = 40; // celsius
    public static final int TEMP_SPIKE_THRESHOLD = 20; // celsius

    private GrabberConstants() {
        throw new UnsupportedOperationException("This is a utility class!");
    }
}
