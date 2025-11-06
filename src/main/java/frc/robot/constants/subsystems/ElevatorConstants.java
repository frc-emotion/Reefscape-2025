// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.constants.subsystems;

import edu.wpi.first.units.Units;
import edu.wpi.first.units.measure.Distance;

import static edu.wpi.first.units.Units.Inches;

/**
 * Constants for the elevator subsystem including physical dimensions, limits, and preset positions.
 */
public final class ElevatorConstants {
    
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

    // Diagnostic Thresholds
    public static final int NORMAL_OPERATION_CURRENT = 40; // amps
    public static final int CURRENT_SPIKE_THRESHOLD = 20; // amps
    public static final int NORMAL_OPERATION_TEMP = 40; // celsius
    public static final int TEMP_SPIKE_THRESHOLD = 20; // celsius

    private ElevatorConstants() {
        throw new UnsupportedOperationException("This is a utility class!");
    }
}
