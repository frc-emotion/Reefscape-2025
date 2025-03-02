package frc.robot.util;

import com.revrobotics.spark.config.ClosedLoopConfig.FeedbackSensor;
import com.revrobotics.spark.config.MAXMotionConfig.MAXMotionPositionMode;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.config.SparkMaxConfig;

import edu.wpi.first.units.Units;
import frc.robot.Constants.ArmConstants;
import frc.robot.Constants.ClimbConstants;
import frc.robot.Constants.ElevatorConstants;
import frc.robot.Constants.GrabberConstants;
import frc.robot.Constants.Ports;

public class Configs {
    public static class ElevatorConfigs {
        public static final SparkMaxConfig ELEVATOR_CONFIG = new SparkMaxConfig();
        public static final SparkMaxConfig ELEVATOR_FOLLOWER_CONFIG = new SparkMaxConfig();

        static {
            ELEVATOR_CONFIG
                    .idleMode(IdleMode.kBrake)
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
                    .feedbackSensor(FeedbackSensor.kAlternateOrExternalEncoder);

            ELEVATOR_CONFIG.alternateEncoder
                    .countsPerRevolution(ElevatorConstants.kEncoderCPR)
                    .positionConversionFactor(ElevatorConstants.inchesPerCount);

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
                    .follow(Ports.CANID.ELEVATOR_DRIVE_LEADER.getId(), true);
                    // .absoluteEncoderPositionAlwaysOn(false)
                    // .analogPositionAlwaysOn(false)
                    // .absoluteEncoderVelocityAlwaysOn(false)
                    // .analogVelocityAlwaysOn(false)
                    // .externalOrAltEncoderPositionAlwaysOn(false)
                    // .externalOrAltEncoderVelocityAlwaysOn(false);
        }
    }

    public static class GrabberConfigs {
        public static final SparkMaxConfig GRABBER_CONFIG = new SparkMaxConfig();

        static {
            GRABBER_CONFIG
                    .idleMode(IdleMode.kBrake)
                    .smartCurrentLimit(GrabberConstants.kSmartCurrentLimit)
                    .secondaryCurrentLimit(GrabberConstants.kSecondaryCurrentLimit);

        }
    }
    public static class ClimbConfigs {
        public static final SparkMaxConfig CLIMB_CONFIG = new SparkMaxConfig();

        static {
            CLIMB_CONFIG
                .idleMode(IdleMode.kBrake)
                .smartCurrentLimit(ClimbConstants.kSmartCurrentLimit)
                .secondaryCurrentLimit(ClimbConstants.kSecondaryCurrentLimit);

        }
    }
    public static class ArmConfigs {
        public static final SparkMaxConfig ARM_CONFIG = new SparkMaxConfig();

        static {
            ARM_CONFIG
                    .idleMode(IdleMode.kBrake)
                    .smartCurrentLimit(ArmConstants.kSmartCurrentLimit)
                    .secondaryCurrentLimit(ArmConstants.kSecondaryCurrentLimit);

            ARM_CONFIG.alternateEncoder
                    .inverted(ArmConstants.kIsInverted)
                    // .positionConversionFactor(ArmConstants.kConversionFactor)
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
    }

}
