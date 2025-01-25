package frc.robot.subsystems.swerve;

import java.util.Map;

import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.studica.frc.AHRS;
import com.studica.frc.AHRS.NavXComType;

import edu.wpi.first.epilogue.Logged;
import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.util.Constants;
import frc.robot.util.Constants.DriveConstants;
import frc.robot.util.Constants.DriveConstants.ModuleConstants;
import frc.robot.util.TabManager;
import frc.robot.util.TabManager.SubsystemTab;

@Logged
public class SwerveSubsystem extends SubsystemBase {
    private final NEOSwerveModule[] swerveModules;

    private final AHRS gyro = new AHRS(NavXComType.kMXP_SPI);

    private final SwerveDrivePoseEstimator poseEstimator;

    private GenericEntry drivePEntry, driveIEntry, driveDEntry, driveFEntry;
    private GenericEntry anglePEntry, angleIEntry, angleDEntry, anglePtest;

    private GenericEntry idleModeEntry;

    private double driveP, driveI, driveD, driveF;
    private double angleP, angleI, angleD;

    public SwerveSubsystem() {
        swerveModules = new NEOSwerveModule[4];
        for (int i = 0; i < Constants.Ports.CANID.SWERVE_IDS.length; i++) {
            swerveModules[i] = new NEOSwerveModule(i);
        }

        resetHeading();

        poseEstimator = new SwerveDrivePoseEstimator(
                DriveConstants.swerveDriveKinematics,
                getHeading(),
                getModulePositions(),
                new Pose2d());

        // Initialize PID values from constants or defaults
        driveP = ModuleConstants.kDriveP;
        driveI = ModuleConstants.kDriveI;
        driveD = ModuleConstants.kDriveD;
        driveF = ModuleConstants.driveVelocityFeedForward;

        angleP = ModuleConstants.kAngleP;
        angleI = ModuleConstants.kAngleI;
        angleD = ModuleConstants.kAngleD;

        // Apply initial PID values to all modules
        applyPIDToAllModules();
        
        initShuffleboardTab();

    }

    public void drive(ChassisSpeeds speeds, boolean fieldRelative) {
        if (fieldRelative) {
            speeds = ChassisSpeeds.fromFieldRelativeSpeeds(
                speeds.vxMetersPerSecond,
                speeds.vyMetersPerSecond,
                speeds.omegaRadiansPerSecond,
                getHeading()
            );
        }
        setModuleStates(DriveConstants.swerveDriveKinematics.toSwerveModuleStates(speeds));
    }

    public void setModuleStates(SwerveModuleState[] states) {
        SwerveDriveKinematics.desaturateWheelSpeeds(
            states, Constants.DriveConstants.tempVelMax);
            
        for (int i = 0; i < swerveModules.length; i++) {
            swerveModules[i].setDesiredModuleState(states[i]);
        }
    }

    @Logged
    public Rotation2d getHeading() {
        return gyro.getRotation2d();
    }

    @Logged
    public Pose2d getPose() {
        return poseEstimator.getEstimatedPosition();
    }

    public void setHeading(Rotation2d rotation) {
        // TODO: Check if this is right
        gyro.setAngleAdjustment(rotation.getDegrees());
        gyro.reset();
    }

    public void addVisionMeasurement(Pose2d visionRobotPose, double timestampSeconds) {
        poseEstimator.addVisionMeasurement(visionRobotPose, timestampSeconds);
    }

    public void setPose(Pose2d pose) {
        setHeading(pose.getRotation());
        poseEstimator.resetPosition(getHeading(), getModulePositions(), pose);
    }

    public SwerveModulePosition[] getModulePositions() {
        SwerveModulePosition[] swerveModulePositions = new SwerveModulePosition[4];

        for (int i = 0; i <= swerveModules.length - 1; i++) {
            swerveModulePositions[i] = swerveModules[i].getModulePosition();
        }

        return swerveModulePositions;
    }

    @Logged
    public double[] getAngleSetpoints() {
        double[] setpoints = new double[swerveModules.length];

        for (int i = 0; i < setpoints.length; i++) {
            setpoints[i] = swerveModules[i].getAngleSetpoint();
        }

        return setpoints;
        
    }

    @Logged
    public double[] getDriveSetpoints() {
        double[] setpoints = new double[swerveModules.length];

        for (int i = 0; i < setpoints.length; i++) {
            setpoints[i] = swerveModules[i].getDriveSetpoint();
        }

        return setpoints;
        
    }

    @Logged
    public SwerveModuleState[] getModuleStates() {
        SwerveModuleState[] swerveModuleStates = new SwerveModuleState[4];

        for (int i = 0; i < swerveModules.length; i++) {
            swerveModuleStates[i] = swerveModules[i].getModuleState();
        }

        return swerveModuleStates;
    }

    @Logged
    public SwerveModuleState[] getDesiredModuleStates() {
        SwerveModuleState[] desiredModuleStates = new SwerveModuleState[4];

        for (int i = 0; i < swerveModules.length; i++) {
            desiredModuleStates[i] = swerveModules[i].getDesiredModuleState();
        }

        return desiredModuleStates;
    }

    private void resetHeading() {
        setHeading(new Rotation2d());
    }

    private String nameFromIndex(int i) {
        switch (i) {
            case 0:
            return "Front Left Module";
            case 1:
                return "Front Right Module";
            case 2:
                return "Back Left Module";
            case 3:
                return "Back Right Module";
            default:
                return "Unknown Module";
        }
    }

   private void initShuffleboardTab() {
        ShuffleboardTab tab = TabManager.getInstance().accessTab(SubsystemTab.DRIVETRAIN);

        anglePtest = tab.add("test angle", angleP).withWidget("Text View").getEntry();

        ShuffleboardLayout globalLayout = tab.getLayout("Global PID & Settings", BuiltInLayouts.kList)
            .withSize(4, 6)
            .withPosition(0, 0);

        idleModeEntry = globalLayout.add("Idle Mode", "Brake")
                .withWidget("Combo Box Chooser")
                .withSize(2, 1)
                .withProperties(Map.of("Options", new String[] { "Brake", "Coast" }))
                .getEntry();

        drivePEntry = globalLayout.add("Drive P", driveP)
                .withWidget("Text View")
                .withSize(2, 1)
                .getEntry();
        driveIEntry = globalLayout.add("Drive I", driveI)
                .withWidget("Text View")
                .withSize(2, 1)
                
                .getEntry();
        driveDEntry = globalLayout.add("Drive D", driveD)
                .withWidget("Text View")
                .withSize(2, 1)
                
                .getEntry();
        driveFEntry = globalLayout.add("Drive F", driveF)
                .withWidget("Text View")
                .withSize(2, 1)
                
                .getEntry();

        anglePEntry = globalLayout.add("Angle P", angleP)
                .withWidget("Text View")
                .withSize(2, 1)
                
                .getEntry();
        angleIEntry = globalLayout.add("Angle I", angleI)
                .withWidget("Text View")
                .withSize(2, 1)
                
                .getEntry();
        angleDEntry = globalLayout.add("Angle D", angleD)
                .withWidget("Text View")
                .withSize(2, 1)
                
                .getEntry();

        for (int i = 0; i < swerveModules.length; i++) {
            NEOSwerveModule module = swerveModules[i];
            
            ShuffleboardLayout moduleLayout = tab.getLayout(nameFromIndex(i), BuiltInLayouts.kList)
                .withSize(4, 8)
                .withPosition(5 + (i * 2), 2);
                
            module.initShuffleboard(moduleLayout);
        }
    }

    /**
     * Applies the current global PID values to all swerve modules.
     */
    private void applyPIDToAllModules() {
        for (NEOSwerveModule module : swerveModules) {
            module.updateDrivePID(driveP, driveI, driveD, driveF);
            module.updateAnglePID(angleP, angleI, angleD);
        }
    }

    /**
     * Applies the current global Idle Mode to all swerve modules.
     */
    private void applyIdleModeToAllModules() {
        String mode = idleModeEntry.getString("Brake");
        IdleMode idleMode = mode.equals("Brake") ? IdleMode.kBrake : IdleMode.kCoast;
        for (NEOSwerveModule module : swerveModules) {
            module.setIdleMode(idleMode);
        }
    }

    /**
     * Updates global Shuffleboard values and applies changes if any.
     */
    public void updateShuffleboard() {
        if (idleModeEntry != null) {
            applyIdleModeToAllModules();
        }
    
        // Apply PID values from Shuffleboard
        driveP = drivePEntry.getDouble(driveP);
        driveI = driveIEntry.getDouble(driveI);
        driveD = driveDEntry.getDouble(driveD);
        driveF = driveFEntry.getDouble(driveF);
        angleP = anglePEntry.getDouble(angleP);
        angleI = angleIEntry.getDouble(angleI);
        angleD = angleDEntry.getDouble(angleD);

        


        System.out.printf("Get double: %s", anglePtest.getDouble(100));
        System.out.printf("Get string: %s", anglePtest.getString("yurr"));
    
        applyPIDToAllModules();
    }

    public void periodic() {
        poseEstimator.update(
            getHeading(), 
            getModulePositions()
        );

        // When robot is disabled, ensure wheels are stopped
        // if (DriverStation.isDisabled()) {
        //     for (var module : swerveModules) {
        //         module.stop();
        //     }
        // }
    }
}