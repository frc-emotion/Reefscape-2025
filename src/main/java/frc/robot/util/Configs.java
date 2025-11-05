package frc.robot.util;

import com.revrobotics.spark.config.SparkMaxConfig;

import frc.robot.config.subsystems.ArmConfig;
import frc.robot.config.subsystems.ClimbConfig;
import frc.robot.config.subsystems.ElevatorConfig;
import frc.robot.config.subsystems.GrabberConfig;

/**
 * @deprecated Use classes in frc.robot.config.subsystems package instead.
 * This class is maintained for backward compatibility only.
 */
@Deprecated(since = "2025", forRemoval = true)
public class Configs {
    /**
     * @deprecated Use {@link frc.robot.config.subsystems.ElevatorConfig} instead
     */
    @Deprecated(since = "2025", forRemoval = true)
    public static class ElevatorConfigs {
        public static final SparkMaxConfig ELEVATOR_CONFIG = ElevatorConfig.ELEVATOR_CONFIG;
        public static final SparkMaxConfig ELEVATOR_FOLLOWER_CONFIG = ElevatorConfig.ELEVATOR_FOLLOWER_CONFIG;
    }

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

    /**
     * @deprecated Use {@link frc.robot.config.subsystems.ArmConfig} instead
     */
    @Deprecated(since = "2025", forRemoval = true)
    public static class ArmConfigs {
        public static final SparkMaxConfig ARM_CONFIG = ArmConfig.ARM_CONFIG;
    }
}
