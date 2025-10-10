package frc.robot;

// import frc.robot.subsystems.vision.Vision;

// public class RobotContainer {
//     private final Vision visionSubsystem = new Vision();

//     public RobotContainer() {

//     }
// }

import com.pathplanner.lib.auto.NamedCommands;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.units.measure.Distance;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.PowerDistribution;
import edu.wpi.first.wpilibj.PowerDistribution.ModuleType;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.RobotModeTriggers;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine.Direction;
import frc.robot.subsystems.swerve.SwerveSubsystem;
import frc.robot.subsystems.arm.ArmSubsystem;
import frc.robot.subsystems.climb.ClimbSubsystem;
import frc.robot.subsystems.climb.ClimbSubsystem.ClimbState;
import frc.robot.subsystems.elevator.ElevatorSubsystem;
import frc.robot.subsystems.grabber.GrabberSubsystem;
import frc.robot.subsystems.grabber.GrabberSubsystem.GrabType;
import frc.robot.commands.teleop.Climb.ClimbManualCommand;
import frc.robot.commands.teleop.Elevator.MoveElevatorManual;
import frc.robot.commands.teleop.Elevator.MoveElevatorPosition;
import frc.robot.commands.teleop.Elevator.ZeroElevatorCurrent;
import frc.robot.commands.teleop.Grabber.GrabberGrabCommand;
import frc.robot.commands.teleop.Grabber.GrabberHoldCommand;
import frc.robot.commands.teleop.Grabber.GrabberPlaceCommand;
import frc.robot.commands.teleop.arm.ArmManualCommand;
import frc.robot.commands.teleop.arm.MoveArmPosition;
import frc.robot.commands.functional.MainCommandFactory;

import frc.robot.Constants;
import frc.robot.Constants.ArmConstants;
import frc.robot.Constants.ElevatorConstants;
import frc.robot.Constants.OperatorConstants;
import frc.robot.util.TabManager;
import frc.robot.util.Configs.ElevatorConfigs;
import frc.robot.util.Faults.FaultManager;
import frc.robot.util.TabManager.SubsystemTab;
import frc.robot.util.tasks.auto.ScoreAlgae;
import frc.robot.util.tasks.general.AlgaeLevel;
import frc.robot.util.tasks.general.CoralLevel;
import frc.robot.util.tasks.general.ScoreCoral;
import frc.robot.util.tasks.positions.AlgaeScorePosition;
import frc.robot.util.tasks.positions.CoralPosition;
import frc.robot.util.tasks.teleop.PickupAlgae;
import frc.robot.util.tasks.teleop.PickupTask;

import static edu.wpi.first.units.Units.Inches;

import java.io.File;
import swervelib.SwerveInputStream;

/**
 * This class is where the bulk of the robot should be declared. Since
 * Command-based is a "declarative" paradigm, very
 * little robot logic should actually be handled in the {@link Robot} periodic
 * methods (other than the scheduler calls).
 * Instead, the structure of the robot (including subsystems, commands, and
 * trigger mappings) should be declared here.
 */
public class RobotContainer {

    private final CommandXboxController driverXbox = new CommandXboxController(0);
    private final CommandXboxController operatorXbox = new CommandXboxController(1);

    // The robot's subsystems and commands are defined here...
    private final SwerveSubsystem drivebase = new SwerveSubsystem(new File(Filesystem.getDeployDirectory(),
            "swerve/neo"));

    private final ElevatorSubsystem elevatorSubsystem = new ElevatorSubsystem(true);
    private final GrabberSubsystem grabberSubsystem = new GrabberSubsystem();
    private final ArmSubsystem armSubsystem = new ArmSubsystem();
    private final ClimbSubsystem climbSubsystem = new ClimbSubsystem();

    // private final PowerDistribution m_PDH = new
    // PowerDistribution(Constants.Ports.CANID.PDH.getId(), ModuleType.kRev);

    private final SendableChooser<Command> autoChooser;

    /**
     * Converts driver input into a field-relative ChassisSpeeds that is controlled
     * by angular velocity.
     */
    SwerveInputStream driveAngularVelocitySlow = SwerveInputStream.of(drivebase.getSwerveDrive(),
            () -> driverXbox.getLeftY() * -1,
            () -> driverXbox.getLeftX() * -1)
            .withControllerRotationAxis(() -> -driverXbox.getRightX())
            .deadband(OperatorConstants.DEADBAND)
            .scaleTranslation(0.35)
            .allianceRelativeControl(true);

    SwerveInputStream driveAngularVelocityMeduim = SwerveInputStream.of(drivebase.getSwerveDrive(),
            () -> driverXbox.getLeftY() * -1,
            () -> driverXbox.getLeftX() * -1)
            .withControllerRotationAxis(() -> -driverXbox.getRightX())
            .deadband(OperatorConstants.DEADBAND)
            .scaleTranslation(0.5)
            .allianceRelativeControl(true);

    SwerveInputStream driveAngularVelocityTurbo = SwerveInputStream.of(drivebase.getSwerveDrive(),
            () -> driverXbox.getLeftY() * -1,
            () -> driverXbox.getLeftX() * -1)
            .withControllerRotationAxis(() -> -driverXbox.getRightX())
            .deadband(OperatorConstants.DEADBAND)
            .scaleTranslation(0.8)
            .allianceRelativeControl(true);
    /**
     * Clone's the angular velocity input stream and converts it to a fieldRelative
     * input stream.
     */
    SwerveInputStream driveDirectAngle = driveAngularVelocityMeduim.copy()
            .withControllerHeadingAxis(driverXbox::getRightX,
                    driverXbox::getRightY)
            .headingWhile(true);

    /**
     * Clone's the angular velocity input stream and converts it to a robotRelative
     * input stream.
     */
    SwerveInputStream driveRobotOriented = driveAngularVelocityMeduim.copy().robotRelative(true)
            .allianceRelativeControl(false);

    SwerveInputStream driveAngularVelocityKeyboard = SwerveInputStream.of(drivebase.getSwerveDrive(),
            () -> -driverXbox.getLeftY(),
            () -> -driverXbox.getLeftX())
            .withControllerRotationAxis(() -> driverXbox.getRawAxis(
                    2))
            .deadband(OperatorConstants.DEADBAND)
            .scaleTranslation(0.8)
            .allianceRelativeControl(true);
    // Derive the heading axis with math!
    SwerveInputStream driveDirectAngleKeyboard = driveAngularVelocityKeyboard.copy()
            .withControllerHeadingAxis(() -> Math.sin(
                    driverXbox.getRawAxis(
                            2) *
                            Math.PI)
                    *
                    (Math.PI *
                            2),
                    () -> Math.cos(
                            driverXbox.getRawAxis(
                                    2) *
                                    Math.PI)
                            *
                            (Math.PI *
                                    2))
            .headingWhile(true);

    /**
     * The container for the robot. Contains subsystems, OI devices, and commands.
     */
    public RobotContainer() {
        autoChooser = new SendableChooser<Command>();
        // FaultManager.register(m_PDH);
        // Configure the trigger bindings
        configureDefaultCommands();
        initializeNamedCommands();
        configureUI();
        DriverStation.silenceJoystickConnectionWarning(true);
        // NamedCommands.registerCommand("test", Commands.print("I EXIST"));

        // operatorXbox.povDown()
        // .onTrue(
        // new SequentialCommandGroup(
        // MainCommandFactory.getPlacePrepCommand(
        // armSubsystem,
        // elevatorSubsystem,
        // new ScoreCoral(CoralLevel.L1)
        // ).onlyIf(() -> grabberSubsystem.getCurrentGrabType() == GrabType.CORAL),
        // MainCommandFactory.getPlacePrepCommand(
        // armSubsystem,
        // elevatorSubsystem,
        // new ScoreAlgae(AlgaeScorePosition.P)
        // ).onlyIf(() -> grabberSubsystem.getCurrentGrabType() == GrabType.ALGAE),
        // MainCommandFactory.getIntakeCommand(
        // armSubsystem,
        // elevatorSubsystem,
        // grabberSubsystem,
        // new PickupAlgae(AlgaeLevel.TOP_OF_CORAL)
        // ).onlyIf(() -> grabberSubsystem.getTargetGrabType() == GrabType.ALGAE &&
        // !grabberSubsystem.hasGamePiece())
        // )
        // );

        // operatorXbox.povRight()
        // .onTrue(
        // new SequentialCommandGroup(
        // MainCommandFactory.getPlacePrepCommand(
        // armSubsystem,
        // elevatorSubsystem,
        // new ScoreCoral(CoralLevel.L2)
        // ).onlyIf(() -> grabberSubsystem.getCurrentGrabType() == GrabType.CORAL),
        // MainCommandFactory.getIntakeCommand(
        // armSubsystem,
        // elevatorSubsystem,
        // grabberSubsystem,
        // new PickupAlgae(AlgaeLevel.L2)
        // ).onlyIf(() -> grabberSubsystem.getTargetGrabType() == GrabType.ALGAE &&
        // !grabberSubsystem.hasGamePiece())
        // )
        // );

        // operatorXbox.povLeft()
        // .onTrue(
        // new SequentialCommandGroup(
        // MainCommandFactory.getPlacePrepCommand(
        // armSubsystem,
        // elevatorSubsystem,
        // new ScoreCoral(CoralLevel.L2)
        // ).onlyIf(() -> grabberSubsystem.getCurrentGrabType() == GrabType.CORAL),
        // MainCommandFactory.getIntakeCommand(
        // armSubsystem,
        // elevatorSubsystem,
        // grabberSubsystem,
        // new PickupAlgae(AlgaeLevel.L3)
        // ).onlyIf(() -> grabberSubsystem.getTargetGrabType() == GrabType.ALGAE &&
        // !grabberSubsystem.hasGamePiece())
        // )
        // );

        // operatorXbox.povUp()
        // .onTrue(
        // new SequentialCommandGroup(
        // MainCommandFactory.getPlacePrepCommand(
        // armSubsystem,
        // elevatorSubsystem,
        // new ScoreCoral(CoralLevel.L4)
        // ).onlyIf(() -> grabberSubsystem.getCurrentGrabType() == GrabType.CORAL),
        // MainCommandFactory.getPlacePrepCommand(
        // armSubsystem,
        // elevatorSubsystem,
        // new ScoreAlgae(AlgaeScorePosition.R1)
        // ).onlyIf(() -> grabberSubsystem.getCurrentGrabType() == GrabType.ALGAE)
        // )
        // );

        // operatorXbox.y().onTrue(
        // MainCommandFactory.getArmElevatorPositionCommand(
        // armSubsystem,
        // elevatorSubsystem,
        // ElevatorConstants.CORAL_INTAKE_HEIGHT,
        // ArmConstants.CORAL_INTAKE_ANGLE
        // )
        // );

        // operatorXbox.start().onTrue( // Zero Elevator
        // new ZeroElevatorCurrent(elevatorSubsystem)
        // );

        // operatorXbox.b().onTrue(
        // MainCommandFactory.getPrepClimbCommand(
        // elevatorSubsystem,
        // armSubsystem,
        // climbSubsystem
        // )
        // );

        // operatorXbox.a().and(() -> climbSubsystem.getClimbState() ==
        // ClimbState.READY).onTrue(
        // MainCommandFactory.getClimbCommand(
        // elevatorSubsystem,
        // armSubsystem,
        // climbSubsystem
        // )
        // );

        // // right bumper sets type to co]\[ral, left bumper sets type to algae

        // operatorXbox.rightBumper()
        // .whileTrue(Commands.runOnce(() ->
        // grabberSubsystem.setTargetType(GrabType.CORAL)));

        // operatorXbox.leftBumper()
        // .whileTrue(Commands.runOnce(() ->
        // grabberSubsystem.setTargetType(GrabType.ALGAE)));

        /* --- SysId Controls --- */

        // Quasistatic Arm
        // operatorXbox.povUp().whileTrue(
        // armSubsystem.getSysIdQuasistatic(Direction.kForward));
        // operatorXbox.povDown().whileTrue(
        // armSubsystem.getSysIdQuasistatic(Direction.kReverse));

        // Dynamic Arm
        // operatorXbox.povDown().onTrue(
        // armSubsystem.getSysIdDynamic(Direction.kReverse)
        // );
        // operatorXbox.povUp().onTrue(
        // armSubsystem.getSysIdDynamic(Direction.kForward)

        // );

        // Quasistatic Elevator
        // operatorXbox.povUp().onTrue(
        // elevatorSubsystem.getSysIdQuasistatic(Direction.kForward)
        // );
        // operatorXbox.povDown().onTrue(
        // elevatorSubsystem.getSysIdQuasistatic(Direction.kReverse)
        // );

        // Dynamic Elevator
        // operatorXbox.povUp().onTrue(
        // elevatorSubsystem.getSysIdDynamic(Direction.kForward)
        // );
        // operatorXbox.povDown().onTrue(
        // elevatorSubsystem.getSysIdDynami(cDirection.kReverse)
        // );
    }

    private void configureUI() {
        autoChooser.setDefaultOption("Taxi Auto", getAutonomousCommand("Straight Test"));
        addAutoOption("S4-B Score");  
        addAutoOption("S2-G Score");
        addAutoOption("meter test"); 

        addAutoOption("persian u");
        addAutoOption("neev");
        addAutoOption("neev-L3");
        addAutoOption("neev-L4");
        
        TabManager.getInstance().accessTab(SubsystemTab.AUTO).add(autoChooser);
    }

    private void configureDefaultCommands() {
        RobotModeTriggers.autonomous().onTrue(Commands.runOnce(drivebase::zeroGyroWithAlliance));

        RobotModeTriggers.teleop().onTrue(Commands.runOnce(drivebase::zeroGyroWithAlliance));

        operatorXbox.povUp().whileTrue(
                MainCommandFactory.getArmElevatorPositionCommand(
                        armSubsystem,
                        elevatorSubsystem,
                        ElevatorConstants.CORAL_L4_HEIGHT,
                        ArmConstants.CORAL_L4_ANGLE));
        // new MoveArmPosition(
        // armSubsystem,
        // Rotation2d.fromDegrees(90),
        // () -> {
        // return ElevatorConstants.CORAL_L4_HEIGHT;
        // }));

        operatorXbox.povRight().whileTrue(
                MainCommandFactory.getArmElevatorPositionCommand(
                        armSubsystem,
                        elevatorSubsystem,
                        ElevatorConstants.CORAL_L3_HEIGHT,
                        ArmConstants.CORAL_L3_ANGLE));

        operatorXbox.povLeft().whileTrue(
                MainCommandFactory.getArmElevatorPositionCommand(
                        armSubsystem,
                        elevatorSubsystem,
                        ElevatorConstants.CORAL_L2_HEIGHT,
                        ArmConstants.CORAL_L2_ANGLE));

        operatorXbox.povDown().whileTrue(
                MainCommandFactory.getArmElevatorPositionCommand(
                        armSubsystem,
                        elevatorSubsystem,
                        ElevatorConstants.CORAL_INTAKE_HEIGHT,
                        ArmConstants.CORAL_INTAKE_ANGLE));

        armSubsystem.setDefaultCommand(
                new ArmManualCommand(
                        armSubsystem,
                        () -> {
                            double raw = -operatorXbox.getLeftY() * 0.58;
                            double sign = Math.signum(raw);
                            double in = Math.pow(raw, 2);

                            return sign * in;
                        }));

        operatorXbox.a().whileTrue(
                new ClimbManualCommand(climbSubsystem, () -> {
                    return 1.0;
                }));

        operatorXbox.b().whileTrue(
                new ClimbManualCommand(climbSubsystem, () -> {
                    return -1.0;
                }));

        operatorXbox.x().whileTrue(
                MainCommandFactory.getArmElevatorPositionCommand(
                        armSubsystem,
                        elevatorSubsystem,
                        ElevatorConstants.ALGAE_L2_CLEANING,
                        ArmConstants.ALGAE_L2_ANGLE
                    )
        );

        operatorXbox.y().whileTrue(
                MainCommandFactory.getArmElevatorPositionCommand(
                        armSubsystem,
                        elevatorSubsystem,
                        ElevatorConstants.ALGAE_L3_CLEANING,
                        ArmConstants.ALGAE_L3_ANGLE
                    )
        );

        elevatorSubsystem.setDefaultCommand(
                new MoveElevatorManual(elevatorSubsystem, () -> -operatorXbox.getRightY()));

        Command driveFieldOrientedAnglularVelocityMeduim = drivebase
                .driveFieldOriented(driveAngularVelocityMeduim);
        Command driveFieldOrientedAnglularVelocitySlow = drivebase.driveFieldOriented(driveAngularVelocitySlow);
        Command driveFieldOrientedAnglularVelocityTurbo = drivebase
                .driveFieldOriented(driveAngularVelocityTurbo);

        Command driveRobotOrientedAngularVelocity = drivebase.driveFieldOriented(driveRobotOriented);
        Command driveSetpointGen = drivebase.driveWithSetpointGeneratorFieldRelative(
                driveDirectAngle);
        Command driveFieldOrientedDirectAngleKeyboard = drivebase.driveFieldOriented(driveDirectAngleKeyboard);
        Command driveFieldOrientedAnglularVelocityKeyboard = drivebase.driveFieldOriented(driveAngularVelocityKeyboard);
        Command driveSetpointGenKeyboard = drivebase.driveWithSetpointGeneratorFieldRelative(driveDirectAngleKeyboard);

        if (Robot.isSimulation()) {
            drivebase.setDefaultCommand(driveFieldOrientedDirectAngleKeyboard);
            driverXbox.start()
                    .onTrue(Commands.runOnce(() -> drivebase
                            .resetOdometry(new Pose2d(3, 3, new Rotation2d()))));
            driverXbox.button(1).whileTrue(drivebase.sysIdDriveMotorCommand());

        } else {
            drivebase.setDefaultCommand(driveFieldOrientedAnglularVelocityMeduim);
        }

        if (DriverStation.isTest()) {
            drivebase.setDefaultCommand(driveFieldOrientedAnglularVelocityMeduim); // Overrides drive
                                                                                   // command above!

            driverXbox.x().whileTrue(Commands.runOnce(drivebase::lock, drivebase).repeatedly());
            driverXbox.y().whileTrue(drivebase.driveToDistanceCommand(1.0, 0.2));
            driverXbox.start().onTrue((Commands.runOnce(drivebase::zeroGyro)));
            driverXbox.back().whileTrue(drivebase.centerModulesCommand());
            driverXbox.leftBumper().whileTrue(Commands.none());
            driverXbox.rightBumper().whileTrue(Commands.none());

        } else {
            driverXbox.a().onTrue((Commands.runOnce(drivebase::zeroGyro)));
            // driverXbox.x().onTrue(Commands.runOnce(drivebase::addFakeVisionReading));
            driverXbox.x().whileTrue(Commands.runOnce(drivebase::lock, drivebase).repeatedly());

            driverXbox.b().whileTrue(
                    drivebase.driveToPose(
                            new Pose2d(new Translation2d(4, 4),
                                    Rotation2d.fromDegrees(0))));
            driverXbox.start().whileTrue(Commands.none());
            driverXbox.back().whileTrue(Commands.none());
            // driverXbox.leftBumper().whileTrue(Commands.runOnce(drivebase::lock,
            // drivebase).repeatedly());
            // driverXbox.rightBumper().onTrue(Commands.none());

            driverXbox.rightBumper().whileTrue(driveFieldOrientedAnglularVelocityTurbo);

            driverXbox.leftBumper().whileTrue(driveFieldOrientedAnglularVelocitySlow);

            // operatorXbox.povDown()
            // .onTrue(
            // MainCommandFactory.getPlacePrepCommand(
            // armSubsystem,
            // elevatorSubsystem,
            // new ScoreCoral(CoralPosition.A, CoralLevel.L1)
            // )
            // );

            // operatorXbox.povRight()
            // .onTrue(
            // new SequentialCommandGroup(
            // MainCommandFactory.getPlacePrepCommand(
            // armSubsystem,
            // elevatorSubsystem,
            // new ScoreCoral(null, CoralLevel.L2)
            // )
            // )
            // );

            // operatorXbox.povLeft()
            // .onTrue(
            // MainCommandFactory.getPlacePrepCommand(
            // armSubsystem,
            // elevatorSubsystem,
            // new ScoreCoral(null, CoralLevel.L3)
            // )
            // );

            // operatorXbox.povUp()
            // .onTrue(
            // MainCommandFactory.getPlacePrepCommand(
            // armSubsystem,
            // elevatorSubsystem,
            // new ScoreCoral(null, CoralLevel.L4)
            // )
            // );

            // operatorXbox.y().onTrue(
            // MainCommandFactory.getArmElevatorPositionCommand(
            // armSubsystem,
            // elevatorSubsystem,
            // ElevatorConstants.CORAL_INTAKE_HEIGHT,
            // ArmConstants.CORAL_INTAKE_ANGLE
            // )
            // );

            // armSubsystem.setDefaultCommand(
            // new ArmManualCommand(
            // armSubsystem,
            // () -> -operatorXbox.getRightY()));

            // operatorXbox.x().onTrue(new ZeroElevatorCurrent(elevatorSubsystem)); // Zero
            // Elevator

            // right trigger coral outtake
            // right bumper algae outtake

            // left trigger coral intake
            // left bumper algae intake

            operatorXbox.rightBumper()
                    .whileTrue(Commands.runOnce(() -> grabberSubsystem.setTargetType(GrabType.CORAL)));

            operatorXbox.leftBumper()
                    .whileTrue(Commands.runOnce(() -> grabberSubsystem.setTargetType(GrabType.ALGAE)));

            operatorXbox.rightTrigger(OperatorConstants.DEADBAND).whileTrue(
                    new GrabberPlaceCommand(grabberSubsystem));

            operatorXbox.leftTrigger(OperatorConstants.DEADBAND).whileTrue(
                    new GrabberGrabCommand(grabberSubsystem));

            // operatorXbox.rightStick().and(() -> Math.abs(operatorXbox.getRightY()) > 0.1)
            // .onTrue(elevatorSubsystem.runOnce(elevatorSubsystem::stop))
            // .whileTrue(elevatorSubsystem.run(() -> elevatorSubsystem
            // .moveSpeed(operatorXbox.getRightY() * 0.25)))
            // .onFalse(elevatorSubsystem.runOnce(elevatorSubsystem::stop));

        }
    }

    public void initializeNamedCommands() {
        NamedCommands.registerCommand(
                "DropCoral",
                new SequentialCommandGroup(
                        Commands.runOnce(()->grabberSubsystem.setTargetType(GrabType.CORAL)),
                        
                        new GrabberGrabCommand(grabberSubsystem)
                )
        );

        NamedCommands.registerCommand(
            "PrepL4",
            MainCommandFactory.getAutoArmElevatorPositionCommand(
                armSubsystem, 
                elevatorSubsystem, 
                ElevatorConstants.CORAL_L4_HEIGHT,
                ArmConstants.CORAL_L4_ANGLE
            )
        );

        NamedCommands.registerCommand(
            "PrepL3",
            MainCommandFactory.getAutoArmElevatorPositionCommand(
                armSubsystem, 
                elevatorSubsystem, 
                ElevatorConstants.CORAL_L3_HEIGHT,
                ArmConstants.CORAL_L3_ANGLE
            )
        );

        NamedCommands.registerCommand(
                "MaintainL2",
                MainCommandFactory.getArmElevatorPositionCommand(armSubsystem, elevatorSubsystem, ElevatorConstants.CORAL_L2_HEIGHT,
                ArmConstants.CORAL_L2_ANGLE)
        );

        NamedCommands.registerCommand(
                "MaintainL3",
                MainCommandFactory.getArmElevatorPositionCommand(armSubsystem, elevatorSubsystem, ElevatorConstants.CORAL_L3_HEIGHT,
                ArmConstants.CORAL_L3_ANGLE)
        );

        NamedCommands.registerCommand(
                "MaintainL4",
                MainCommandFactory.getArmElevatorPositionCommand(armSubsystem, elevatorSubsystem, ElevatorConstants.CORAL_L4_HEIGHT,
                ArmConstants.CORAL_L4_ANGLE)
        );

        NamedCommands.registerCommand(
            "PrepL2",
            MainCommandFactory.getAutoArmElevatorPositionCommand(
                armSubsystem, 
                elevatorSubsystem, 
                ElevatorConstants.CORAL_L2_HEIGHT,
                ArmConstants.CORAL_L2_ANGLE
            )
        );

        NamedCommands.registerCommand(
            "ResetIntake",
            MainCommandFactory.getAutoArmElevatorPositionCommand(
                armSubsystem, 
                elevatorSubsystem, 
                ElevatorConstants.CORAL_INTAKE_HEIGHT,
                ArmConstants.CORAL_INTAKE_ANGLE
            )
        );
    }

    /**
     * Use this to pass the autonomous command to the main {@link Robot} class.
     *
     * @return the command to run in autonomous
     */
    public Command getAutonomousCommand() {
        return autoChooser.getSelected();
    }

    private void addAutoOption(String name) {
        autoChooser.addOption(name, drivebase.getAutonomousCommand(name));
    }

    private Command getAutonomousCommand(String name) {
        return drivebase.getAutonomousCommand(name);
    }

    public void setMotorBrake(boolean brake) {
        drivebase.setMotorBrake(brake);
    }
}