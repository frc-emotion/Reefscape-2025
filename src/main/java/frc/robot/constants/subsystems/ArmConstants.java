// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.constants.subsystems;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.units.Units;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.AngularAcceleration;
import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.units.measure.Distance;
import edu.wpi.first.units.measure.Mass;
import edu.wpi.first.units.measure.Time;
import edu.wpi.first.units.measure.Voltage;

/**
 * Constants for the arm subsystem including physical limits, PID values, and preset positions.
 * 
 * ARM GEOMETRY:
 * - Zero (0°) = Arm resting on hopper (ONLY when elevator at bottom for intake)
 * - Safe travel (25°) = Arm vertical - DEFAULT position when elevator is up
 * - Positive angles (25-125°) = Arm back/up for scoring - safe at all elevator heights
 * 
 * SAFETY STRATEGY:
 * - When elevator is UP: Arm stays at vertical (~25°) by default
 * - When elevator is DOWN: Arm can go to 0° for ground intake
 * - Emergency: ReturnArmToSafe command snaps back to vertical
 */
public final class ArmConstants {
    
    // Motor Configuration
    public static final int kSmartCurrentLimit = 45;
    public static final double kMaxOutput = 1.0;
    
    // Motion Profile Limits
    /** Maximum angular velocity in degrees per second */
    public static final AngularVelocity MAX_VELOCITY = Units.DegreesPerSecond.of(180);
    /** Maximum angular acceleration in degrees per second squared */
    public static final AngularAcceleration MAX_ACCELERATION = Units.DegreesPerSecondPerSecond.of(90);
    
    // Ramp Rates
    /** Closed loop ramp rate in seconds */
    public static final Time CLOSED_LOOP_RAMP_RATE = Units.Seconds.of(0.25);
    /** Open loop ramp rate in seconds */
    public static final Time OPEN_LOOP_RAMP_RATE = Units.Seconds.of(0.25);
    
    // Gearing
    /** First gear reduction stage ratio (e.g., 3 means 3:1) */
    public static final double GEAR_RATIO_STAGE_1 = 3.0;
    /** Second gear reduction stage ratio (e.g., 4 means 4:1) */
    public static final double GEAR_RATIO_STAGE_2 = 4.0;

    // Physical Constraints (relative to hopper rest = 0°)
    /** Minimum arm rotation in degrees (full forward to bumper) */
    public static final double kMinRotation = -45;
    /** Maximum arm rotation in degrees (full back/up for high scoring) */
    public static final double kMaxRotation = 125;
    
    // Safe Travel Position (DEFAULT when elevator is up)
    /** Safe angle - arm is vertical, won't tip robot. This is the DEFAULT position. */
    public static final Rotation2d SAFE_TRAVEL_ANGLE = Rotation2d.fromDegrees(25); // ~vertical
    
    // Intake Position (ONLY when elevator at bottom)
    /** Hopper rest position - ONLY use when elevator is at bottom! */
    public static final Rotation2d HOPPER_REST_ANGLE = Rotation2d.fromDegrees(0);

    // Feedforward Constants
    public static final double kS = 0; // Static friction (volts)
    public static final double kG = 0.088858; // Gravity compensation (volts)
    public static final double kV = 0.00025; // Velocity gain (volts per deg/s)
    public static final double kA = 0.001; // Acceleration gain (volts per deg/s²)

    // PID Constants
    public static final double kP = 0.002058;
    public static final double kI = 0;
    public static final double kD = 0;
    
    // Physical Dimensions (for simulation and mechanism config)
    /** Arm length from pivot to end effector */
    public static final Distance ARM_LENGTH = Units.Meters.of(0.135);
    /** Arm mass including end effector */
    public static final Mass ARM_MASS = Units.Pounds.of(1);
    /** Starting position of arm when robot boots (degrees) */
    public static final Angle ARM_STARTING_POSITION = Units.Degrees.of(0);
    
    // Robot/Mechanism Position Configuration
    /** Maximum robot height for field visualization */
    public static final Distance MAX_ROBOT_HEIGHT = Units.Meters.of(1.5);
    /** Maximum robot length for field visualization */
    public static final Distance MAX_ROBOT_LENGTH = Units.Meters.of(0.75);
    /** Arm pivot position relative to robot center (X, Y, Z in meters) */
    public static final Translation3d ARM_PIVOT_POSITION = new Translation3d(
        Units.Meters.of(0.25),  // X: forward from center
        Units.Meters.of(0),     // Y: left/right from center
        Units.Meters.of(0.5)    // Z: height above ground
    );
    
    // SysId Configuration
    /** SysId quasistatic voltage - START LOW for heavy mechanisms! */
    public static final Voltage SYSID_STEP_VOLTAGE = Units.Volts.of(2);  // Reduced from 3V for safety
    /** SysId dynamic voltage ramp rate (volts per second) */
    public static final double SYSID_RAMP_RATE_VALUE = 1.5;  // Reduced from 3.0 for safety
    /** SysId timeout duration */
    public static final Time SYSID_TIMEOUT = Units.Seconds.of(10);  // Reduced from 30s


    // Coral Scoring Presets (TODO: Tune these on the actual field!)
    // Note: All positive angles (back/up) are safe at any elevator height
    public static final Rotation2d CORAL_L1_ANGLE = Rotation2d.fromDegrees(95);  // Low trough
    public static final Rotation2d CORAL_L2_ANGLE = Rotation2d.fromDegrees(95);  // Branch L2
    public static final Rotation2d CORAL_L3_ANGLE = Rotation2d.fromDegrees(95);  // Branch L3  
    public static final Rotation2d CORAL_L4_ANGLE = Rotation2d.fromDegrees(100); // Branch L4 (highest)
    public static final Rotation2d CORAL_INTAKE_ANGLE = Rotation2d.fromDegrees(125); // Intake from human player

    // Algae Presets (TODO: Tune these on the actual field!)
    public static final Rotation2d ALGAE_L2_ANGLE = Rotation2d.fromDegrees(-30);  // Harvesting from L2 branch
    public static final Rotation2d ALGAE_L3_ANGLE = Rotation2d.fromDegrees(-30);  // Harvesting from L3 branch
    public static final Rotation2d ALGAE_GROUND_ANGLE = HOPPER_REST_ANGLE;         // Ground pickup (elevator at bottom)
    public static final Rotation2d ALGAE_ON_CORAL_ANGLE = Rotation2d.fromDegrees(0);
    public static final Rotation2d ALGAE_NET_ANGLE = Rotation2d.fromDegrees(0);
    public static final Rotation2d ALGAE_PRO_ANGLE = Rotation2d.fromDegrees(0);

    // Diagnostic Thresholds
    public static final int NORMAL_OPERATION_CURRENT = 30; // amps
    public static final int NORMAL_OPERATION_TEMP = 40; // celsius
    public static final int CURRENT_SPIKE_THRESHOLD = 20; // amps
    public static final int TEMP_SPIKE_THRESHOLD = 20; // celsius

    private ArmConstants() {
        throw new UnsupportedOperationException("This is a utility class!");
    }
}
