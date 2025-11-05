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
import frc.robot.config.subsystems.GrabberConfig;
import frc.robot.constants.PortMap;
import frc.robot.constants.subsystems.GrabberConstants;
import frc.robot.util.diagnostics.Faults.FaultManager;
import frc.robot.util.diagnostics.Faults.FaultTypes.FaultType;

@Logged
public class GrabberSubsystem extends SubsystemBase {
    private SparkMax grabberMotor;
    private TimeOfFlight coralSensorFront, coralSensorBack;
    private DigitalInput algaeSensor;
    private GrabType targetType;

    public static enum GrabType {
        CORAL,
        ALGAE,
        NONE;
    };

    public static enum CoralState {
        FRONT,
        BACK,
        BOTH,
        NONE;
    }

    public GrabberSubsystem() {
        grabberMotor = new SparkMax(PortMap.CANID.GRABBER_DRIVE.getId(), MotorType.kBrushless);

        grabberMotor.configure(GrabberConfig.GRABBER_CONFIG, ResetMode.kResetSafeParameters,
                PersistMode.kPersistParameters);

        coralSensorFront = new TimeOfFlight(PortMap.CANID.CORAL_TOF_FRONT.getId());
        coralSensorBack = new TimeOfFlight(PortMap.CANID.CORAL_TOF_BACK.getId());
        algaeSensor = new DigitalInput(PortMap.DIO.ALGAE_BEAM_BREAK.getId());

        targetType = GrabType.NONE;

        FaultManager.register(grabberMotor);

        safetyChecks();
    }

    private void safetyChecks() {
        FaultManager.register(
                () -> grabberMotor.getOutputCurrent() > GrabberConstants.NORMAL_OPERATION_CURRENT
                        + GrabberConstants.CURRENT_SPIKE_THRESHOLD,
                "Grabber Drive",
                "Possible unsafe current spike",
                FaultType.WARNING);

        FaultManager.register(
                () -> grabberMotor.getMotorTemperature() > GrabberConstants.NORMAL_OPERATION_TEMP
                        + GrabberConstants.TEMP_SPIKE_THRESHOLD,
                "Grabber Drive",
                "Possible unsafe motor temperature",
                FaultType.WARNING);

    }

    public GrabType getTargetGrabType() {
        return targetType;
    }

    public GrabType getCurrentGrabType() {
        if (getAlgaeState()) {
            return GrabType.ALGAE;
        } else if (getCoralState() != CoralState.NONE) {
            return GrabType.CORAL;
        } else {
            return GrabType.NONE;
        }
    }

    public void setTargetType(GrabType type) {
        targetType = type;
    }

    public void set(double speed) {
        grabberMotor.set(speed);
    }

    public void stop() {
        grabberMotor.stopMotor();
    }

    public boolean getFrontCoralState() {
        return coralSensorFront.getRange() < GrabberConstants.CORAL_DETECT_RANGE;
    }

    public boolean getBackCoralState() {
        return coralSensorBack.getRange() < GrabberConstants.CORAL_DETECT_RANGE;
    }

    public double getBackCoral() {
        return coralSensorBack.getRange();
    }

    public CoralState getCoralState() {
        if (getFrontCoralState()) {
            if (getBackCoralState()) {
                return CoralState.BOTH;
            }
            return CoralState.FRONT;

        } else if (getBackCoralState()) {
            return CoralState.BACK;
        } else {
            return CoralState.NONE;
        }
    }

    public boolean getAlgaeState() {
        return algaeSensor.get();
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

        SmartDashboard.putNumber("Grabber/2/CoralBackBludYurr", getBackCoral());

        SmartDashboard.putBoolean("Grabber/1/Spiked", grabberMotor.getOutputCurrent() > GrabberConstants.CURRENT_SPIKE_THRESHOLD);
    }
} 