package frc.robot.util;

import com.revrobotics.spark.config.SparkMaxConfig;

// Note: ArmConfig and ElevatorConfig removed - now managed by YAMS subsystems
import frc.robot.config.subsystems.ClimbConfig;
import frc.robot.config.subsystems.GrabberConfig;

/**
 * @deprecated Use classes in frc.robot.config.subsystems package instead.
 * This class is maintained for backward compatibility only.
 */
@Deprecated(since = "2025", forRemoval = true)
public class Configs {
    // Note: ElevatorConfigs and ArmConfigs removed - now managed by YAMS subsystems
    // YAMS uses its own configuration system via SmartMotorControllerConfig

    /**
     * @deprecated Use {@link frc.robot.config.subsystems.GrabberConfig} instead
     */
    @Deprecated(since = "2025", forRemoval = true)
    public static class GrabberConfigs {
        public static final SparkMaxConfig GRABBER_CONFIG = GrabberConfig.GRABBER_CONFIG;
    }

    /**
     * @deprecated Use {@link frc.robot.config.subsystems.ClimbConfig} instead
     */
    @Deprecated(since = "2025", forRemoval = true)
    public static class ClimbConfigs {
        public static final SparkMaxConfig CLIMB_CONFIG = ClimbConfig.CLIMB_CONFIG;
    }
}
