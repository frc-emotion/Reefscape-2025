package frc.robot.subsystems.grabber;


import com.playingwithfusion.TimeOfFlight;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;

import edu.wpi.first.epilogue.Logged;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.GrabberConstants;
import frc.robot.Constants.Ports;
import frc.robot.util.Configs.GrabberConfigs;
import frc.robot.util.Faults.FaultManager;
import frc.robot.util.Faults.FaultTypes.FaultType;

@Logged
public class GrabberSubsystem extends SubsystemBase {
    private SparkMax grabberMotor;
        private DigitalInput coralSensor;
        private TimeOfFlight algaeSensor;
        private GrabType targetType;
        private GrabDirection targetDirection;

        public static enum GrabType { 
            CORAL,
            ALGAE,
            NONE;
        };

        public static enum GrabDirection {
            INTAKE,
            OUTTAKE
        }
                            
        public GrabberSubsystem() {
            grabberMotor = new SparkMax(Ports.CANID.GRABBER_DRIVE.getId(), MotorType.kBrushless);

            grabberMotor.configure(GrabberConfigs.GRABBER_CONFIG, ResetMode.kResetSafeParameters,
                PersistMode.kPersistParameters);

            coralSensor = new DigitalInput(Ports.DIO.CORAL_BEAM_BREAK.getId());
            algaeSensor = new TimeOfFlight(Ports.CANID.ALGAE_TOF.getId());

            targetType = GrabType.NONE;

            FaultManager.register(grabberMotor);

            safetyChecks();
        }

        private void safetyChecks() {
            FaultManager.register(
                    () -> grabberMotor.getOutputCurrent() > GrabberConstants.NORMAL_OPERATION_CURRENT + GrabberConstants.CURRENT_SPIKE_THRESHOLD,
                    "Grabber Drive",
                    "Possible unsafe current spike",
                    FaultType.WARNING);
    
            FaultManager.register(
                () -> grabberMotor.getMotorTemperature() > GrabberConstants.NORMAL_OPERATION_TEMP + GrabberConstants.TEMP_SPIKE_THRESHOLD,
                "Grabber Drive",
                "Possible unsafe motor temperature",
                FaultType.WARNING);
    
        }

        public GrabType getTargetGrabType() {
            return targetType;
        }

        public GrabType getCurrentGrabType() {
            if(getAlgaeState()) {
                return GrabType.ALGAE;
            } else if(getCoralState()) {
                return GrabType.CORAL;
            } else {
                return GrabType.NONE;
            }
        }

        public GrabDirection getTargetGrabDirection() {
            return targetDirection;
        }

        public void setTargetDirection(GrabDirection direction) {
            targetDirection = direction;
        }

        public void setTargetType(GrabType type) {
            targetType = type;
        }

        public void set(double speed){
            grabberMotor.set(speed);
        }

        public void stop() {
            grabberMotor.stopMotor();
        }

        public boolean getCoralState() {
            return coralSensor.get();
        }

        public boolean getAlgaeState() {
            return algaeSensor.getRange() < GrabberConstants.DETECT_RANGE;
        }

        public boolean hasGamePiece() {
            return getCurrentGrabType() == GrabType.NONE;
        }

        @Override
        public void periodic() {
            SmartDashboard.putNumber("Grabber/1/Output", grabberMotor.get());
            SmartDashboard.putNumber("Grabber/1/Voltage", grabberMotor.getBusVoltage());
            SmartDashboard.putNumber("Grabber/1/Current", grabberMotor.getOutputCurrent());
            SmartDashboard.putNumber("Grabber/1/Temp", grabberMotor.getMotorTemperature());
            SmartDashboard.putString("Grabber/1/Type", getCurrentGrabType().name());
            SmartDashboard.putString("Grabber/1/Direction", getTargetGrabDirection().name());
        }
}