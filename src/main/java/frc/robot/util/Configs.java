// package frc.robot.util;

// import com.revrobotics.spark.config.SparkMaxConfig;
// import com.revrobotics.spark.config.ClosedLoopConfig.FeedbackSensor;
// import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;

// import frc.robot.util.Constants.DriveConstants.ModuleConstants;

// public class Configs {
//     public static class SwerveConfigs {
//         public static final SparkMaxConfig DRIVE_CONFIG = new SparkMaxConfig();
//         public static final SparkMaxConfig ANGLE_CONFIG = new SparkMaxConfig();

//         static {
//             DRIVE_CONFIG
//                 .idleMode(IdleMode.kCoast)
//                 .smartCurrentLimit(ModuleConstants.kDriveSmartCurrentLimit)
//                 .secondaryCurrentLimit(ModuleConstants.kDriveSecondaryCurrentLimit);
//             DRIVE_CONFIG.encoder
//                 .positionConversionFactor(ModuleConstants.driveFactor) // meters
//                 .velocityConversionFactor(ModuleConstants.driveFactor / 60.0); // meters per second
//             DRIVE_CONFIG.closedLoop
//                 .feedbackSensor(FeedbackSensor.kPrimaryEncoder)
//                 .pid(ModuleConstants.kDriveP, ModuleConstants.kDriveI, ModuleConstants.kDriveD)
//                 .velocityFF(ModuleConstants.driveVelocityFeedForward)
//                 .outputRange(-1, 1);
            
//             ANGLE_CONFIG
//                 .idleMode(IdleMode.kCoast)
//                 .smartCurrentLimit(ModuleConstants.kAngleSmartCurrentLimit)
//                 .secondaryCurrentLimit(ModuleConstants.kAngleSmartCurrentLimit)
//                 .voltageCompensation(12);
//             ANGLE_CONFIG.encoder
//                 .positionConversionFactor(360.0 / ModuleConstants.kAngleMotorRatio)//360/42)//ModuleConstants.angleFactor) // degrees
//                 .velocityConversionFactor(ModuleConstants.angleFactor / 60.0);
                
//             ANGLE_CONFIG.closedLoop
//                 .feedbackSensor(FeedbackSensor.kPrimaryEncoder)
//                 .pid(ModuleConstants.kAngleP, ModuleConstants.kAngleI, ModuleConstants.kAngleD)
//                 .outputRange(-1, 1)
//                 .positionWrappingEnabled(true)
//                 .positionWrappingInputRange(0, ModuleConstants.angleFactor);
//         }
//     }
// }
