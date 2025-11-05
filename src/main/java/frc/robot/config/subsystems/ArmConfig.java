package frc.robot.config.subsystems;

import com.revrobotics.spark.config.ClosedLoopConfig.FeedbackSensor;
import com.revrobotics.spark.config.MAXMotionConfig.MAXMotionPositionMode;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.config.SparkMaxConfig;

import frc.robot.constants.subsystems.ArmConstants;

/**
 * Configuration class for Arm subsystem SparkMax controller.
 * Defines motor control parameters, current limits, PID values, and encoder settings.
 */
public final class ArmConfig {
    /** Arm motor configuration */
    public static final SparkMaxConfig ARM_CONFIG = new SparkMaxConfig();

    static {
        ARM_CONFIG
                .idleMode(IdleMode.kBrake)
                .smartCurrentLimit(ArmConstants.kSmartCurrentLimit)
                .secondaryCurrentLimit(ArmConstants.kSecondaryCurrentLimit);

        ARM_CONFIG.alternateEncoder
                .inverted(ArmConstants.kIsInverted)
                .velocityConversionFactor(ArmConstants.kConversionFactor / 60.0);

        ARM_CONFIG.softLimit
                .forwardSoftLimitEnabled(false)
                .reverseSoftLimitEnabled(false)
                .forwardSoftLimit(ArmConstants.kMinRotation)
                .reverseSoftLimit(ArmConstants.kMaxRotation);

        ARM_CONFIG.closedLoop
                .feedbackSensor(FeedbackSensor.kAlternateOrExternalEncoder)
                .pid(
                        ArmConstants.kP,
                        ArmConstants.kI,
                        ArmConstants.kD)
                .outputRange(-ArmConstants.kMaxOutput, ArmConstants.kMaxOutput);

        ARM_CONFIG.closedLoop.maxMotion
                .positionMode(MAXMotionPositionMode.kMAXMotionTrapezoidal)
                .maxVelocity(ArmConstants.kMaxVelocity)
                .maxAcceleration(ArmConstants.kMaxAcceleration)
                .allowedClosedLoopError(ArmConstants.kMaxError);
    }

    private ArmConfig() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
