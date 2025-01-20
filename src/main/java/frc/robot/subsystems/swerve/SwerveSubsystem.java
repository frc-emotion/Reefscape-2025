package frc.robot.subsystems.swerve;

import com.studica.frc.AHRS;
import com.studica.frc.AHRS.NavXComType;

import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import frc.robot.util.Constants;
import frc.robot.util.Constants.DriveConstants;

public class SwerveSubsystem implements SwerveDrive {
    private final NEOSwerveModule[] swerveModules;

    private final AHRS gyro = new AHRS(NavXComType.kMXP_SPI);

    private final SwerveDrivePoseEstimator poseEstimator;
    
    public SwerveSubsystem() {
        swerveModules = new NEOSwerveModule[4];
        for(int i = 0; i < Constants.Ports.CANID.SWERVE_IDS.length - 1; i++) {
            swerveModules[i] = new NEOSwerveModule(i);
        }

        resetHeading();

        poseEstimator = new SwerveDrivePoseEstimator(
            DriveConstants.swerveDriveKinematics,
            getHeading(),
            getModulePositions(),
            getPose()
        );
    }

    @Override
    public void drive(ChassisSpeeds speeds, boolean fieldRelative) {
        if(fieldRelative) {
            speeds = ChassisSpeeds.fromRobotRelativeSpeeds(speeds, getHeading());
        }

        setModuleStates(DriveConstants.swerveDriveKinematics.toSwerveModuleStates(speeds));
    }

    @Override
    public void setModuleStates(SwerveModuleState[] states) {
        for(int i = 0; i < 4; i++) {
            swerveModules[i].setDesiredModuleState(states[i]);
        }
    }

    @Override
    public Rotation2d getHeading() {
        return gyro.getRotation2d();
    }

    @Override
    public Pose2d getPose() {
        return poseEstimator.getEstimatedPosition();
    }

    @Override
    public void setHeading(Rotation2d rotation) {
        gyro.setAngleAdjustment(rotation.getDegrees());
        gyro.reset();
    }

    @Override
    public void addVisionMeasurement(Pose2d visionRobotPose, double timestampSeconds) {
        poseEstimator.addVisionMeasurement(visionRobotPose, timestampSeconds);
    }

    @Override
    public void setPose(Pose2d pose) {
        setHeading(pose.getRotation());
        poseEstimator.resetPosition(getHeading(), getModulePositions(), pose);
    }

    @Override
    public SwerveModulePosition[] getModulePositions() {
        SwerveModulePosition[] swerveModulePositions = new SwerveModulePosition[4];

        for(int i = 0; i < 4; i++) {
            swerveModulePositions[i] = swerveModules[i].getModulePosition();
        }

        return swerveModulePositions;
    }

    @Override
    public SwerveModuleState[] getModuleStates() {
        SwerveModuleState[] swerveModuleStates = new SwerveModuleState[4];

        for(int i = 0; i < 4; i++) {
            swerveModuleStates[i] = swerveModules[i].getModuleState();
        }

        return swerveModuleStates;
    }

    @Override
    public SwerveModuleState[] getDesiredModuleStates() {
        SwerveModuleState[] desiredModuleStates = new SwerveModuleState[4];

        for(int i = 0; i < 4; i++) {
            desiredModuleStates[i] = swerveModules[i].getDesiredModuleState();
        }

        return desiredModuleStates;
    }

    @Override
    public void periodic() {
        SwerveDrive.super.periodic();
    }
}