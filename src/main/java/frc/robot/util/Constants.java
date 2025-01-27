package frc.robot.util;

import static edu.wpi.first.units.Units.*;

import java.util.HashMap;
import java.util.Map;

import com.revrobotics.spark.SparkBase.Faults;

import edu.wpi.first.units.measure.*;
import frc.robot.util.Constants.DriveConstants.ModuleConstants;
import edu.wpi.first.hal.PowerDistributionFaults;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;

public class Constants {

    public static final RobotMode ROBOT_MODE = RobotMode.STANDARD;

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

        public static final AngularAcceleration kMaxDriveAngularAcceleration = DegreesPerSecondPerSecond
                .of(MAX_ANGULAR_ACCEL);
        public static final AngularAcceleration kNormalDriveAngularAcceleration = kMaxDriveAngularAcceleration
                .times(0.666);
        public static final AngularAcceleration kSlowDriveAngularAcceleration = kMaxDriveAngularAcceleration
                .times(0.333);

        public static final double tempVelMax = 4.8; // M/s
        public static final double tempAngVelMax = 360; // Rotations

        public static final SwerveDriveKinematics swerveDriveKinematics = new SwerveDriveKinematics(
                new Translation2d(kTrackWidth, kTrackWidth),
                new Translation2d(kTrackWidth.times(-1), kTrackWidth),
                new Translation2d(kTrackWidth, kTrackWidth.times(-1)),
                new Translation2d(kTrackWidth.times(-1), kTrackWidth.times(-1)));

        public static class ModuleConstants {

            // public static final Angle[] ENCODER_OFFSETS = {
            //         Degrees.of(4.21 + 10 + 0.8),
            //         Degrees.of(178 - 35.7),
            //         Degrees.of(95.53 - 127.7),
            //         Degrees.of(155.6 - 90)
            // };

            public static final Angle[] ENCODER_OFFSETS = {
                    Degrees.of(125.63),
                    Degrees.of(-156.09),
                    Degrees.of(-107.35),
                    Degrees.of(-56.55)
            };


            public static final double kWheelDiameter = 0.1016; // meters
            public static final double kDriveWheelFreeSpeed = 14.419; // rotations per second

            public static final double kDriveMotorRatio = 0.14814815;
            public static final double kAngleMotorRatio = 21.42857143;

            public static final int kDriveSmartCurrentLimit = 50;
            public static final int kDriveSecondaryCurrentLimit = 60;
            public static final int kAngleSmartCurrentLimit = 20;

            public static final double kDriveP = 0.04;
            public static final double kDriveI = 0;
            public static final double kDriveD = 0;

            public static final double kAngleP = 1.0;
            public static final double kAngleI = 0.1;
            public static final double kAngleD = 0;

            public static final double driveFactor = ModuleConstants.kWheelDiameter * 180 / ModuleConstants.kDriveMotorRatio;
            // public static final double angleFactor = 180 / ModuleConstants.kAngleMotorRatio;
            public static final double angleFactor = 360;
            // public static final double driveVelocityFeedForward = 1 / ModuleConstants.kDriveWheelFreeSpeed;
            public static final double driveVelocityFeedForward = 1 / 479;

        }
    }

    public static class ElevatorConstants {
        // Physical Constants
        public static final int kMotorCount = 2;
        public static final double kGearRatio = 0;
        public static final double kCarriageMassKg = 0;
        public static final double kDrumRadiusMeters = 0;
        public static final double kMinHeightMeters = 0;
        public static final double kMaxHeightMeters = 0;
        public static final double kStartingHeightMeters = 0;

        // Ports

        // Feedforward Constants
        public static final double kS = 0; // static gain in volts
        public static final double kG = 0; // gravity gain in volts
        public static final double kV = 0; // velocity gain in mps

        // PID Constants
        public static final double kP = 0;
        public static final double kI = 0;
        public static final double kD = 0;
    }
    
    public static class VisionConstants {
        public static final Transform3d kFrontRightTransform = new Transform3d();
        public static final Transform3d kFrontLeftTransform = new Transform3d();
        public static final Transform3d kBackTransform = new Transform3d();
    }

    public static class Ports {

        public enum CANID {
            PDH(1, "Power Distribution Hub"),

            FRONT_LEFT_CANCODER(11, "Front Left Cancoder"),
            FRONT_RIGHT_CANCODER(12, "Front Right Cancoder"),
            BACK_LEFT_CANCODER(13, "Back Left Cancoder"),
            BACK_RIGHT_CANCODER(14, "Back Right Cancoder"),

            FRONT_LEFT_DRIVE(3, "Front Left Drive"),
            FRONT_LEFT_TURN(4, "Front Left Turn"),

            FRONT_RIGHT_DRIVE(5, "Front Right Drive"),
            FRONT_RIGHT_TURN(6, "Front Right Turn"),

            BACK_LEFT_DRIVE(7, "Back Left Drive"),
            BACK_LEFT_TURN(8, "Back Left Turn"),

            BACK_RIGHT_DRIVE(9, "Back Right Drive"),
            BACK_RIGHT_TURN(10, "Back Right Turn");

            private final int id;
            private final String name;

            private static final Map<Integer, String> CAN_ID_MAP = new HashMap<>();

            /*
             * Order:
             * 0 - Front Left
             * 1 - Front Right
             * 2 - Back Left
             * 3 - Back Right
             */

            // Drive ID, Turn ID
            public static final int[][] SWERVE_IDS = {
                    { FRONT_LEFT_DRIVE.id, FRONT_LEFT_TURN.id },
                    { FRONT_RIGHT_DRIVE.id, FRONT_RIGHT_TURN.id },
                    { BACK_LEFT_DRIVE.id, BACK_LEFT_TURN.id },
                    { BACK_RIGHT_DRIVE.id, BACK_RIGHT_TURN.id }
            };

            public static final int[] CANCODER_IDS = {
                    FRONT_LEFT_CANCODER.id,
                    FRONT_RIGHT_CANCODER.id,
                    BACK_LEFT_CANCODER.id,
                    BACK_RIGHT_CANCODER.id
            };

            static {
                for (CANID canId : CANID.values()) {
                    CAN_ID_MAP.put(canId.getId(), canId.getName());
                }
            }

            CANID(int id, String name) {
                this.id = id;
                this.name = name;
            }

            public int getId() {
                return id;
            }

            public String getName() {
                return name;
            }

            public static String getNameById(int id) {
                return CAN_ID_MAP.getOrDefault(id, "Unknown CAN ID");
            }
        }
    }



}
