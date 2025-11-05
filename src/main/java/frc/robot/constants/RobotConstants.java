// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.units.Units;
import frc.robot.util.diagnostics.Faults.FaultTypes.PDFaults;
import swervelib.math.Matter;

/**
 * Robot-wide constants including mass, loop timing, and known faults.
 */
public final class RobotConstants {
    
    /** Known power distribution faults to ignore */
    public static final List<String> KNOWN_PD_FAULTS = new ArrayList<>(Arrays.asList(
            PDFaults.CHANNEL_20_BREAKER.name(),
            PDFaults.CHANNEL_21_BREAKER.name(),
            PDFaults.CHANNEL_22_BREAKER.name(),
            PDFaults.CHANNEL_20_BREAKER.name()));
    
    /** Robot mass in kg (148 - 20.3 lbs converted to kg) */
    public static final double ROBOT_MASS = (148 - 20.3) * 0.453592;
    
    /** Chassis matter object for physics simulation */
    public static final Matter CHASSIS = new Matter(
            new Translation3d(0, 0, Units.Meters.convertFrom(8, Units.Inches)),
            ROBOT_MASS);
    
    /** Main loop time in seconds (20ms + 110ms spark max velocity lag) */
    public static final double LOOP_TIME = 0.13;
    
    /** Maximum robot speed in meters per second */
    public static final double MAX_SPEED = Units.Feet.convertFrom(20.0, Units.Meters);

    private RobotConstants() {
        throw new UnsupportedOperationException("This is a utility class!");
    }
}
