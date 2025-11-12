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
    public static final double kSecondaryCurrentLimit = 60; // Secondary current limit

    // Physical Constraints
    /** Minimum arm rotation in degrees (10 o'clock position - hard stop) */
    public static final double kMinRotation = -60;  // Hard stop at 10 o'clock
    /** Maximum arm rotation in degrees (4-5 o'clock position) */
    public static final double kMaxRotation = 125;  // Max extension
    
    // Starting Position (where arm rests on hard stop)
    public static final double kStartingAngle = -60;  // 10 o'clock hard stop position
    
    // Safe SysId starting position (away from hard stop)
    public static final double kSysIdStartAngle = 60;  // Mid-range, safe position

    // Feedforward Constants
    public static final double kS = 0; // Static friction (volts)
    public static final double kG = 0.088858; // Gravity compensation (volts)
    public static final double kV = 0.00025; // Velocity gain (volts per deg/s)
    public static final double kA = 0.001; // Acceleration gain (volts per deg/s²)

    // PID Constants
    public static final double kP = 0.002058;
    public static final double kI = 0;
    public static final double kD = 0;

    // Motion Constraints
    public static final double kMaxOutput = 1.0;  // Maximum motor output [-1, 1]
    public static final double kMaxVelocity = 90.0; // degrees/second
    public static final double kMaxAcceleration = 45.0; // degrees/second²
    public static final double kMaxError = 2.0; // degrees tolerance
    
    // Constrained Operation (when elevator is extended)
    public static final double kMaxHeightConstrained = 30.0; // Max elevator height for full arm motion
    public static final double kMaxRotationConstrained = 100.0; // Max arm angle when elevator extended
    public static final double kMinRotationConstrained = -30.0; // Min arm angle when elevator extended
    
    // Encoder Configuration (RELATIVE ENCODER - NEO built-in Hall effect)
    public static final boolean kIsInverted = false;
    
    /**
     * IMPORTANT: YAMS handles encoder offset automatically via .withStartingPosition()
     * 
     * The kEncoderOffsetDegrees constant is kept for legacy compatibility but is NOT
     * actively used by YAMS. Instead, YAMS calculates the offset internally when you call:
     * .withStartingPosition(Degrees.of(kStartingAngle))
     * 
     * VERIFICATION PROCEDURE (not calibration - YAMS does this automatically):
     * 1. Power on robot with arm resting on hard stop (-60°)
     * 2. Enable robot
     * 3. Check SmartDashboard "Arm/Calibrated Angle" = -60° ± 2°
     * 4. If not correct, check:
     *    - Motor wiring (may be reversed)
     *    - Motor inversion setting (.withMotorInverted())
     *    - Physical encoder mounting
     * 
     * UNITS NOTE:
     * - Encoder reads ARM OUTPUT POSITION directly (after 12:1 gearing)
     * - NOT motor shaft position
     * - Conversion: 1 encoder rotation = 360° arm rotation
     * - No gearing config in YAMS - encoder is post-reduction
     */
    public static final double kEncoderOffsetDegrees = 0.0; // Not used by YAMS - kept for legacy code
    
    // Legacy - kept for backward compatibility
    public static final double kZeroOffset = kEncoderOffsetDegrees;
    public static final double kConversionFactor = 1.0; // Position conversion factor
    
    // Input Handling
    public static final double kInputSensitivity = 1.0; // Joystick sensitivity
    public static final double kMaxInputAccel = 2.0; // Max input acceleration (deg/s²)

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
