package frc.robot.util;

import com.revrobotics.spark.config.SparkMaxConfig;
import com.revrobotics.spark.config.ClosedLoopConfig.FeedbackSensor;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;

import frc.robot.util.Constants.DriveConstants.ModuleConstants;

public class Configs {
    public static class SwerveConfigs {
        public static final SparkMaxConfig DRIVE_CONFIG = new SparkMaxConfig();
        public static final SparkMaxConfig TURN_CONFIG = new SparkMaxConfig();

        static {
            double driveFactor = ModuleConstants.kWheelDiameter * 180 / ModuleConstants.kDriveMotorRatio;
            double turnFactor = 180 / ModuleConstants.kTurnMotorRatio;
            double driveVelocityFeedForward = 1 / ModuleConstants.kDriveWheelFreeSpeed;

            DRIVE_CONFIG
                .idleMode(IdleMode.kBrake)
                .smartCurrentLimit(ModuleConstants.kDriveSmartCurrentLimit)
                .secondaryCurrentLimit(ModuleConstants.kDriveSecondaryCurrentLimit);
            DRIVE_CONFIG.encoder
                .positionConversionFactor(driveFactor) // meters
                .velocityConversionFactor(driveFactor / 60.0); // meters per second
            DRIVE_CONFIG.closedLoop
                .feedbackSensor(FeedbackSensor.kPrimaryEncoder)
                .pid(ModuleConstants.kDriveP, ModuleConstants.kDriveI, ModuleConstants.kDriveD)
                .velocityFF(driveVelocityFeedForward)
                .outputRange(-1, 1);
            
            TURN_CONFIG
                .idleMode(IdleMode.kBrake)
                .smartCurrentLimit(ModuleConstants.kTurnSmartCurrentLimit)
                .secondaryCurrentLimit(ModuleConstants.kTurnSmartCurrentLimit);
            TURN_CONFIG.encoder
                .positionConversionFactor(turnFactor) // degrees
                .velocityConversionFactor(turnFactor / 60.0); // degrees per second
            TURN_CONFIG.closedLoop
                .feedbackSensor(FeedbackSensor.kPrimaryEncoder)
                .pid(ModuleConstants.kTurnP, ModuleConstants.kTurnI, ModuleConstants.kTurnD)
                .outputRange(-1, 1)
                .positionWrappingEnabled(true)
                .positionWrappingInputRange(0, turnFactor);
        }
    }
}
