// package frc.robot.subsystems.swerve;

// import com.ctre.phoenix6.configs.MagnetSensorConfigs;
// import com.ctre.phoenix6.hardware.CANcoder;
// import com.ctre.phoenix6.signals.SensorDirectionValue;
// import com.ctre.phoenix6.sim.CANcoderSimState;
// import com.revrobotics.sim.SparkMaxSim;
// import com.revrobotics.sim.SparkRelativeEncoderSim;
// import com.revrobotics.spark.SparkMax;
// import com.revrobotics.spark.SparkBase.PersistMode;
// import com.revrobotics.spark.SparkBase.ResetMode;
// import com.revrobotics.spark.SparkLowLevel.MotorType;

// import edu.wpi.first.math.system.plant.DCMotor;
// import frc.robot.util.Configs;
// import frc.robot.Constants.Ports;

// public class SimSwerveModule {
//     private final SparkMax r_angleMotor, r_driveMotor;

//     private final SparkMaxSim s_angleMotor, s_driveMotor;

//     private final SparkRelativeEncoderSim s_angleEncoder, s_driveEncoder;

//     public SimSwerveModule(int id) {
//         r_angleMotor = new SparkMax(Ports.CANID.SWERVE_IDS[id][0], MotorType.kBrushless);
//         r_driveMotor = new SparkMax(Ports.CANID.SWERVE_IDS[id][1], MotorType.kBrushless);

//         r_angleMotor.configure(Configs.SwerveConfigs.ANGLE_CONFIG, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
//         r_driveMotor.configure(Configs.SwerveConfigs.DRIVE_CONFIG, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

//         s_angleMotor = new SparkMaxSim(r_angleMotor, DCMotor.getNEO(1));
//         s_driveMotor = new SparkMaxSim(r_driveMotor, DCMotor.getNEO(1));

//         s_angleEncoder = s_angleMotor.getRelativeEncoderSim();
//         s_driveEncoder = s_driveMotor.getRelativeEncoderSim();
//     }

// }
