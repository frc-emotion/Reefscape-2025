// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.constants;

import java.util.HashMap;
import java.util.Map;

/**
 * Maps all CAN IDs and DIO ports used on the robot.
 */
public final class PortMap {
    
    /** Digital Input/Output port assignments */
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

    /** CAN bus device IDs */
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
         * Swerve module order:
         * 0 - Front Left
         * 1 - Front Right
         * 2 - Back Left
         * 3 - Back Right
         */

        /** Swerve module IDs [module][0=drive, 1=turn] */
        public static final int[][] SWERVE_IDS = {
            { FRONT_LEFT_DRIVE.id, FRONT_LEFT_TURN.id },
            { FRONT_RIGHT_DRIVE.id, FRONT_RIGHT_TURN.id },
            { BACK_LEFT_DRIVE.id, BACK_LEFT_TURN.id },
            { BACK_RIGHT_DRIVE.id, BACK_RIGHT_TURN.id }
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

    private PortMap() {
        throw new UnsupportedOperationException("This is a utility class!");
    }
}
