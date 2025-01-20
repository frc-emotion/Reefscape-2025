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
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import frc.robot.util.Constants;
import frc.robot.util.Constants.DriveConstants;
import frc.robot.util.Constants.DriveConstants.ModuleConstants;
import frc.robot.util.TabManager;
import frc.robot.util.TabManager.SubsystemTab;

@Logged
public class SwerveSubsystem implements SwerveDrive {
    private final NEOSwerveModule[] swerveModules;

    private final AHRS gyro = new AHRS(NavXComType.kMXP_SPI);

    private final SwerveDrivePoseEstimator poseEstimator;

    private GenericEntry drivePEntry, driveIEntry, driveDEntry, driveFEntry;
    private GenericEntry anglePEntry, angleIEntry, angleDEntry;

    private GenericEntry idleModeEntry;

    private GenericEntry driveSetpointEntry;
    private GenericEntry angleSetpointEntry;

    private double driveP, driveI, driveD, driveF;
    private double angleP, angleI, angleD;

    public SwerveSubsystem() {
        swerveModules = new NEOSwerveModule[4];
        for (int i = 0; i < Constants.Ports.CANID.SWERVE_IDS.length - 1; i++) {
            swerveModules[i] = new NEOSwerveModule(i);
        }

        resetHeading();

        poseEstimator = new SwerveDrivePoseEstimator(
                DriveConstants.swerveDriveKinematics,
                getHeading(),
                getModulePositions(),
                new Pose2d());

        initShuffleboardTab();

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
    }

    @Override
    public void drive(ChassisSpeeds speeds, boolean fieldRelative) {
        if (fieldRelative) {
            speeds = ChassisSpeeds.fromRobotRelativeSpeeds(speeds, getHeading());
        }

        setModuleStates(DriveConstants.swerveDriveKinematics.toSwerveModuleStates(speeds));
    }

    @Override
    public void setModuleStates(SwerveModuleState[] states) {
        for (int i = 0; i < swerveModules.length - 1; i++) {
            swerveModules[i].setDesiredModuleState(states[i]);
        }
    }

    @Override
    @Logged
    public Rotation2d getHeading() {
        return gyro.getRotation2d();
    }

    @Override
    @Logged
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

        for (int i = 0; i < swerveModules.length - 1; i++) {
            swerveModulePositions[i] = swerveModules[i].getModulePosition();
        }

        return swerveModulePositions;
    }

    @Override
    @Logged
    public SwerveModuleState[] getModuleStates() {
        SwerveModuleState[] swerveModuleStates = new SwerveModuleState[4];

        for (int i = 0; i < swerveModules.length - 1; i++) {
            swerveModuleStates[i] = swerveModules[i].getModuleState();
        }

        return swerveModuleStates;
    }

    @Override
    @Logged
    public SwerveModuleState[] getDesiredModuleStates() {
        SwerveModuleState[] desiredModuleStates = new SwerveModuleState[4];

        for (int i = 0; i < swerveModules.length - 1; i++) {
            desiredModuleStates[i] = swerveModules[i].getDesiredModuleState();
        }

        return desiredModuleStates;
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

        ShuffleboardLayout globalLayout = tab.getLayout("Global PID & Settings", BuiltInLayouts.kList)
            .withSize(4, 6)
            .withPosition(0, 0);

        idleModeEntry = globalLayout.add("Idle Mode", "Brake")
                .withWidget("Combo Box Chooser")
                .withSize(2, 1)
                .withProperties(Map.of("Options", new String[] { "Brake", "Coast" }))
                .getEntry();

        drivePEntry = globalLayout.add("Drive P", driveP)
                .withWidget("Number Slider")
                .withSize(2, 1)
                .withProperties(Map.of("min", 0.0, "max", 1.0))
                .getEntry();
        driveIEntry = globalLayout.add("Drive I", driveI)
                .withWidget("Number Slider")
                .withSize(2, 1)
                .withProperties(Map.of("min", 0.0, "max", 1.0))
                .getEntry();
        driveDEntry = globalLayout.add("Drive D", driveD)
                .withWidget("Number Slider")
                .withSize(2, 1)
                .withProperties(Map.of("min", 0.0, "max", 1.0))
                .getEntry();
        driveFEntry = globalLayout.add("Drive F", driveF)
                .withWidget("Number Slider")
                .withSize(2, 1)
                .withProperties(Map.of("min", 0.0, "max", 1.0))
                .getEntry();

        anglePEntry = globalLayout.add("Angle P", angleP)
                .withWidget("Number Slider")
                .withSize(2, 1)
                .withProperties(Map.of("min", 0.0, "max", 1.0))
                .getEntry();
        angleIEntry = globalLayout.add("Angle I", angleI)
                .withWidget("Number Slider")
                .withSize(2, 1)
                .withProperties(Map.of("min", 0.0, "max", 1.0))
                .getEntry();
        angleDEntry = globalLayout.add("Angle D", angleD)
                .withWidget("Number Slider")
                .withSize(2, 1)
                .withProperties(Map.of("min", 0.0, "max", 1.0))
                .getEntry();

        driveSetpointEntry = globalLayout.add("Drive Setpoint", 0.0)
                .withWidget("Number Slider")
                .withSize(2, 1)
                .withProperties(Map.of("min", -100.0, "max", 100.0))
                .getEntry();
        angleSetpointEntry = globalLayout.add("Angle Setpoint", 0.0)
                .withWidget("Number Slider")
                .withSize(2, 1)
                .withProperties(Map.of("min", -180.0, "max", 180.0))
                .getEntry();

        for (int i = 0; i < swerveModules.length; i++) {
            NEOSwerveModule module = swerveModules[i];
            
            ShuffleboardLayout moduleLayout = tab.getLayout(nameFromIndex(i), BuiltInLayouts.kList)
                .withSize(4, 6)
                .withPosition(i * 4, 6);
                
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

        if (drivePEntry != null && driveIEntry != null && driveDEntry != null && driveFEntry != null) {
            double newDriveP = drivePEntry.getDouble(driveP);
            double newDriveI = driveIEntry.getDouble(driveI);
            double newDriveD = driveDEntry.getDouble(driveD);
            double newDriveF = driveFEntry.getDouble(driveF);
            if (newDriveP != driveP || newDriveI != driveI || newDriveD != driveD || newDriveF != driveF) {
                driveP = newDriveP;
                driveI = newDriveI;
                driveD = newDriveD;
                driveF = newDriveF;
                for (NEOSwerveModule module : swerveModules) {
                    module.updateDrivePID(driveP, driveI, driveD, driveF);
                }
            }
        }

        if (anglePEntry != null && angleIEntry != null && angleDEntry != null) {
            double newAngleP = anglePEntry.getDouble(angleP);
            double newAngleI = angleIEntry.getDouble(angleI);
            double newAngleD = angleDEntry.getDouble(angleD);
            if (newAngleP != angleP || newAngleI != angleI || newAngleD != angleD) {
                angleP = newAngleP;
                angleI = newAngleI;
                angleD = newAngleD;
                for (NEOSwerveModule module : swerveModules) {
                    module.updateAnglePID(angleP, angleI, angleD);
                }
            }
        }

        if (driveSetpointEntry != null) {
            double setpoint = driveSetpointEntry.getDouble(0.0);
            for (NEOSwerveModule module : swerveModules) {
                module.setDriveSetpoint(setpoint);
            }
        }

        if (angleSetpointEntry != null) {
            double setpoint = angleSetpointEntry.getDouble(0.0);
            for (NEOSwerveModule module : swerveModules) {
                module.setAngleSetpoint(setpoint);
            }
        }
    }

    @Override
    public void periodic() {
        SwerveDrive.super.periodic();

        // When robot is disabled, ensure wheels are stopped
        if (DriverStation.isDisabled()) {
            for (var module : swerveModules) {
                module.stop();
            }
        }
    }
}