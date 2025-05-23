package frc.robot.subsystems.climb;

import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.config.SparkMaxConfig;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.Constants.Ports.CANID;
import frc.robot.util.Configs;
import frc.robot.util.Configs.ClimbConfigs;

public class ClimbSubsystem extends SubsystemBase {

    private final SparkMax climbMotor;

    private ClimbState currentClimbState;

    public static enum ClimbState {
        STOWED,
        READY,
        CLIMBED;
    }

    public ClimbSubsystem() {
        climbMotor = new SparkMax(CANID.CLIMB_PORT.getId(), MotorType.kBrushless);
        climbMotor.configure(ClimbConfigs.CLIMB_CONFIG, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

        currentClimbState = ClimbState.STOWED;
    }

    public void setRawSpeed(double speed) {
        climbMotor.set(speed);
    }

    public void stop() {
        setRawSpeed(0);
    }

    public double getPosition() {
        return climbMotor.getEncoder().getPosition();
    }
    
    public ClimbState getClimbState() {
        return currentClimbState;
    }

    public void setClimbState(ClimbState climbState) {
        currentClimbState = climbState;
    }
}
