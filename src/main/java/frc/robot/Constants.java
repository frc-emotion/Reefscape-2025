// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

/**
 * DEPRECATED: This class is kept for backward compatibility.
 * Please use the modular constants in the frc.robot.constants package instead.
 * 
 * New structure:
 * - frc.robot.constants.RobotConstants - Robot-wide constants
 * - frc.robot.constants.OperatorConstants - Controller settings
 * - frc.robot.constants.AutoConstants - Autonomous PID values
 * - frc.robot.constants.PortMap - CAN IDs and DIO ports
 * - frc.robot.constants.VisionConstants - Vision processing
 * - frc.robot.constants.subsystems.* - Subsystem-specific constants
 */
@Deprecated
public final class Constants {
    
    // Re-export classes for backward compatibility by aliasing
    public static final class AutonConstants {
        public static final com.pathplanner.lib.config.PIDConstants TRANSLATION_PID = 
            frc.robot.constants.AutoConstants.TRANSLATION_PID;
        public static final com.pathplanner.lib.config.PIDConstants ANGLE_PID = 
            frc.robot.constants.AutoConstants.ANGLE_PID;
    }
    
    public static final class DrivebaseConstants {
        public static final double WHEEL_LOCK_TIME = 
            frc.robot.constants.subsystems.DriveConstants.WHEEL_LOCK_TIME;
    }
    
    public static final class OperatorConstants {
        public static final double DEADBAND = frc.robot.constants.OperatorConstants.DEADBAND;
        public static final double LEFT_Y_DEADBAND = frc.robot.constants.OperatorConstants.LEFT_Y_DEADBAND;
        public static final double RIGHT_X_DEADBAND = frc.robot.constants.OperatorConstants.RIGHT_X_DEADBAND;
        public static final double TURN_CONSTANT = frc.robot.constants.OperatorConstants.TURN_CONSTANT;
    }
    
    public static final class ClimbConstants {
        public static final double kSpeed = frc.robot.constants.subsystems.ClimbConstants.kSpeed;
        public static final double SET_SPEED = frc.robot.constants.subsystems.ClimbConstants.SET_SPEED;
        public static final double POSITION_ERROR = frc.robot.constants.subsystems.ClimbConstants.POSITION_ERROR;
        public static final double EXTENSION_LIMIT = frc.robot.constants.subsystems.ClimbConstants.EXTENSION_LIMIT;
        public static final double EXTENDED_POSITION = frc.robot.constants.subsystems.ClimbConstants.EXTENDED_POSITION;
        public static final double CLIMBED_POSITION = frc.robot.constants.subsystems.ClimbConstants.CLIMBED_POSITION;
        public static final int kSmartCurrentLimit = frc.robot.constants.subsystems.ClimbConstants.kSmartCurrentLimit;
        public static final double kSecondaryCurrentLimit = frc.robot.constants.subsystems.ClimbConstants.kSecondaryCurrentLimit;
    }
    
    public static final class ElevatorConstants {
        // Re-export all elevator constants
        public static final double TOLERABLE_ERROR = frc.robot.constants.subsystems.ElevatorConstants.TOLERABLE_ERROR;
        public static final int kSmartCurrentLimit = frc.robot.constants.subsystems.ElevatorConstants.kSmartCurrentLimit;
        public static final int kSecondaryCurrentLimit = frc.robot.constants.subsystems.ElevatorConstants.kSecondaryCurrentLimit;
        public static final int kEncoderCPR = frc.robot.constants.subsystems.ElevatorConstants.kEncoderCPR;
        public static final double kpulleyDiameterInches = frc.robot.constants.subsystems.ElevatorConstants.kpulleyDiameterInches;
        public static final double kPulleyCircumInches = frc.robot.constants.subsystems.ElevatorConstants.kPulleyCircumInches;
        public static final double kGearRatio = frc.robot.constants.subsystems.ElevatorConstants.kGearRatio;
        public static final double effectiveCountsPerRevolution = frc.robot.constants.subsystems.ElevatorConstants.effectiveCountsPerRevolution;
        public static final double inchesPerCount = frc.robot.constants.subsystems.ElevatorConstants.inchesPerCount;
        public static final edu.wpi.first.units.measure.Mass kCarriageMass = frc.robot.constants.subsystems.ElevatorConstants.kCarriageMass;
        public static final edu.wpi.first.units.measure.Distance kDrumRadius = frc.robot.constants.subsystems.ElevatorConstants.kDrumRadius;
        public static final edu.wpi.first.units.measure.Distance kMinHeight = frc.robot.constants.subsystems.ElevatorConstants.kMinHeight;
        public static final edu.wpi.first.units.measure.Distance kMaxHeight = frc.robot.constants.subsystems.ElevatorConstants.kMaxHeight;
        public static final edu.wpi.first.units.measure.Distance kStartingHeight = frc.robot.constants.subsystems.ElevatorConstants.kStartingHeight;
        public static final double MAX_MOTOR_RPM = frc.robot.constants.subsystems.ElevatorConstants.MAX_MOTOR_RPM;
        public static final double MAX_MOTOR_ACCELERATION = frc.robot.constants.subsystems.ElevatorConstants.MAX_MOTOR_ACCELERATION;
        public static final double kS = frc.robot.constants.subsystems.ElevatorConstants.kS;
        public static final double kG = frc.robot.constants.subsystems.ElevatorConstants.kG;
        public static final double kV = frc.robot.constants.subsystems.ElevatorConstants.kV;
        public static final double kA = frc.robot.constants.subsystems.ElevatorConstants.kA;
        public static final double kP = frc.robot.constants.subsystems.ElevatorConstants.kP;
        public static final double kI = frc.robot.constants.subsystems.ElevatorConstants.kI;
        public static final double kD = frc.robot.constants.subsystems.ElevatorConstants.kD;
        public static final edu.wpi.first.units.measure.Distance CORAL_L1_HEIGHT = frc.robot.constants.subsystems.ElevatorConstants.CORAL_L1_HEIGHT;
        public static final edu.wpi.first.units.measure.Distance CORAL_L2_HEIGHT = frc.robot.constants.subsystems.ElevatorConstants.CORAL_L2_HEIGHT;
        public static final edu.wpi.first.units.measure.Distance CORAL_L3_HEIGHT = frc.robot.constants.subsystems.ElevatorConstants.CORAL_L3_HEIGHT;
        public static final edu.wpi.first.units.measure.Distance CORAL_L4_HEIGHT = frc.robot.constants.subsystems.ElevatorConstants.CORAL_L4_HEIGHT;
        public static final edu.wpi.first.units.measure.Distance CORAL_INTAKE_HEIGHT = frc.robot.constants.subsystems.ElevatorConstants.CORAL_INTAKE_HEIGHT;
        public static final edu.wpi.first.units.measure.Distance ALGAE_PREP_NET = frc.robot.constants.subsystems.ElevatorConstants.ALGAE_PREP_NET;
        public static final edu.wpi.first.units.measure.Distance ALGAE_PREP_PROCESSOR_HEIGHT = frc.robot.constants.subsystems.ElevatorConstants.ALGAE_PREP_PROCESSOR_HEIGHT;
        public static final edu.wpi.first.units.measure.Distance ALGAE_L3_CLEANING = frc.robot.constants.subsystems.ElevatorConstants.ALGAE_L3_CLEANING;
        public static final edu.wpi.first.units.measure.Distance ALGAE_L2_CLEANING = frc.robot.constants.subsystems.ElevatorConstants.ALGAE_L2_CLEANING;
        public static final edu.wpi.first.units.measure.Distance ALGAE_GROUND_INTAKE = frc.robot.constants.subsystems.ElevatorConstants.ALGAE_GROUND_INTAKE;
        public static final edu.wpi.first.units.measure.Distance PREP_0 = frc.robot.constants.subsystems.ElevatorConstants.PREP_0;
        public static final edu.wpi.first.units.measure.Distance DEADZONE_DISTANCE = frc.robot.constants.subsystems.ElevatorConstants.DEADZONE_DISTANCE;
        public static final int NORMAL_OPERATION_CURRENT = frc.robot.constants.subsystems.ElevatorConstants.NORMAL_OPERATION_CURRENT;
        public static final int CURRENT_SPIKE_THRESHOLD = frc.robot.constants.subsystems.ElevatorConstants.CURRENT_SPIKE_THRESHOLD;
        public static final int NORMAL_OPERATION_TEMP = frc.robot.constants.subsystems.ElevatorConstants.NORMAL_OPERATION_TEMP;
        public static final int TEMP_SPIKE_THRESHOLD = frc.robot.constants.subsystems.ElevatorConstants.TEMP_SPIKE_THRESHOLD;
    }
    
    public static final class GrabberConstants {
        public static final int kSmartCurrentLimit = frc.robot.constants.subsystems.GrabberConstants.kSmartCurrentLimit;
        public static final double kSecondaryCurrentLimit = frc.robot.constants.subsystems.GrabberConstants.kSecondaryCurrentLimit;
        public static final double GRABBER_CORAL_OUTTAKE = frc.robot.constants.subsystems.GrabberConstants.GRABBER_CORAL_OUTTAKE;
        public static final double GRABBER_CORAL_INTAKE = frc.robot.constants.subsystems.GrabberConstants.GRABBER_CORAL_INTAKE;
        public static final double GRABBER_ALGAE_SPEED = frc.robot.constants.subsystems.GrabberConstants.GRABBER_ALGAE_SPEED;
        public static final double GRABBER_ALGAE_HOLD_SPEED = frc.robot.constants.subsystems.GrabberConstants.GRABBER_ALGAE_HOLD_SPEED;
        public static final double GRABBER_CORAL_HOLD_SPEED = frc.robot.constants.subsystems.GrabberConstants.GRABBER_CORAL_HOLD_SPEED;
        public static final double ZERO_SPEED = frc.robot.constants.subsystems.GrabberConstants.ZERO_SPEED;
        public static final double CORAL_DETECT_RANGE = frc.robot.constants.subsystems.GrabberConstants.CORAL_DETECT_RANGE;
        public static final int NORMAL_OPERATION_CURRENT = frc.robot.constants.subsystems.GrabberConstants.NORMAL_OPERATION_CURRENT;
        public static final int CURRENT_SPIKE_THRESHOLD = frc.robot.constants.subsystems.GrabberConstants.CURRENT_SPIKE_THRESHOLD;
        public static final int NORMAL_OPERATION_TEMP = frc.robot.constants.subsystems.GrabberConstants.NORMAL_OPERATION_TEMP;
        public static final int TEMP_SPIKE_THRESHOLD = frc.robot.constants.subsystems.GrabberConstants.TEMP_SPIKE_THRESHOLD;
    }
    
    public static final class ArmConstants {
        public static final int kSmartCurrentLimit = frc.robot.constants.subsystems.ArmConstants.kSmartCurrentLimit;
        public static final double kSecondaryCurrentLimit = frc.robot.constants.subsystems.ArmConstants.kSecondaryCurrentLimit;
        public static final double kMaxOutput = frc.robot.constants.subsystems.ArmConstants.kMaxOutput;
        public static final double kMinRotation = frc.robot.constants.subsystems.ArmConstants.kMinRotation;
        public static final double kMaxRotation = frc.robot.constants.subsystems.ArmConstants.kMaxRotation;
        public static final double kMaxHeightConstrained = frc.robot.constants.subsystems.ArmConstants.kMaxHeightConstrained;
        public static final double kMaxRotationConstrained = frc.robot.constants.subsystems.ArmConstants.kMaxRotationConstrained;
        public static final double kMinRotationConstrained = frc.robot.constants.subsystems.ArmConstants.kMinRotationConstrained;
        public static final double kS = frc.robot.constants.subsystems.ArmConstants.kS;
        public static final double kG = frc.robot.constants.subsystems.ArmConstants.kG;
        public static final double kV = frc.robot.constants.subsystems.ArmConstants.kV;
        public static final double kA = frc.robot.constants.subsystems.ArmConstants.kA;
        public static final double kP = frc.robot.constants.subsystems.ArmConstants.kP;
        public static final double kI = frc.robot.constants.subsystems.ArmConstants.kI;
        public static final double kD = frc.robot.constants.subsystems.ArmConstants.kD;
        public static final double kMaxVelocity = frc.robot.constants.subsystems.ArmConstants.kMaxVelocity;
        public static final double kMaxAcceleration = frc.robot.constants.subsystems.ArmConstants.kMaxAcceleration;
        public static final double kMaxError = frc.robot.constants.subsystems.ArmConstants.kMaxError;
        public static final boolean kIsInverted = frc.robot.constants.subsystems.ArmConstants.kIsInverted;
        public static final double kZeroOffset = frc.robot.constants.subsystems.ArmConstants.kZeroOffset;
        public static final double kConversionFactor = frc.robot.constants.subsystems.ArmConstants.kConversionFactor;
        public static final double kInputSensitivity = frc.robot.constants.subsystems.ArmConstants.kInputSensitivity;
        public static final double kMaxInputAccel = frc.robot.constants.subsystems.ArmConstants.kMaxInputAccel;
        public static final edu.wpi.first.math.geometry.Rotation2d CORAL_L1_ANGLE = frc.robot.constants.subsystems.ArmConstants.CORAL_L1_ANGLE;
        public static final edu.wpi.first.math.geometry.Rotation2d CORAL_L2_ANGLE = frc.robot.constants.subsystems.ArmConstants.CORAL_L2_ANGLE;
        public static final edu.wpi.first.math.geometry.Rotation2d CORAL_L3_ANGLE = frc.robot.constants.subsystems.ArmConstants.CORAL_L3_ANGLE;
        public static final edu.wpi.first.math.geometry.Rotation2d CORAL_L4_ANGLE = frc.robot.constants.subsystems.ArmConstants.CORAL_L4_ANGLE;
        public static final edu.wpi.first.math.geometry.Rotation2d CORAL_INTAKE_ANGLE = frc.robot.constants.subsystems.ArmConstants.CORAL_INTAKE_ANGLE;
        public static final edu.wpi.first.math.geometry.Rotation2d ALGAE_L2_ANGLE = frc.robot.constants.subsystems.ArmConstants.ALGAE_L2_ANGLE;
        public static final edu.wpi.first.math.geometry.Rotation2d ALGAE_L3_ANGLE = frc.robot.constants.subsystems.ArmConstants.ALGAE_L3_ANGLE;
        public static final edu.wpi.first.math.geometry.Rotation2d ALGAE_GROUND_ANGLE = frc.robot.constants.subsystems.ArmConstants.ALGAE_GROUND_ANGLE;
        public static final edu.wpi.first.math.geometry.Rotation2d ALGAE_ON_CORAL_ANGLE = frc.robot.constants.subsystems.ArmConstants.ALGAE_ON_CORAL_ANGLE;
        public static final edu.wpi.first.math.geometry.Rotation2d ALGAE_NET_ANGLE = frc.robot.constants.subsystems.ArmConstants.ALGAE_NET_ANGLE;
        public static final edu.wpi.first.math.geometry.Rotation2d ALGAE_PRO_ANGLE = frc.robot.constants.subsystems.ArmConstants.ALGAE_PRO_ANGLE;
        public static final edu.wpi.first.math.geometry.Rotation2d CLIMB_ANGLE = frc.robot.constants.subsystems.ArmConstants.CLIMB_ANGLE;
        public static final int NORMAL_OPERATION_CURRENT = frc.robot.constants.subsystems.ArmConstants.NORMAL_OPERATION_CURRENT;
        public static final int NORMAL_OPERATION_TEMP = frc.robot.constants.subsystems.ArmConstants.NORMAL_OPERATION_TEMP;
        public static final int CURRENT_SPIKE_THRESHOLD = frc.robot.constants.subsystems.ArmConstants.CURRENT_SPIKE_THRESHOLD;
        public static final int TEMP_SPIKE_THRESHOLD = frc.robot.constants.subsystems.ArmConstants.TEMP_SPIKE_THRESHOLD;
    }
    
    public static final class Ports {
        public static final frc.robot.constants.PortMap.DIO DIO = null;
        public static final frc.robot.constants.PortMap.CANID CANID = null;
        
        public static final class VisionConstants {
            public static final double ACCURACY_LIMIT = frc.robot.constants.VisionConstants.ACCURACY_LIMIT;
            public static final double DEBOUNCE_TIME_SEC = frc.robot.constants.VisionConstants.DEBOUNCE_TIME_SEC;
        }
    }
    
    public static final class LEDConstants {
        public static int rainbowFirstPixelHue = frc.robot.constants.subsystems.LEDConstants.rainbowFirstPixelHue;
        public static final int LED_PORT = frc.robot.constants.subsystems.LEDConstants.LED_PORT;
        public static final int LED_COUNT = frc.robot.constants.subsystems.LEDConstants.LED_COUNT;
    }
    
    // Robot-wide constants
    public static final java.util.List<String> KNOWN_PD_FAULTS = frc.robot.constants.RobotConstants.KNOWN_PD_FAULTS;
    public static final double ROBOT_MASS = frc.robot.constants.RobotConstants.ROBOT_MASS;
    public static final swervelib.math.Matter CHASSIS = frc.robot.constants.RobotConstants.CHASSIS;
    public static final double LOOP_TIME = frc.robot.constants.RobotConstants.LOOP_TIME;
    public static final double MAX_SPEED = frc.robot.constants.RobotConstants.MAX_SPEED;

    private Constants() {
        throw new UnsupportedOperationException("This is a utility class!");
    }
}