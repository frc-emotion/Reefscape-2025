package frc.robot.subsystems.swerve;

import com.ctre.phoenix6.configs.MagnetSensorConfigs;
import com.ctre.phoenix6.hardware.CANcoder;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.spark.SparkClosedLoopController;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkBase.ControlType;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import frc.robot.util.Configs;
import frc.robot.util.Constants.DriveConstants.ModuleConstants;

public class NEOSwerveModule {
    private final SparkMax driveMotor, turnMotor;
    private final CANcoder absoluteTurnEncoder;

    private final SparkClosedLoopController driveController, turnController;

    private final RelativeEncoder driveEncoder, turnEncoder;

    private SwerveModuleState desiredState;

    public NEOSwerveModule(int id) {
        if(id < 0 || id > 3) throw new IndexOutOfBoundsException("Swerve Module index " + id + "out of bounds for length 4");

        driveMotor = new SparkMax(ModuleConstants.MOTOR_IDS[id][0], MotorType.kBrushless);
        driveMotor.configure(
            Configs.SwerveConfigs.DRIVE_CONFIG,
            ResetMode.kResetSafeParameters,
            PersistMode.kPersistParameters
        );
        driveEncoder = driveMotor.getEncoder();
        driveController = driveMotor.getClosedLoopController();

        turnMotor = new SparkMax(ModuleConstants.MOTOR_IDS[id][1], MotorType.kBrushless);
        turnMotor.configure(
            Configs.SwerveConfigs.TURN_CONFIG,
            ResetMode.kResetSafeParameters,
            PersistMode.kPersistParameters
        );
        turnEncoder = turnMotor.getEncoder();
        turnController = turnMotor.getClosedLoopController();

        absoluteTurnEncoder = new CANcoder(ModuleConstants.ENCODER_IDS[id]);

        MagnetSensorConfigs magnetSensorConfigs = new MagnetSensorConfigs();
        magnetSensorConfigs.withMagnetOffset(ModuleConstants.ENCODER_OFFSETS[id]);

        resetTurnEncoder();

        desiredState = new SwerveModuleState(0, getTurnPosition());
    }

    /**
     * Resets the RelativeEncoder of the NEO motor to the position of the CANcoder.
     */
    public void resetTurnEncoder() {
        turnEncoder.setPosition(absoluteTurnEncoder.getPosition().getValueAsDouble());
    }

    /**
     * Resets all module encoders 
     */
    public void resetEncoders() {
        resetTurnEncoder();
        driveEncoder.setPosition(0);
    }

    /**
     * Sets the target module state.
     *  
     * @param desiredState The desired state of the module
     */
    public void setDesiredModuleState(SwerveModuleState desiredState) {
        desiredState.optimize(getTurnPosition());
        this.desiredState = desiredState;

        turnController.setReference(desiredState.angle.getDegrees(), ControlType.kPosition);
        driveController.setReference(desiredState.speedMetersPerSecond, ControlType.kVelocity);
    }

    /**
     * Retrieves the last desired module state.
     * 
     * @return The current target module state
     */
    public SwerveModuleState getDesiredModuleState() {
        return desiredState;
    }

    /**
     * Retrieves the current module state.
     * 
     * @return The current module state
     */
    public SwerveModuleState getModuleState() {
        return new SwerveModuleState(
            getDriveVelocity(),
            getTurnPosition()
        );
    }

    /**
     * Retrieves the current module position.
     * 
     * @return The current module position
     */
    public SwerveModulePosition getModulePosition() {
        return new SwerveModulePosition(
            getDrivePosition(),
            getTurnPosition()
        );
    }

    private Rotation2d getTurnPosition() {
        return Rotation2d.fromDegrees(turnEncoder.getPosition());
    }

    private double getDriveVelocity() {
        return driveEncoder.getVelocity();
    }

    private double getDrivePosition() {
        return driveEncoder.getPosition();
    }
}
