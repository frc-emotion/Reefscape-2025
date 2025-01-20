package frc.robot.subsystems.swerve;

import edu.wpi.first.epilogue.Logged;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj2.command.Subsystem;
import frc.robot.util.TabManager;
import frc.robot.util.TabManager.SubsystemTab;

/**
 * Interface for a subsystem which controls the swerve drive base of the robot. Includes
 * methods for movement, swerve module control, and tracking robot pose. 
 */
public interface SwerveDrive extends Subsystem {
    /**
     * Controls the drive base through directional inputs.
     * 
     * @param speeds    The desired speeds for each direction
     * @param fieldRelative     Whether the input should be robot or field relative. 
     */
    void drive(ChassisSpeeds speeds, boolean fieldRelative);
    
    /**
     * 
     * @param states
     */
    void setModuleStates(SwerveModuleState[] states);

    /**
     * Retrieves the current heading of the robot.
     * 
     * @return The heading as a Rotation2d object
     */
    @Logged
    default Rotation2d getHeading() {
        return getPose().getRotation();
    }

    /**
     * Retrieves the current pose of the robot.
     * 
     * @return The pose of the robot.
     */
    Pose2d getPose();

    /**
     * Zeroes the heading.
     */
    default void resetHeading() {
        setHeading(new Rotation2d());
    }

    /**
     * Sets the heading of the robot to the given heading.
     * 
     * @param rotation The heading to reset to.
     */
    default void setHeading(Rotation2d rotation) {
        setPose(
            new Pose2d(
                getPose().getTranslation(),
                rotation
            )
        );
    }

    /**
     * Sets the pose of the robot to the given pose.
     * 
     * @param pose The pose to reset to.
     */
    void setPose(Pose2d pose);

    /**
     * Adds a vision pose to the pose estimator.
     * 
     * @param visionRobotPose   The last vision pose.
     * @param timestampSeconds  The timestamp of the pose.
     */
    void addVisionMeasurement(Pose2d visionRobotPose, double timestampSeconds);

    SwerveModulePosition[] getModulePositions();
    SwerveModuleState[] getModuleStates();
    SwerveModuleState[] getDesiredModuleStates();

    void updateShuffleboard();

}
