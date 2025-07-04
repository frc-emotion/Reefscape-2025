// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.units.Units;
import edu.wpi.first.units.measure.Distance;
import frc.robot.util.Faults.FaultTypes.PDFaults;
import swervelib.math.Matter;
import edu.wpi.first.units.measure.Mass;
import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.DriverStation;

import static edu.wpi.first.units.Units.Inches;
import static edu.wpi.first.units.Units.Milliseconds;
import static edu.wpi.first.units.Units.Pounds;
import static edu.wpi.first.units.Units.Seconds;

/**
 * The Constants class provides a convenient place for teams to hold robot-wide
 * numerical or boolean constants. This
 * class should not be used for any other purpose. All constants should be
 * declared globally (i.e. public static). Do
 * not put anything functional in this class.
 *
 * <p>
 * It is advised to statically import this class (or one of its inner classes)
 * wherever the
 * constants are needed, to reduce verbosity.
 */
public final class Constants {

    public static final List<String> KNOWN_PD_FAULTS = new ArrayList<>(Arrays.asList(
            PDFaults.CHANNEL_20_BREAKER.name(),
            PDFaults.CHANNEL_21_BREAKER.name(),
            PDFaults.CHANNEL_22_BREAKER.name(),
            PDFaults.CHANNEL_20_BREAKER.name()));

    public static final double ROBOT_MASS = (148 - 20.3) * 0.453592; // 32lbs * kg per pound
    public static final Matter CHASSIS = new Matter(new Translation3d(0, 0, Units.Meters.convertFrom(8, Units.Inches)),
            ROBOT_MASS);
    public static final double LOOP_TIME = 0.13; // s, 20ms + 110ms sprk max velocity lag
    public static final double MAX_SPEED = Units.Feet.convertFrom(20.0, Units.Meters);

    // Maximum speed of the robot in meters per second, used to limit acceleration.

    // public static final class AutonConstants
    // {
    //
    // public static final PIDConstants TRANSLATION_PID = new PIDConstants(0.7, 0,
    // 0);
    // public static final PIDConstants ANGLE_PID = new PIDConstants(0.4, 0, 0.01);
    // }

    public static final class DrivebaseConstants {

        // Hold time on motor brakes when disabled
        public static final double WHEEL_LOCK_TIME = 10; // seconds
    }

    public static class OperatorConstants {

        // Joystick Deadband
        public static final double DEADBAND = 0.1;
        public static final double LEFT_Y_DEADBAND = 0.1;
        public static final double RIGHT_X_DEADBAND = 0.1;
        public static final double TURN_CONSTANT = 6;
    }

    public static class ClimbConstants {

        public static final double kSpeed = 0.8;

        public static final double SET_SPEED = 0.8;

        public static final double POSITION_ERROR = 1;

        public static final double EXTENSION_LIMIT = 1;

        public static final double EXTENDED_POSITION = 0;

        public static final double CLIMBED_POSITION = 0;

        public static final int kSmartCurrentLimit = 45;

        public static final double kSecondaryCurrentLimit = 45;


    }
    public static class ElevatorConstants {
        public static final double TOLERABLE_ERROR = 1.5;

        // Physical Constants
        public static final int kSmartCurrentLimit = 45;
        public static final int kSecondaryCurrentLimit = 45;

        public static final int kEncoderCPR = 8192;

        public static final double kpulleyDiameterInches = 2.0;

        public static final double kPulleyCircumInches = Math.PI * kpulleyDiameterInches;

        public static final double kGearRatio = 12;

        public static final double effectiveCountsPerRevolution = kEncoderCPR * kGearRatio;

        public static final double inchesPerCount = kPulleyCircumInches / kGearRatio;

        public static final Mass kCarriageMass = Pounds.of(28.34);
        public static final Distance kDrumRadius = Inches.of(0.878);
        public static final Distance kMinHeight = Inches.of(0);
        public static final Distance kMaxHeight = Inches.of(29);
        public static final Distance kStartingHeight = Inches.of(0);

        public static final double MAX_MOTOR_RPM = 10000.0; // Need to change name of variable not RPM
        public static final double MAX_MOTOR_ACCELERATION = 80; // TODO: Need to change name of variable not RPM

        // Feedforward Constants
        public static final double kS = 0.0; // static gain in volts
        public static final double kG = 0.36; // gravity gain in volts
        public static final double kV = 0; //10.52; // velocity gain in mps
        public static final double kA = 0; //0.03; // acceleration gain in mps^2

        // PID Constants
        public static final double kP = 2.0;
        public static final double kI = 0;
        public static final double kD = 0;

        public static final Distance CORAL_L1_HEIGHT = Units.Inches.of(0);
        public static final Distance CORAL_L2_HEIGHT = Units.Inches.of(8.6);
        public static final Distance CORAL_L3_HEIGHT = Units.Inches.of(18.1);
        public static final Distance CORAL_L4_HEIGHT = Units.Inches.of(29.5);


        public static final Distance ALGAE_PREP_NET = Units.Inches.of(61);
        public static final Distance ALGAE_PREP_PROCESSOR_HEIGHT = Units.Inches.of(1);
        public static final Distance ALGAE_L3_CLEANING = Units.Inches.of(18.1);
        public static final Distance ALGAE_L2_CLEANING = Units.Inches.of(8.6);
        public static final Distance ALGAE_GROUND_INTAKE = Units.Inches.of(0);
        public static final Distance PREP_0 = Units.Inches.of(0);
        public static final Distance DEADZONE_DISTANCE = Units.Inches.of(1);
        public static final Distance CORAL_INTAKE_HEIGHT = Units.Inches.of(0);

        public static final int NORMAL_OPERATION_CURRENT = 40;
        public static final int CURRENT_SPIKE_THRESHOLD = 20;

        public static final int NORMAL_OPERATION_TEMP = 40;
        public static final int TEMP_SPIKE_THRESHOLD = 20;

    }

    public static class GrabberConstants {
        public static final int kSmartCurrentLimit = 35;
        public static final double kSecondaryCurrentLimit = 35;

        public static final double GRABBER_CORAL_OUTTAKE = -0.65; // TBD

        public static final double GRABBER_CORAL_INTAKE = GRABBER_CORAL_OUTTAKE / 2.5;

        public static final double GRABBER_ALGAE_SPEED = -0.65; // TBD
        public static final double GRABBER_ALGAE_HOLD_SPEED = 0.1; // TBD
        public static final double GRABBER_CORAL_HOLD_SPEED = 0.3; // TBD
        public static final double ZERO_SPEED = 0;
        public static final double CORAL_DETECT_RANGE = 0;
        
        public static final int NORMAL_OPERATION_CURRENT = 40;
        public static final int CURRENT_SPIKE_THRESHOLD = 20;
        public static final int NORMAL_OPERATION_TEMP = 40;
        public static final int TEMP_SPIKE_THRESHOLD = 20;
    }                                                                                                               
    public static class ArmConstants {
        public static final int kSmartCurrentLimit = 45;
        public static final double kSecondaryCurrentLimit = 45;

        public static final double kMaxOutput = 1;

            // Physical Constraints
        public static final double kMinRotation = -45; // Note: Aligns with hopper
        public static final double kMaxRotation = 125;
        public static final double kMaxHeightConstrained = 0; // The height in meters at which the arm is able to rotate fully. Should be roughly the length of the arm.
        public static final double kMaxRotationConstrained = 95; // The max rotation while the arm is below the minimum full rotation height. Could be formulaic, but probably not necessary.
        public static final double kMinRotationConstrained = 0;

            // Feedforward Constants
        public static final double kS = 0;
        public static final double kG = 0.088858; // recalc: 0.55
        public static final double kV = 0.00025; // recalc: 0.39
        public static final double kA = 0.001; // recalc: 0.02

            // PID Constants
        public static final double kP = 0.002058; //0.003058;
        public static final double kI = 0;
        public static final double kD = 0;

            // Smart Motion Constants
        public static final double kMaxVelocity = 300;
        public static final double kMaxAcceleration = 3000;
        public static final double kMaxError = 2;
        
            // Encoder Constants
        public static final boolean kIsInverted = false;
        public static final double kZeroOffset = 99.473 - 129.315 + 155.841 + 26.106 - 21.8443;
        // public static final double kConversionFactor = 360 * (22.0/84.0);
        // public static final double kConversionFactor = 180.0 / 7.15;
        public static final double kConversionFactor = ((180.0 / 7.15) / 1.21915) * (90.0 / 93.415);

            // Input Constants
        public static final double kInputSensitivity = 10; // degrees per second
        
        // Presets
        public static final Rotation2d CORAL_L1_ANGLE = Rotation2d.fromDegrees(95);
        public static final Rotation2d CORAL_L2_ANGLE = Rotation2d.fromDegrees(95);
        public static final Rotation2d CORAL_L3_ANGLE = Rotation2d.fromDegrees(95);
        public static final Rotation2d CORAL_L4_ANGLE = Rotation2d.fromDegrees(100); // it was 90 before type shi

        // public static final Rotation2d CORAL_L1_ANGLE_FLIPPED = new Rotation2d(0);
        // public static final Rotation2d CORAL_L2_ANGLE_FLIPPED = new Rotation2d(0);
        // public static final Rotation2d CORAL_L3_ANGLE_FLIPPED = new Rotation2d(0);
        // public s\tatic final Rotation2d CORAL_L4_ANGLE_FLIPPED = new Rotation2d(0);

        public static final Rotation2d CORAL_INTAKE_ANGLE = Rotation2d.fromDegrees(125);
        
        public static final Rotation2d ALGAE_L2_ANGLE = Rotation2d.fromDegrees(-30); // was -45
        public static final Rotation2d ALGAE_L3_ANGLE = Rotation2d.fromDegrees(-30); // was -45
        public static final Rotation2d ALGAE_GROUND_ANGLE = new Rotation2d(0);
        public static final Rotation2d ALGAE_ON_CORAL_ANGLE = new Rotation2d(0);
        public static final Rotation2d ALGAE_NET_ANGLE = new Rotation2d(0);
        public static final Rotation2d ALGAE_PRO_ANGLE = new Rotation2d(0);

        public static final Rotation2d CLIMB_ANGLE = new Rotation2d(0);

        public static final double kMaxInputAccel = 10;


        public static final int NORMAL_OPERATION_CURRENT = 30;
        public static final int NORMAL_OPERATION_TEMP = 40;
        public static final int CURRENT_SPIKE_THRESHOLD = 20;
        public static final int TEMP_SPIKE_THRESHOLD = 20;

    }

    public static class Ports {

        public enum DIO {
            ALGAE_BEAM_BREAK(1, "Algae Beam Break");

            private final int id;
            private final String name;

            private static final Map<Integer, String> DIO_ID_MAP = new HashMap<>();

            static {
                for (DIO dioID : DIO.values()) {
                    DIO_ID_MAP.put(dioID.getId(), dioID.getName());
                }
            }

            DIO(int id, String name) {
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
                return DIO_ID_MAP.getOrDefault(id, "Unknown DIO ID");
            }
        }

        public static class VisionConstants {

            public static final double ACCURACY_LIMIT = 4;
            public static final double DEBOUNCE_TIME_SEC = Milliseconds.of(15).in(Seconds);
        }

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
            BACK_RIGHT_TURN(10, "Back Right Turn"),

            ELEVATOR_DRIVE_LEADER(17, "Elevator Drive Leader"),
            ELEVATOR_DRIVE_FOLLOWER(18, "Elevator Drive Follower"),

            GRABBER_DRIVE(15, "Grabber Drive"),
            CORAL_TOF_BACK(72, "Coral Back Time of Flight"),
            CORAL_TOF_FRONT(73, "Coral Front Time of Flight"),
            ARM_ANGLE(19, "Arm Angle"),

            PIGEON_GYRO(30, "Gyro"),

            CLIMB_PORT(16, "Climb Motor");

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

            // public static final int[] CANCODER_IDS = {
            // FRONT_LEFT_CANCODER.id,
            // FRONT_RIGHT_CANCODER.id,
            // BACK_LEFT_CANCODER.id,
            // BACK_RIGHT_CANCODER.id
            // };

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
    public static class LEDConstants {

        public static int rainbowFirstPixelHue = 0;

        public static final int LED_PORT = 0;

        public static final int LED_COUNT = 69;


    }

}