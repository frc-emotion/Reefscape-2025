package frc.robot.util;

import com.revrobotics.spark.ClosedLoopSlot;
import com.revrobotics.spark.config.SparkMaxConfig;
import com.revrobotics.spark.config.ClosedLoopConfig.FeedbackSensor;
import com.revrobotics.spark.config.MAXMotionConfig.MAXMotionPositionMode;

import edu.wpi.first.units.Units;
import frc.robot.Constants.ArmConstants;
import frc.robot.Constants.ElevatorConstants;
import frc.robot.Constants.GrabberConstants;
import frc.robot.Constants.Ports;

import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;

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
                    .forwardSoftLimitEnabled(true)
                    .reverseSoftLimitEnabled(true)
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
                    .smartCurrentLimit(ElevatorConstants.kSmartCurrentLimit)
                    .secondaryCurrentLimit(ElevatorConstants.kSecondaryCurrentLimit)
                    .voltageCompensation(12.0)
                    .follow(Ports.CANID.ELEVATOR_DRIVE_1.getId(), true).signals
                    .absoluteEncoderPositionAlwaysOn(false)
                    .analogPositionAlwaysOn(false)
                    .absoluteEncoderVelocityAlwaysOn(false)
                    .analogVelocityAlwaysOn(false)
                    .externalOrAltEncoderPositionAlwaysOn(false)
                    .externalOrAltEncoderVelocityAlwaysOn(false);
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

    public static class ArmConfigs {
        public static final SparkMaxConfig ARM_CONFIG = new SparkMaxConfig();

        static {
            ARM_CONFIG
                    .idleMode(IdleMode.kBrake)
                    .smartCurrentLimit(ArmConstants.kSmartCurrentLimit)
                    .secondaryCurrentLimit(ArmConstants.kSecondaryCurrentLimit);

            ARM_CONFIG.alternateEncoder
                    .inverted(ArmConstants.kIsInverted)
                    .positionConversionFactor(ArmConstants.kConversionFactor)
                    .velocityConversionFactor(ArmConstants.kConversionFactor / 60.0);

            ARM_CONFIG.softLimit
                    .forwardSoftLimitEnabled(true)
                    .reverseSoftLimitEnabled(true)
                    .forwardSoftLimit(ArmConstants.kMinRotation)
                    .reverseSoftLimit(ArmConstants.kMaxRotation);

            ARM_CONFIG.closedLoop
                    .feedbackSensor(FeedbackSensor.kAbsoluteEncoder)
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
