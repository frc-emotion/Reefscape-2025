package frc.robot.subsystems.swerve;

import static edu.wpi.first.units.Units.Amps;
import static edu.wpi.first.units.Units.Inches;

import org.ironmaple.simulation.SimulatedArena;
import org.ironmaple.simulation.drivesims.COTS;
import org.ironmaple.simulation.drivesims.GyroSimulation;
import org.ironmaple.simulation.drivesims.SelfControlledSwerveDriveSimulation;
import org.ironmaple.simulation.drivesims.SwerveDriveSimulation;
import org.ironmaple.simulation.drivesims.SwerveModuleSimulation;
import org.ironmaple.simulation.drivesims.configs.DriveTrainSimulationConfig;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.math.system.plant.DCMotor;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.util.Constants.DriveConstants;
import frc.robot.util.Constants.DriveConstants.ModuleConstants;

public class MapleSwerveSubsystem implements SwerveDrive {
    private final SelfControlledSwerveDriveSimulation swerveDriveSimulation;
    private final Field2d field2d;

    public MapleSwerveSubsystem() {
        final DriveTrainSimulationConfig config = 
            DriveTrainSimulationConfig.Default()
            .withGyro(COTS.ofNav2X())
            .withSwerveModule(
                COTS.ofMark4i(
                    DCMotor.getNEO(1),
                    DCMotor.getNEO(1),
                    1,
                    2
                )
            )
            .withTrackLengthTrackWidth(Inches.of(DriveConstants.TRACK_WIDTH), Inches.of(DriveConstants.TRACK_WIDTH))
            .withBumperSize(Inches.of(30), Inches.of(30));
        
        this.swerveDriveSimulation = new SelfControlledSwerveDriveSimulation(
            new SwerveDriveSimulation(config, new Pose2d(3, 3, new Rotation2d()))
        );

        swerveDriveSimulation.withCurrentLimits(
            Amps.of(ModuleConstants.kDriveSecondaryCurrentLimit), 
            Amps.of(ModuleConstants.kTurnSmartCurrentLimit)
        );

        SimulatedArena.getInstance().addDriveTrainSimulation(swerveDriveSimulation.getDriveTrainSimulation());

        field2d = new Field2d();
        SmartDashboard.putData("Simulation Field", field2d);
    }

    @Override
    public void drive(ChassisSpeeds speeds, boolean fieldRelative) {
        speeds.omegaRadiansPerSecond = Units.degreesToRadians(speeds.omegaRadiansPerSecond);

        swerveDriveSimulation.runChassisSpeeds(
            speeds, 
            new Translation2d(),
            fieldRelative,
            false
        );
    }

    @Override
    public void setModuleStates(SwerveModuleState[] states) {
        swerveDriveSimulation.runSwerveStates(states);
    }

    @Override
    public Pose2d getPose() {
        return swerveDriveSimulation.getOdometryEstimatedPose();
    }

    @Override
    public void setPose(Pose2d pose) {
        swerveDriveSimulation.setSimulationWorldPose(pose);
        swerveDriveSimulation.resetOdometry(pose);
    }

    @Override
    public void addVisionMeasurement(Pose2d visionRobotPose, double timestampSeconds) {
        swerveDriveSimulation.addVisionEstimation(visionRobotPose, timestampSeconds);
    }

    @Override
    public void periodic() {
        SwerveDrive.super.periodic();

        swerveDriveSimulation.periodic();

        field2d.setRobotPose(swerveDriveSimulation.getActualPoseInSimulationWorld());
        // SmartDashboard.putData("Simulation Field", field2d);
    }
    
    @Override
    public SwerveModulePosition[] getModulePositions() {
        return swerveDriveSimulation.getLatestModulePositions();
    }

    @Override
    public SwerveModuleState[] getModuleStates() {
        return swerveDriveSimulation.getMeasuredStates();
    }

    @Override
    public SwerveModuleState[] getDesiredModuleStates() {
        return swerveDriveSimulation.getSetPointsOptimized();
    }
}
