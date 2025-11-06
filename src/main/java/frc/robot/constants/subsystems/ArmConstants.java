// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.constants.subsystems;

import edu.wpi.first.math.geometry.Rotation2d;

/**
 * Constants for the arm subsystem including physical limits, PID values, and preset positions.
 */
public final class ArmConstants {
    
    // Motor Configuration
    public static final int kSmartCurrentLimit = 45;

    // Physical Constraints
    /** Minimum arm rotation in degrees */
    public static final double kMinRotation = -45;
    /** Maximum arm rotation in degrees */
    public static final double kMaxRotation = 125;

    // Feedforward Constants
    public static final double kS = 0; // Static friction (volts)
    public static final double kG = 0.088858; // Gravity compensation (volts)
    public static final double kV = 0.00025; // Velocity gain (volts per deg/s)
    public static final double kA = 0.001; // Acceleration gain (volts per deg/s²)

    // PID Constants
    public static final double kP = 0.002058;
    public static final double kI = 0;
    public static final double kD = 0;


    // Coral Scoring Presets
    public static final Rotation2d CORAL_L1_ANGLE = Rotation2d.fromDegrees(95);
    public static final Rotation2d CORAL_L2_ANGLE = Rotation2d.fromDegrees(95);
    public static final Rotation2d CORAL_L3_ANGLE = Rotation2d.fromDegrees(95);
    public static final Rotation2d CORAL_L4_ANGLE = Rotation2d.fromDegrees(100);
    public static final Rotation2d CORAL_INTAKE_ANGLE = Rotation2d.fromDegrees(125);

    // Algae Presets
    public static final Rotation2d ALGAE_L2_ANGLE = Rotation2d.fromDegrees(-30);
    public static final Rotation2d ALGAE_L3_ANGLE = Rotation2d.fromDegrees(-30);
    public static final Rotation2d ALGAE_GROUND_ANGLE = new Rotation2d(0);
    public static final Rotation2d ALGAE_ON_CORAL_ANGLE = new Rotation2d(0);
    public static final Rotation2d ALGAE_NET_ANGLE = new Rotation2d(0);
    public static final Rotation2d ALGAE_PRO_ANGLE = new Rotation2d(0);

    // Climb Preset
    public static final Rotation2d CLIMB_ANGLE = new Rotation2d(0);

    // Diagnostic Thresholds
    public static final int NORMAL_OPERATION_CURRENT = 30; // amps
    public static final int NORMAL_OPERATION_TEMP = 40; // celsius
    public static final int CURRENT_SPIKE_THRESHOLD = 20; // amps
    public static final int TEMP_SPIKE_THRESHOLD = 20; // celsius

    private ArmConstants() {
        throw new UnsupportedOperationException("This is a utility class!");
    }
}
