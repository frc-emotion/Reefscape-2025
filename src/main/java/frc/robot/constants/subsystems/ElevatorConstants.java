// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.constants.subsystems;

import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.units.Units;
import edu.wpi.first.units.measure.Current;
import edu.wpi.first.units.measure.Distance;
import edu.wpi.first.units.measure.LinearAcceleration;
import edu.wpi.first.units.measure.LinearVelocity;
import edu.wpi.first.units.measure.Mass;
import edu.wpi.first.units.measure.Time;
import edu.wpi.first.units.measure.Voltage;

import static edu.wpi.first.units.Units.Inches;

/**
 * Constants for the elevator subsystem including physical dimensions, limits, and preset positions.
 */
public final class ElevatorConstants {
    
    // Motor Configuration
    /** Stator current limit in amps */
    public static final Current STATOR_CURRENT_LIMIT = Units.Amps.of(40);
    
    // Motion Profile Limits
    /** Maximum linear velocity in meters per second */
    public static final LinearVelocity MAX_VELOCITY = Units.MetersPerSecond.of(0.5);
    /** Maximum linear acceleration in meters per second squared */
    public static final LinearAcceleration MAX_ACCELERATION = Units.MetersPerSecondPerSecond.of(0.5);
    
    // Gearing
    /** First gear reduction stage ratio (e.g., 3 means 3:1) */
    public static final double GEAR_RATIO_STAGE_1 = 3.0;
    /** Second gear reduction stage ratio (e.g., 4 means 4:1) */
    public static final double GEAR_RATIO_STAGE_2 = 4.0;
    
    // Mechanism Circumference (pulley/sprocket)
    /** Pulley/sprocket diameter in inches */
    public static final double PULLEY_DIAMETER_INCHES = 0.25;
    /** Mechanism circumference - distance traveled per rotation */
    public static final Distance MECHANISM_CIRCUMFERENCE = Units.Meters.of(
        Units.Inches.of(PULLEY_DIAMETER_INCHES).in(Units.Meters) * Math.PI * 2
    );
    
    // Feedforward Constants
    public static final double kS = 0.0; // Static gain (volts)
    public static final double kG = 0.36; // Gravity gain (volts)
    public static final double kV = 0; // Velocity gain (volts per m/s)
    public static final double kA = 0; // Acceleration gain (volts per m/s²)

    // PID Constants
    public static final double kP = 2.0;
    public static final double kI = 0;
    public static final double kD = 0;

    // Coral Scoring Heights
    public static final Distance CORAL_L1_HEIGHT = Units.Inches.of(0);
    public static final Distance CORAL_L2_HEIGHT = Units.Inches.of(8.6);
    public static final Distance CORAL_L3_HEIGHT = Units.Inches.of(18.1);
    public static final Distance CORAL_L4_HEIGHT = Units.Inches.of(29.5);
    public static final Distance CORAL_INTAKE_HEIGHT = Units.Inches.of(0);

    // Algae Positions
    public static final Distance ALGAE_PREP_NET = Units.Inches.of(61);
    public static final Distance ALGAE_PREP_PROCESSOR_HEIGHT = Units.Inches.of(1);
    public static final Distance ALGAE_L3_CLEANING = Units.Inches.of(18.1);
    public static final Distance ALGAE_L2_CLEANING = Units.Inches.of(8.6);
    public static final Distance ALGAE_GROUND_INTAKE = Units.Inches.of(0);

    // Utility Positions
    public static final Distance PREP_0 = Units.Inches.of(0);
    public static final Distance DEADZONE_DISTANCE = Units.Inches.of(1);
    
    // Homing Constants
    public static final double HOMING_SPEED = -0.15; // Slow speed for homing
    public static final double HOMING_CURRENT_THRESHOLD = 15.0; // amps - detect bottom
    public static final double HOMING_TIMEOUT_SECONDS = 5.0; // Safety timeout

    // Physical Dimensions
    /** Elevator carriage mass */
    public static final Mass CARRIAGE_MASS = Units.Pounds.of(16);
    /** Starting height when robot boots */
    public static final Distance STARTING_HEIGHT = Units.Meters.of(0.5);
    /** Minimum height (hard limit - physical stop) */
    public static final Distance MIN_HEIGHT = Units.Meters.of(0);
    /** Maximum height (hard limit - physical stop) */
    public static final Distance MAX_HEIGHT = Units.Meters.of(3);
    
    // Robot/Mechanism Position Configuration
    /** Maximum robot height for field visualization */
    public static final Distance MAX_ROBOT_HEIGHT = Units.Meters.of(1.5);
    /** Maximum robot length for field visualization */
    public static final Distance MAX_ROBOT_LENGTH = Units.Meters.of(0.75);
    /** Elevator position relative to robot center (X, Y, Z in meters) */
    public static final Translation3d ELEVATOR_POSITION = new Translation3d(
        Units.Meters.of(-0.25), // X: behind center
        Units.Meters.of(0),     // Y: left/right from center
        Units.Meters.of(0.5)    // Z: height above ground
    );
    
    // SysId Configuration
    /** SysId quasistatic voltage - START LOW for heavy mechanisms! */
    public static final Voltage SYSID_STEP_VOLTAGE = Units.Volts.of(4);  // Reduced from 12V for safety
    /** SysId dynamic voltage ramp rate (volts per second) */
    public static final double SYSID_RAMP_RATE_VALUE = 2.0;  // Reduced from 12.0 for safety
    /** SysId timeout duration */
    public static final Time SYSID_TIMEOUT = Units.Seconds.of(10);  // Reduced from 30s
    
    // Diagnostic Thresholds
    public static final int NORMAL_OPERATION_CURRENT = 40; // amps
    public static final int CURRENT_SPIKE_THRESHOLD = 20; // amps
    public static final int NORMAL_OPERATION_TEMP = 40; // celsius
    public static final int TEMP_SPIKE_THRESHOLD = 20; // celsius

    private ElevatorConstants() {
        throw new UnsupportedOperationException("This is a utility class!");
    }
}
