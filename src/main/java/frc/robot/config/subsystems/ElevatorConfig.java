package frc.robot.config.subsystems;

import com.revrobotics.spark.config.ClosedLoopConfig.FeedbackSensor;
import com.revrobotics.spark.config.MAXMotionConfig.MAXMotionPositionMode;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.config.SparkMaxConfig;

import edu.wpi.first.units.Units;
import frc.robot.constants.subsystems.ElevatorConstants;
import frc.robot.constants.PortMap;

/**
 * Configuration class for Elevator subsystem SparkMax controllers.
 * Defines motor control parameters, current limits, PID values, and soft limits.
 */
public final class ElevatorConfig {
    /** Leader motor configuration */
    public static final SparkMaxConfig ELEVATOR_CONFIG = new SparkMaxConfig();
    
    /** Follower motor configuration */
    public static final SparkMaxConfig ELEVATOR_FOLLOWER_CONFIG = new SparkMaxConfig();

    static {
        ELEVATOR_CONFIG
                .idleMode(IdleMode.kCoast)
                .smartCurrentLimit(ElevatorConstants.kSmartCurrentLimit)
                .secondaryCurrentLimit(ElevatorConstants.kSecondaryCurrentLimit)
                .voltageCompensation(12.0);

        ELEVATOR_CONFIG.softLimit
                .forwardSoftLimitEnabled(false)
                .reverseSoftLimitEnabled(false)
                .forwardSoftLimit(Units.Inches.of(60).in(Units.Inches))
                .reverseSoftLimit(Units.Inches.of(0).in(Units.Inches));

        ELEVATOR_CONFIG.closedLoop
                .pid(ElevatorConstants.kP, ElevatorConstants.kI, ElevatorConstants.kD)
                .feedbackSensor(FeedbackSensor.kPrimaryEncoder);

        ELEVATOR_CONFIG.alternateEncoder
                .countsPerRevolution(ElevatorConstants.kEncoderCPR);

        ELEVATOR_CONFIG.closedLoop.maxMotion
                .maxVelocity(ElevatorConstants.MAX_MOTOR_RPM)
                .maxAcceleration(ElevatorConstants.MAX_MOTOR_ACCELERATION)
                .positionMode(MAXMotionPositionMode.kMAXMotionTrapezoidal)
                .allowedClosedLoopError(0.01);

        ELEVATOR_CONFIG.closedLoop.pid(ElevatorConstants.kP, ElevatorConstants.kI, ElevatorConstants.kD);

        ELEVATOR_FOLLOWER_CONFIG
                .idleMode(IdleMode.kBrake)
                .smartCurrentLimit(ElevatorConstants.kSmartCurrentLimit)
                .secondaryCurrentLimit(ElevatorConstants.kSecondaryCurrentLimit)
                .voltageCompensation(12.0)
                .disableFollowerMode()
                .follow(PortMap.CANID.ELEVATOR_DRIVE_LEADER.getId(), true);
    }

    private ElevatorConfig() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
