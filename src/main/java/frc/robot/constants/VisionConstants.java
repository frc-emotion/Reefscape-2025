// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.constants;

import static edu.wpi.first.units.Units.Milliseconds;
import static edu.wpi.first.units.Units.Seconds;

/**
 * Constants for vision processing and AprilTag detection.
 */
public final class VisionConstants {
    
    /** Maximum accuracy limit for vision measurements */
    public static final double ACCURACY_LIMIT = 4;
    
    /** Debounce time for vision measurements (seconds) */
    public static final double DEBOUNCE_TIME_SEC = Milliseconds.of(15).in(Seconds);

    private VisionConstants() {
        throw new UnsupportedOperationException("This is a utility class!");
    }
}
