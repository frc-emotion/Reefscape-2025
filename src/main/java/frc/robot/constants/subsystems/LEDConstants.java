// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.constants.subsystems;

/**
 * Constants for LED strip control and animations.
 */
public final class LEDConstants {
    
    /** Starting hue for rainbow animation */
    public static int rainbowFirstPixelHue = 0;

    /** PWM port for LED strip */
    public static final int LED_PORT = 0;

    /** Number of LEDs in the strip */
    public static final int LED_COUNT = 69;

    private LEDConstants() {
        throw new UnsupportedOperationException("This is a utility class!");
    }
}
