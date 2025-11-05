package frc.robot.config.subsystems;

import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.config.SparkMaxConfig;

import frc.robot.constants.subsystems.ClimbConstants;

/**
 * Configuration class for Climb subsystem SparkMax controller.
 * Defines motor control parameters and current limits.
 */
public final class ClimbConfig {
    /** Climb motor configuration */
    public static final SparkMaxConfig CLIMB_CONFIG = new SparkMaxConfig();

    static {
        CLIMB_CONFIG
            .idleMode(IdleMode.kBrake)
            .smartCurrentLimit(ClimbConstants.kSmartCurrentLimit)
            .secondaryCurrentLimit(ClimbConstants.kSecondaryCurrentLimit);
    }

    private ClimbConfig() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
