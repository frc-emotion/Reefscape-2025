package frc.robot.util;

import com.revrobotics.spark.config.SparkMaxConfig;

import edu.wpi.first.units.Units;

import frc.robot.Constants.ElevatorConstants;

import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;


public class Configs {
    public static class ElevatorConfigs {
        public static final SparkMaxConfig ELEVATOR_CONFIG = new SparkMaxConfig();

        static {
            ELEVATOR_CONFIG.idleMode(IdleMode.kCoast)
            .smartCurrentLimit(ElevatorConstants.kSmartCurrentLimit)
            .secondaryCurrentLimit(ElevatorConstants.kSecondaryCurrentLimit);

            ELEVATOR_CONFIG.softLimit
            .forwardSoftLimitEnabled(true)
            .reverseSoftLimitEnabled(true)
            .forwardSoftLimit(Units.Inches.of(60).in(Units.Inches))
            .reverseSoftLimit(Units.Inches.of(0).in(Units.Inches));

            ELEVATOR_CONFIG.alternateEncoder
            .countsPerRevolution(ElevatorConstants.kEncoderCPR)
            .positionConversionFactor(ElevatorConstants.inchesPerCount);

            ELEVATOR_CONFIG.closedLoop.maxMotion
            .maxVelocity(ElevatorConstants.MAX_MOTOR_RPM)
            .maxAcceleration(ElevatorConstants.MAX_MOTOR_ACCELERATION)
            .allowedClosedLoopError(2);
            
            ELEVATOR_CONFIG.closedLoop.pid(ElevatorConstants.kP, ElevatorConstants.kI, ElevatorConstants.kD);
            
        }
    }
    
}
