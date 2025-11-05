package frc.robot.config.subsystems;

import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.config.SparkMaxConfig;

import frc.robot.constants.subsystems.GrabberConstants;

/**
 * Configuration class for Grabber subsystem SparkMax controller.
 * Defines motor control parameters and current limits.
 */
public final class GrabberConfig {
    /** Grabber motor configuration */
    public static final SparkMaxConfig GRABBER_CONFIG = new SparkMaxConfig();

    static {
        GRABBER_CONFIG
                .idleMode(IdleMode.kBrake)
                .smartCurrentLimit(GrabberConstants.kSmartCurrentLimit)
                .secondaryCurrentLimit(GrabberConstants.kSecondaryCurrentLimit);
    }

    private GrabberConfig() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
