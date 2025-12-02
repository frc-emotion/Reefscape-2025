package frc.robot.controls;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.Robot;
import frc.robot.constants.OperatorConstants;
import frc.robot.statemachine.SuperstructureStateMachine;
import frc.robot.subsystems.swerve.SwerveSubsystem;
import swervelib.SwerveInputStream;

/**
 * Manages driver Xbox controller bindings and drive configurations.
 * Provides speed modes (slow, medium, turbo) and simulation/test bindings.
 */
public class DriverControls {

    private final CommandXboxController controller;
    private final SwerveSubsystem drivebase;
    private final SuperstructureStateMachine stateMachine;

    // Drive speed configurations
    private static final double SLOW_SPEED = 0.35;
    private static final double MEDIUM_SPEED = 0.5;
    private static final double TURBO_SPEED = 0.8;

    // Drive input streams
    private final SwerveInputStream driveAngularVelocitySlow;
    private final SwerveInputStream driveAngularVelocityMedium;
    private final SwerveInputStream driveAngularVelocityTurbo;
    private final SwerveInputStream driveAngularVelocityKeyboard;
    private final SwerveInputStream driveDirectAngleKeyboard;

    /**
     * Creates driver controls for the specified controller and drivebase.
     * 
     * @param controller   The driver's Xbox controller
     * @param drivebase    The swerve drive subsystem
     * @param stateMachine The state machine coordinator
     */
    public DriverControls(CommandXboxController controller, SwerveSubsystem drivebase,
            SuperstructureStateMachine stateMachine) {
        this.controller = controller;
        this.drivebase = drivebase;
        this.stateMachine = stateMachine;

        // Initialize drive input streams
        this.driveAngularVelocitySlow = createAngularVelocityStream(SLOW_SPEED);
        this.driveAngularVelocityMedium = createAngularVelocityStream(MEDIUM_SPEED);
        this.driveAngularVelocityTurbo = createAngularVelocityStream(TURBO_SPEED);

        this.driveAngularVelocityKeyboard = SwerveInputStream.of(drivebase.getSwerveDrive(),
                () -> -controller.getLeftY(),
                () -> -controller.getLeftX())
                .withControllerRotationAxis(() -> controller.getRawAxis(2))
                .deadband(OperatorConstants.DEADBAND)
                .scaleTranslation(TURBO_SPEED)
                .allianceRelativeControl(true);

        this.driveDirectAngleKeyboard = driveAngularVelocityKeyboard.copy()
                .withControllerHeadingAxis(
                        () -> Math.sin(controller.getRawAxis(2) * Math.PI) * (Math.PI * 2),
                        () -> Math.cos(controller.getRawAxis(2) * Math.PI) * (Math.PI * 2))
                .headingWhile(true);
    }

    /**
     * Creates an angular velocity stream with the specified translation scale.
     */
    private SwerveInputStream createAngularVelocityStream(double translationScale) {
        return SwerveInputStream.of(drivebase.getSwerveDrive(),
                () -> controller.getLeftY() * -1,
                () -> controller.getLeftX() * -1)
                .withControllerRotationAxis(() -> -controller.getRightX())
                .deadband(OperatorConstants.DEADBAND)
                .scaleTranslation(translationScale)
                .allianceRelativeControl(true);
    }

    /**
     * Configures all driver button bindings.
     * Adapts bindings based on simulation vs test vs real robot modes.
     */
    public void configureBindings() {
        // Create drive commands
        Command driveFieldOrientedMedium = drivebase.driveFieldOriented(driveAngularVelocityMedium);
        Command driveFieldOrientedSlow = drivebase.driveFieldOriented(driveAngularVelocitySlow);
        Command driveFieldOrientedTurbo = drivebase.driveFieldOriented(driveAngularVelocityTurbo);
        Command driveFieldOrientedDirectAngleKeyboard = drivebase.driveFieldOriented(driveDirectAngleKeyboard);

        if (Robot.isSimulation()) {
            configureSimulationBindings(driveFieldOrientedDirectAngleKeyboard);
        } else if (DriverStation.isTest()) {
            configureTestBindings(driveFieldOrientedMedium);
        } else {
            configureTeleopBindings(driveFieldOrientedMedium, driveFieldOrientedSlow, driveFieldOrientedTurbo);
        }

        // SysId bindings removed - YAGSL handles swerve feedforward automatically via
        // JSON
    }

    // SysId methods removed - YAGSL handles swerve feedforward via
    // /deploy/swerve/neo/modules/physicalproperties.json

    /**
     * Configures bindings for simulation mode.
     */
    private void configureSimulationBindings(Command driveCommand) {
        drivebase.setDefaultCommand(driveCommand);

        controller.start().onTrue(Commands.runOnce(() -> drivebase.resetOdometry(new Pose2d(3, 3, new Rotation2d()))));
    }

    /**
     * Configures bindings for test mode.
     */
    private void configureTestBindings(Command driveCommand) {
        drivebase.setDefaultCommand(driveCommand);

        controller.x().whileTrue(Commands.runOnce(drivebase::lock, drivebase).repeatedly());
        controller.y().whileTrue(drivebase.driveToDistanceCommand(1.0, 0.2));
        controller.start().onTrue(Commands.runOnce(drivebase::zeroGyro));
        controller.back().whileTrue(drivebase.centerModulesCommand());
        controller.leftBumper().whileTrue(Commands.none());
        controller.rightBumper().whileTrue(Commands.none());
    }

    /**
     * Configures bindings for normal teleop mode.
     */
    private void configureTeleopBindings(Command driveNormal, Command driveSlow, Command driveTurbo) {
        drivebase.setDefaultCommand(driveNormal);

        // Button A: Zero gyro
        controller.a().onTrue(Commands.runOnce(drivebase::zeroGyro));

        // Button X: Lock wheels
        controller.x().whileTrue(Commands.runOnce(drivebase::lock, drivebase).repeatedly());

        // Button B: Drive to pose (demo)
        controller.b().whileTrue(drivebase.driveToPose(
                new Pose2d(new Translation2d(4, 4), Rotation2d.fromDegrees(0))));

        // Bumpers: Speed control
        controller.rightBumper().whileTrue(driveTurbo); // Turbo mode
        controller.leftBumper().whileTrue(driveSlow); // Slow mode

        // D-Pad: Heading lock (hold to lock heading while driving)
        // Allows translation while maintaining cardinal directions
        // Tune PID values via NetworkTables: "Swerve Rotation PID kP/kI/kD"
        // controller.povUp().whileTrue(
        // drivebase.driveWithHeadingLock(0,
        // () -> -controller.getLeftY(),
        // () -> -controller.getLeftX())); // Lock forward
        // controller.povRight().whileTrue(
        // drivebase.driveWithHeadingLock(90,
        // () -> -controller.getLeftY(),
        // () -> -controller.getLeftX())); // Lock left
        // controller.povDown().whileTrue(
        // drivebase.driveWithHeadingLock(180,
        // () -> -controller.getLeftY(),
        // () -> -controller.getLeftX())); // Lock backward
        // controller.povLeft().whileTrue(
        // drivebase.driveWithHeadingLock(270,
        // () -> -controller.getLeftY(),
        // () -> -controller.getLeftX())); // Lock right

        for (int i = 0; i < 360; i += 45) {
            double heading = i;
            if (i == 90) {
                heading = 270;
            } else if (i == 270) {
                heading = 90;
            }
            controller.pov(i).whileTrue(
                    drivebase.driveWithHeadingLock(heading,
                            () -> -controller.getLeftY(),
                            () -> -controller.getLeftX()));
        }

        // Start: Toggle manual/macro control mode
        controller.start().onTrue(stateMachine.toggleControlMode());

        // Back: Cycle drive mode (slow -> medium -> turbo)
        controller.back().onTrue(stateMachine.cycleDriveMode());
    }

    /**
     * Gets the medium speed drive command.
     * 
     * @return Command for medium-speed field-oriented drive
     */
    public Command getMediumDriveCommand() {
        return drivebase.driveFieldOriented(driveAngularVelocityMedium);
    }
}
