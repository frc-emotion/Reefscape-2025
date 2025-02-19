package frc.robot;

import com.pathplanner.lib.auto.NamedCommands;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.PowerDistribution;
import edu.wpi.first.wpilibj.PowerDistribution.ModuleType;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.subsystems.swerve.SwerveSubsystem;
import frc.robot.subsystems.arm.ArmSubsystem;
import frc.robot.subsystems.elevator.ElevatorSubsystem;
import frc.robot.subsystems.grabber.GrabberSubsystem;
import frc.robot.subsystems.grabber.GrabberSubsystem.GrabType;
import frc.robot.commands.teleop.Elevator.ZeroElevatorCurrent;
import frc.robot.commands.teleop.Grabber.GrabberGrabCommand;
import frc.robot.commands.teleop.Grabber.GrabberHoldCommand;
import frc.robot.commands.functional.MainCommandFactory;
import frc.robot.commands.teleop.Elevator.MoveElevatorManual;

import frc.robot.Constants;
import frc.robot.Constants.ArmConstants;
import frc.robot.Constants.ElevatorConstants;
import frc.robot.Constants.OperatorConstants;
import frc.robot.util.Configs.ElevatorConfigs;
import frc.robot.util.Faults.FaultManager;
import frc.robot.util.tasks.general.CoralLevel;
import frc.robot.util.tasks.general.ScoreCoral;

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

    private final ElevatorSubsystem elevatorSubsystem = new ElevatorSubsystem();
    private final GrabberSubsystem grabberSubsystem = new GrabberSubsystem();
    private final ArmSubsystem armSubsystem = new ArmSubsystem();

    // private final PowerDistribution m_PDH = new
    // PowerDistribution(Constants.Ports.CANID.PDH.getId(), ModuleType.kRev);

    /**
     * Converts driver input into a field-relative ChassisSpeeds that is controlled
     * by angular velocity.
     */
    SwerveInputStream driveAngularVelocitySlow = SwerveInputStream.of(drivebase.getSwerveDrive(),
            () -> driverXbox.getLeftY() * -1,
            () -> driverXbox.getLeftX() * -1)
            .withControllerRotationAxis(driverXbox::getRightX)
            .deadband(OperatorConstants.DEADBAND)
            .scaleTranslation(0.35)
            .allianceRelativeControl(true);

    SwerveInputStream driveAngularVelocityMeduim = SwerveInputStream.of(drivebase.getSwerveDrive(),
            () -> driverXbox.getLeftY() * -1,
            () -> driverXbox.getLeftX() * -1)
            .withControllerRotationAxis(driverXbox::getRightX)
            .deadband(OperatorConstants.DEADBAND)
            .scaleTranslation(0.5)
            .allianceRelativeControl(true);

    SwerveInputStream driveAngularVelocityTurbo = SwerveInputStream.of(drivebase.getSwerveDrive(),
            () -> driverXbox.getLeftY() * -1,
            () -> driverXbox.getLeftX() * -1)
            .withControllerRotationAxis(driverXbox::getRightX)
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
        // FaultManager.register(m_PDH);
        // Configure the trigger bindings
        configureBindings();
        configureDefaultCommands();
        DriverStation.silenceJoystickConnectionWarning(true);
        // NamedCommands.registerCommand("test", Commands.print("I EXIST"));
    }

    private void configureSimulationBindings() {

    }

    /**
     * Use this method to define your trigger->command mappings. Triggers can be
     * created via the
     * {@link Trigger#Trigger(java.util.function.BooleanSupplier)} constructor with
     * an arbitrary predicate, or via the
     * named factories in
     * {@link edu.wpi.first.wpilibj2.command.button.CommandGenericHID}'s subclasses
     * for
     * {@link CommandXboxController
     * Xbox}/{@link edu.wpi.first.wpilibj2.command.button.CommandPS4Controller PS4}
     * controllers or {@link edu.wpi.first.wpilibj2.command.button.CommandJoystick
     * Flight joysticks}.
     */
    private void configureBindings() {

        Command driveFieldOrientedDirectAngle = drivebase.driveFieldOriented(driveDirectAngle);

        Command driveFieldOrientedAnglularVelocityMeduim = drivebase.driveFieldOriented(driveAngularVelocityMeduim);
        Command driveFieldOrientedAnglularVelocitySlow = drivebase.driveFieldOriented(driveAngularVelocitySlow);
        Command driveFieldOrientedAnglularVelocityTurbo = drivebase.driveFieldOriented(driveAngularVelocityTurbo);

        Command driveRobotOrientedAngularVelocity = drivebase.driveFieldOriented(driveRobotOriented);
        Command driveSetpointGen = drivebase.driveWithSetpointGeneratorFieldRelative(
                driveDirectAngle);
        Command driveFieldOrientedDirectAngleKeyboard = drivebase.driveFieldOriented(driveDirectAngleKeyboard);
        Command driveFieldOrientedAnglularVelocityKeyboard = drivebase.driveFieldOriented(driveAngularVelocityKeyboard);
        Command driveSetpointGenKeyboard = drivebase.driveWithSetpointGeneratorFieldRelative(
                driveDirectAngleKeyboard);

        if (Robot.isSimulation()) {
            drivebase.setDefaultCommand(driveFieldOrientedDirectAngleKeyboard);
            driverXbox.start()
                    .onTrue(Commands.runOnce(() -> drivebase.resetOdometry(new Pose2d(3, 3, new Rotation2d()))));
            driverXbox.button(1).whileTrue(drivebase.sysIdDriveMotorCommand());

        } else {
            drivebase.setDefaultCommand(driveFieldOrientedAnglularVelocityMeduim);
        }

        if (DriverStation.isTest()) {
            drivebase.setDefaultCommand(driveFieldOrientedAnglularVelocityMeduim); // Overrides drive command above!

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
                            new Pose2d(new Translation2d(4, 4), Rotation2d.fromDegrees(0))));
            driverXbox.start().whileTrue(Commands.none());
            driverXbox.back().whileTrue(Commands.none());
            // driverXbox.leftBumper().whileTrue(Commands.runOnce(drivebase::lock,
            // drivebase).repeatedly());
            // driverXbox.rightBumper().onTrue(Commands.none());

            driverXbox.rightBumper().whileTrue(driveFieldOrientedAnglularVelocityTurbo);

            driverXbox.leftBumper().whileTrue(driveFieldOrientedAnglularVelocitySlow);

            operatorXbox.povDown()
                .onTrue(
                    MainCommandFactory.getPlacePrepCommand(
                        armSubsystem,
                        elevatorSubsystem,
                        new ScoreCoral(null, CoralLevel.L1)
                    )
                );
            
            operatorXbox.povRight()
                .onTrue(
                    new SequentialCommandGroup(
                        MainCommandFactory.getPlacePrepCommand(
                            armSubsystem,
                            elevatorSubsystem,
                            new ScoreCoral(null, CoralLevel.L2)
                        )
                    )
                );

            operatorXbox.povLeft()
                .onTrue(
                    MainCommandFactory.getPlacePrepCommand(
                        armSubsystem,
                        elevatorSubsystem,
                        new ScoreCoral(null, CoralLevel.L3)
                    )
                );

            operatorXbox.povUp()
                .onTrue(
                    MainCommandFactory.getPlacePrepCommand(
                        armSubsystem,
                        elevatorSubsystem,
                        new ScoreCoral(null, CoralLevel.L4)
                    )
                );
            
            operatorXbox.y().onTrue(
                MainCommandFactory.getArmElevatorPositionCommand(
                    armSubsystem, 
                    elevatorSubsystem,
                    ElevatorConstants.CORAL_INTAKE_HEIGHT,
                    ArmConstants.CORAL_INTAKE_ANGLE
                )
            );
          
         operatorXbox.leftStick().and(() -> Math.abs(operatorXbox.getLeftY()) > 0.1)
                                         .onTrue(elevatorSubsystem.runOnce(elevatorSubsystem::stop))
                                         .whileTrue(elevatorSubsystem.run(() -> elevatorSubsystem
                                                         .moveSpeed(operatorXbox.getLeftY() * 0.75)))
                                        .onFalse(elevatorSubsystem.runOnce(elevatorSubsystem::stop));

            operatorXbox.x().onTrue(new ZeroElevatorCurrent(elevatorSubsystem)); // Zero Elevator

            // right trigger coral outtake
            // right bumper algae outtake

            // left trigger coral intake
            // left bumper algae intake

            operatorXbox.rightBumper()
                    .whileTrue(Commands.runOnce(() -> grabberSubsystem.setTargetType(GrabType.CORAL)));

            operatorXbox.leftBumper()
                .whileTrue(Commands.runOnce(() -> grabberSubsystem.setTargetType(GrabType.ALGAE)));

        }

    }

    private void configureDefaultCommands() {
        grabberSubsystem.setDefaultCommand(new GrabberHoldCommand(grabberSubsystem));
    }

    /**
     * Use this to pass the autonomous command to the main {@link Robot} class.
     *
     * @return the command to run in autonomous
     */
    public Command getAutonomousCommand() {
        return drivebase.getAutonomousCommand("Straight Test");
    }

    public void setMotorBrake(boolean brake) {
        drivebase.setMotorBrake(brake);
    }
}