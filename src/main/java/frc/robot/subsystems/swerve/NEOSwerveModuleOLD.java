// package frc.robot.subsystems.swerve;

// import java.util.Map;

// import org.dyn4j.geometry.Rotation;

// import com.ctre.phoenix6.configs.MagnetSensorConfigs;
// import com.ctre.phoenix6.controls.ControlRequest;
// import com.ctre.phoenix6.hardware.CANcoder;
// import com.ctre.phoenix6.signals.SensorDirectionValue;
// import com.revrobotics.RelativeEncoder;
// import com.revrobotics.spark.SparkClosedLoopController;
// import com.revrobotics.spark.SparkMax;
// import com.revrobotics.spark.config.SparkBaseConfig;
// import com.revrobotics.spark.config.SparkMaxConfigAccessor;
// import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
// import com.revrobotics.spark.config.SparkMaxConfig;
// import com.revrobotics.spark.SparkBase.ControlType;
// import com.revrobotics.spark.SparkBase.PersistMode;
// import com.revrobotics.spark.SparkBase.ResetMode;
// import com.revrobotics.spark.SparkLowLevel.MotorType;

// import edu.wpi.first.epilogue.Logged;
// import edu.wpi.first.math.controller.PIDController;
// import edu.wpi.first.math.geometry.Rotation2d;
// import edu.wpi.first.math.kinematics.SwerveModulePosition;
// import edu.wpi.first.math.kinematics.SwerveModuleState;
// import edu.wpi.first.networktables.GenericEntry;
// import edu.wpi.first.networktables.NetworkTableEntry;
// import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;
// import frc.robot.util.Configs;
// import frc.robot.util.Constants;
// import frc.robot.util.Constants.DriveConstants.ModuleConstants;
// import frc.robot.util.Faults.FaultManager;

// public class NEOSwerveModuleOLD {
//     private final SparkMax driveMotor, angleMotor;
//     private final CANcoder absoluteAngleEncoder;

//     private final SparkClosedLoopController driveController, angleController;
//     private double angleSetpoint, driveSetpoint;

//     private final RelativeEncoder driveEncoder, angleEncoder;

//     private SwerveModuleState desiredState;

//     // NetworkTableEntries for PID, Setpoint, and Feedforward
//     private GenericEntry idleModeEntry;

//     // Drive PID Entries
//     private GenericEntry drivePEntry, driveIEntry, driveDEntry, driveFEntry;
//     private GenericEntry driveSetpointEntry;

//     // Angle PID Entries
//     private GenericEntry anglePEntry, angleIEntry, angleDEntry;
//     private GenericEntry angleSetpointEntry;

//     // Configuration Objects
//     private SparkMaxConfig driveConfig = new SparkMaxConfig().apply(Configs.SwerveConfigs.DRIVE_CONFIG);

//     public NEOSwerveModuleOLD(int id) {
//         if (id < 0 || id > Constants.Ports.CANID.SWERVE_IDS.length)
//             throw new IndexOutOfBoundsException("Swerve Module index " + id + " out of bounds for length " + Constants.Ports.CANID.SWERVE_IDS.length);

//         // Initialize Drive Motor
//         driveMotor = new SparkMax(Constants.Ports.CANID.SWERVE_IDS[id][0], MotorType.kBrushless);
//         driveMotor.configure(
//                 driveConfig,
//                 ResetMode.kResetSafeParameters,
//                 PersistMode.kPersistParameters);
//         driveEncoder = driveMotor.getEncoder();
//         driveController = driveMotor.getClosedLoopController();

//         // Initialize Angle Motor
//         angleMotor = new SparkMax(Constants.Ports.CANID.SWERVE_IDS[id][1], MotorType.kBrushless);

//         angleMotor.configure(
//                 Configs.SwerveConfigs.ANGLE_CONFIG,
//                 ResetMode.kResetSafeParameters,
//                 PersistMode.kPersistParameters);

//         angleEncoder = angleMotor.getEncoder();

//         angleController = angleMotor.getClosedLoopController();
        
//         // Initialize Absolute Angle Encoder
//         absoluteAngleEncoder = new CANcoder(Constants.Ports.CANID.CANCODER_IDS[id]);

//         // Configure Magnet Sensor
//         MagnetSensorConfigs magnetSensorConfigs = new MagnetSensorConfigs();
//         magnetSensorConfigs.withMagnetOffset(ModuleConstants.ENCODER_OFFSETS[id]);
//         magnetSensorConfigs.withSensorDirection(SensorDirectionValue.Clockwise_Positive);
//         magnetSensorConfigs.withAbsoluteSensorDiscontinuityPoint(1);

//         absoluteAngleEncoder.getConfigurator().apply(magnetSensorConfigs);

//         resetAngleEncoder();
//         driveEncoder.setPosition(0);

//         desiredState = new SwerveModuleState(0, getAnglePosition());

//         // Register Motors with Fault Manager
//         // FaultManager.register(driveMotor);
//         // FaultManager.register(angleMotor);
//     }

//     /**
//      * Sets the Idle Mode of both drive and angle motors.
//      *
//      * @param mode The desired IdleMode (kBrake or kCoast)
//      */
//     public void setIdleMode(IdleMode mode) {
//         SparkMaxConfig temp = new SparkMaxConfig().apply(Configs.SwerveConfigs.ANGLE_CONFIG);
//         temp.idleMode(mode);

//         driveConfig.idleMode(mode);

//         driveMotor.configure(driveConfig, ResetMode.kResetSafeParameters, PersistMode.kNoPersistParameters);
//         angleMotor.configure(temp, ResetMode.kResetSafeParameters, PersistMode.kNoPersistParameters);
//     }

//     /**
//      * Updates the PID constants for drive motor along with Feedforward.
//      *
//      * @param p Proportional coefficient
//      * @param i Integral coefficient
//      * @param d Derivative coefficient
//      * @param f Feedforward coefficient
//      */
//     public void updateDrivePID(double p, double i, double d, double f) {
//         driveConfig.closedLoop.pidf(p, i, d, f);
//         driveMotor.configure(driveConfig, ResetMode.kResetSafeParameters, PersistMode.kNoPersistParameters);
//     }

//     /**
//      * Updates the PID constants for angle motor.
//      *
//      * @param p Proportional coefficient
//      * @param i Integral coefficient
//      * @param d Derivative coefficient
//      */
//     public void updateAnglePID(double p, double i, double d) {
//         System.out.printf("P: %s | I: %s | D: %s %n", p, i, d);
//         SparkMaxConfig temp = new SparkMaxConfig().apply(Configs.SwerveConfigs.ANGLE_CONFIG);
//         temp.closedLoop.pid(p, i, d);

//         angleMotor.configure(temp, ResetMode.kResetSafeParameters, PersistMode.kNoPersistParameters);
//     }

//     private double getRawAbsoluteEncoder() {
//         return absoluteAngleEncoder.getAbsolutePosition().getValueAsDouble();
//     }

//     private double getAbsoluteEncoder() {
//         //return (absoluteAngleEncoder.getAbsolutePosition().getValueAsDouble() + 1) * 180;
//         return (absoluteAngleEncoder.getAbsolutePosition().getValueAsDouble()) * 360;
//         //return (absoluteAngleEncoder.getAbsolutePosition().getValueAsDouble() * 180) < 0 ;
//     }

//     /**
//      * Resets the RelativeEncoder of the angle motor to the position of the CANcoder.
//      */
//     public void resetAngleEncoder() {
//         double absAngle = getAbsoluteEncoder();
//         // Force it into [0..360)
//         absAngle = (absAngle % 360 + 360) % 360;
//         angleEncoder.setPosition(absAngle);
    
//     }

//     /**
//      * Resets all module encoders.
//      */
//     public void resetEncoders() {
//         resetAngleEncoder();
//         driveEncoder.setPosition(0);
//     }

//     /**
//      * Sets the target module state.
//      * 
//      * @param desiredState The desired state of the module
//      */
//     public void setDesiredModuleState(SwerveModuleState desiredState) {
//         // Optimize around the current angle
//         desiredState.optimize(getAnglePosition());
    
//         // The optimized angle can be [-180,180). Convert to a 0 - 360
//         double angleDeg = desiredState.angle.getDegrees();  // might be -10
//         angleDeg = (angleDeg % 360 + 360) % 360;         // now it’s in [0,360)
    
//         angleSetpoint = angleDeg;
//         driveSetpoint = desiredState.speedMetersPerSecond;
    
//         angleController.setReference(angleDeg, ControlType.kPosition); 
//         driveController.setReference(driveSetpoint, ControlType.kVelocity);
        
//     }    

//     /**
//      * Retrieves the last desired module state.
//      * 
//      * @return The current target module state
//      */
//     public SwerveModuleState getDesiredModuleState() {
//         return desiredState;
//     }

//     /**
//      * Retrieves the current module state.
//      * 
//      * @return The current module state
//      */
//     public SwerveModuleState getModuleState() {
//         return new SwerveModuleState(
//                 getDriveVelocity(),
//                 getAnglePosition());
//     }

//     /**
//      * Retrieves the current module position.
//      * 
//      * @return The current module position
//      */
//     public SwerveModulePosition getModulePosition() {
//         return new SwerveModulePosition(
//                 getDrivePosition(),
//                 getAnglePosition());
//     }

//     /**
//      * Stops the module by setting desired state to zero speed and current angle.
//      */
//     // public void stop() {
//     //     setDesiredModuleState(new SwerveModuleState());
//     // }

//     private Rotation2d getAnglePosition() {


//         // if(angleEncoder.getPosition() > 360)
//         // {
//         //     return Rotation2d.fromDegrees(angleEncoder.getPosition());
//         // }

//         // return Rotation2d.fromDegrees(angleEncoder.getPosition());
//         // //return new Rotation2d(Math.IEEEremainder(angleEncoder.getPosition(),360));
//         // //return new Rotation2d(10);
        
//         //if angleEncoder.getPosition() > 360:

//         // System.out.println(angleEncoder.getPosition());
//         // System.out.println(Math.IEEEremainder(Math.abs(angleEncoder.getPosition()), 360));
//         // return Rotation2d.fromDegrees(Math.IEEEremainder(Math.abs(angleEncoder.getPosition()), 360));
//         // return Rotation2d.fromDegrees(Math.IEEEremainder(Math.abs(angleEncoder.getPosition()), 360));
        
//         return Rotation2d.fromDegrees(angleEncoder.getPosition());

//     }


    

//     private double getDriveVelocity() {
//         return driveEncoder.getVelocity();
//     }

//     private double getDrivePosition() {
//         return driveEncoder.getPosition();
//     }

//     private Rotation2d getAbsolutePosition() {
//         return Rotation2d.fromDegrees(getAbsoluteEncoder());
//     }

//     @Logged
//     public double getAngleSetpoint() {
//        return angleSetpoint;
//     }

//     @Logged
//     public double getDriveSetpoint() {
//        return driveSetpoint;
//     }

//     public void initShuffleboard(ShuffleboardLayout layout) {
//         layout.addNumber(
//                 "Absolute Position",
//                 () -> getAbsolutePosition().getDegrees() % 360);
//         layout.addNumber("Absolute Raw Position",
//             () -> getRawAbsoluteEncoder()
//         );
//         layout.addNumber(
//                 "Integrated Position", 
//                 () -> (getAnglePosition().getDegrees() % 360 + 360) % 360);
//         layout.addNumber("Velocity", () -> getDriveVelocity());
//         layout.withSize(2, 4);
//     }

//     public void updateShuffleboard() {}
// }