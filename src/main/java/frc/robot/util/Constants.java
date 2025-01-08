package frc.robot.util;

import static edu.wpi.first.units.Units.*;

import edu.wpi.first.units.measure.*;

import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;



public class Constants {

    public static final RobotMode ROBOT_MODE = RobotMode.SIM;

    public static enum RobotMode {
        STANDARD,
        SIM;
    }

    public static class IOConstants {
        public static final int DRIVER_PORT = 0;
        public static final int OPERATOR_PORT = 1;

        public static final double DRIVER_CURVE_CONSTANT = 1;
        public static final double DRIVER_DEADZONE = 0.15;
    }
    
    public static class DriveConstants {
        public static final double TRACK_WIDTH = 24; // Inches

            // Internal Use Only: Change These
        private static final double MAX_DRIVE_SPEED = 4.60248; // Meters per second
        private static final double MAX_DRIVE_ACCEL = 4; // Meters per second squared

        // Update angular constants later
        private static final double MAX_ANGULAR_SPEED = 270; // degrees per second
        private static final double MAX_ANGULAR_ACCEL = 48; // degrees per second squared

            // For External Use: Do not Change
        public static final Distance kTrackWidth = Distance.ofBaseUnits(TRACK_WIDTH, Inches);
        
        public static final LinearVelocity kMaxDriveSpeed = MetersPerSecond.of(MAX_DRIVE_SPEED);
        public static final LinearVelocity kNormalDriveSpeed = kMaxDriveSpeed.times(0.666);
        public static final LinearVelocity kSlowDriveSpeed = kMaxDriveSpeed.times(0.333);

        public static final LinearAcceleration kMaxDriveAcceleration = MetersPerSecondPerSecond.of(MAX_DRIVE_ACCEL);
        public static final LinearAcceleration kNormalDriveAcceleration = kMaxDriveAcceleration.times(0.666);
        public static final LinearAcceleration kSlowDriveAcceleration = kMaxDriveAcceleration.times(0.333);

        public static final AngularVelocity kMaxDriveAngularSpeed = DegreesPerSecond.of(MAX_ANGULAR_SPEED);
        public static final AngularVelocity kNormalDriveAngularSpeed = kMaxDriveAngularSpeed.times(0.666);
        public static final AngularVelocity kSlowDriveAngularSpeed = kMaxDriveAngularSpeed.times(0.333);
        
        public static final AngularAcceleration kMaxDriveAngularAcceleration = DegreesPerSecondPerSecond.of(MAX_ANGULAR_ACCEL);
        public static final AngularAcceleration kNormalDriveAngularAcceleration = kMaxDriveAngularAcceleration.times(0.666);
        public static final AngularAcceleration kSlowDriveAngularAcceleration = kMaxDriveAngularAcceleration.times(0.333);

        public static final SwerveDriveKinematics swerveDriveKinematics = new SwerveDriveKinematics(
            new Translation2d(kTrackWidth, kTrackWidth),
            new Translation2d(kTrackWidth.times(-1), kTrackWidth),
            new Translation2d(kTrackWidth, kTrackWidth.times(-1)),
            new Translation2d(kTrackWidth.times(-1), kTrackWidth.times(-1))
        );


        public static class ModuleConstants {
            /*
             * Order:
             *      0 - Front Left
             *      1 - Front Right
             *      2 - Back Left
             *      3 - Back Right
             */
            
             // Drive ID, Turn ID
            public static final int[][] MOTOR_IDS = {
                {2, 3},
                {4, 5},
                {6, 7},
                {8, 9}
            };

            public static final int[] ENCODER_IDS = {
                1, 2, 3, 4
            };

            public static final Angle[] ENCODER_OFFSETS = {
                Degrees.of(0),
                Degrees.of(0),
                Degrees.of(0),
                Degrees.of(0)
            };

            public static final double kWheelDiameter = 0.1016; // meters
            public static final double kDriveWheelFreeSpeed = 14.419; // rotations per second

            public static final double kDriveMotorRatio = 1;
            public static final double kTurnMotorRatio = 1;
            
            public static final int kDriveSmartCurrentLimit = 45;
            public static final int kDriveSecondaryCurrentLimit = 60;
            public static final int kTurnSmartCurrentLimit = 20;

            public static final double kDriveP = 0.04;
            public static final double kDriveI = 0;
            public static final double kDriveD = 0;

            public static final double kTurnP = 1;
            public static final double kTurnI = 0;
            public static final double kTurnD = 0;
        }
    }
}
