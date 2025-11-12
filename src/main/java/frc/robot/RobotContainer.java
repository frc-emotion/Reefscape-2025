package frc.robot;

import java.io.File;

import com.pathplanner.lib.auto.NamedCommands;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.RobotModeTriggers;
import frc.robot.commands.functional.MainCommandFactory;
import frc.robot.commands.teleop.Grabber.GrabberGrabCommand;
import frc.robot.constants.subsystems.ArmConstants;
import frc.robot.constants.subsystems.ElevatorConstants;
import frc.robot.controls.DriverControls;
import frc.robot.controls.OperatorControls;
import frc.robot.statemachine.SuperstructureStateMachine;
import frc.robot.subsystems.arm.ArmSubsystem;
import frc.robot.subsystems.elevator.ElevatorSubsystem;
import frc.robot.subsystems.grabber.GrabberSubsystem;
import frc.robot.subsystems.grabber.GrabberSubsystem.GrabType;
import frc.robot.subsystems.swerve.SwerveSubsystem;
import frc.robot.util.ui.TabManager;
import frc.robot.util.ui.TabManager.SubsystemTab;

/**
 * RobotContainer organizes the robot's subsystems and configures button bindings.
 * Controller bindings are delegated to DriverControls and OperatorControls classes.
 */
public class RobotContainer {
    
    // Controllers
    private final CommandXboxController driverController = new CommandXboxController(0);
    private final CommandXboxController operatorController = new CommandXboxController(1);
    
    // Subsystems
    private final SwerveSubsystem drivebase;
    private final ElevatorSubsystem elevatorSubsystem;
    private final GrabberSubsystem grabberSubsystem;
    private final ArmSubsystem armSubsystem;
    
    // State machine coordinator
    private final SuperstructureStateMachine stateMachine;
    
    // Control managers
    private final DriverControls driverControls;
    private final OperatorControls operatorControls;
    
    // Auto chooser
    private final SendableChooser<Command> autoChooser;
    
    /**
     * Creates the robot container and initializes all subsystems and controls.
     */
    public RobotContainer() {
        // Initialize subsystems
        drivebase = new SwerveSubsystem(new File(Filesystem.getDeployDirectory(), "swerve/neo"));
        elevatorSubsystem = new ElevatorSubsystem(true);
        grabberSubsystem = new GrabberSubsystem();
        armSubsystem = new ArmSubsystem();
        
        // Initialize state machine coordinator
        stateMachine = new SuperstructureStateMachine(
                armSubsystem,
                elevatorSubsystem,
                grabberSubsystem,
                drivebase);
        
        // Initialize control managers with state machine
        driverControls = new DriverControls(driverController, drivebase, stateMachine);
        operatorControls = new OperatorControls(
                operatorController,
                stateMachine,
                armSubsystem,
                elevatorSubsystem,
                grabberSubsystem);
        
        // Configure everything
        autoChooser = new SendableChooser<>();
        configureRobotModeBindings();
        driverControls.configureBindings();
        operatorControls.configureBindings();
        initializeNamedCommands();
        configureUI();
        
        // Silence joystick warnings
        DriverStation.silenceJoystickConnectionWarning(true);
    }
    
    /**
     * Configures bindings that trigger on robot mode changes (auto/teleop).
     */
    private void configureRobotModeBindings() {
        RobotModeTriggers.autonomous().onTrue(
                Commands.runOnce(drivebase::zeroGyroWithAlliance));
        RobotModeTriggers.teleop().onTrue(
                Commands.runOnce(drivebase::zeroGyroWithAlliance));
    }
    
    /**
     * Registers named commands for use in PathPlanner autonomous routines.
     */
    public void initializeNamedCommands() {
        NamedCommands.registerCommand(
                "DropCoral",
                new SequentialCommandGroup(
                        Commands.runOnce(() -> grabberSubsystem.setTargetType(GrabType.CORAL)),
                        new GrabberGrabCommand(grabberSubsystem)));
        
        NamedCommands.registerCommand(
                "PrepL4",
                MainCommandFactory.getAutoArmElevatorPositionCommand(
                        armSubsystem,
                        elevatorSubsystem,
                        ElevatorConstants.CORAL_L4_HEIGHT,
                        ArmConstants.CORAL_L4_ANGLE));
        
        NamedCommands.registerCommand(
                "PrepL3",
                MainCommandFactory.getAutoArmElevatorPositionCommand(
                        armSubsystem,
                        elevatorSubsystem,
                        ElevatorConstants.CORAL_L3_HEIGHT,
                        ArmConstants.CORAL_L3_ANGLE));
        
        NamedCommands.registerCommand(
                "PrepL2",
                MainCommandFactory.getAutoArmElevatorPositionCommand(
                        armSubsystem,
                        elevatorSubsystem,
                        ElevatorConstants.CORAL_L2_HEIGHT,
                        ArmConstants.CORAL_L2_ANGLE));
        
        NamedCommands.registerCommand(
                "ResetIntake",
                MainCommandFactory.getAutoArmElevatorPositionCommand(
                        armSubsystem,
                        elevatorSubsystem,
                        ElevatorConstants.CORAL_INTAKE_HEIGHT,
                        ArmConstants.CORAL_INTAKE_ANGLE));
        
        NamedCommands.registerCommand(
                "MaintainL2",
                MainCommandFactory.getArmElevatorPositionCommand(
                        armSubsystem,
                        elevatorSubsystem,
                        ElevatorConstants.CORAL_L2_HEIGHT,
                        ArmConstants.CORAL_L2_ANGLE));
        
        NamedCommands.registerCommand(
                "MaintainL3",
                MainCommandFactory.getArmElevatorPositionCommand(
                        armSubsystem,
                        elevatorSubsystem,
                        ElevatorConstants.CORAL_L3_HEIGHT,
                        ArmConstants.CORAL_L3_ANGLE));
        
        NamedCommands.registerCommand(
                "MaintainL4",
                MainCommandFactory.getArmElevatorPositionCommand(
                        armSubsystem,
                        elevatorSubsystem,
                        ElevatorConstants.CORAL_L4_HEIGHT,
                        ArmConstants.CORAL_L4_ANGLE));
    }
    
    /**
     * Configures the autonomous chooser and dashboard UI.
     */
    private void configureUI() {
        // Set up autonomous chooser
        autoChooser.setDefaultOption("Taxi Auto", getAutonomousCommand("Straight Test"));
        addAutoOption("S4-B Score");
        addAutoOption("S2-G Score");
        addAutoOption("meter test");
        addAutoOption("persian u");
        addAutoOption("neev");
        addAutoOption("neev-L3");
        addAutoOption("neev-L4");
        
        // Add to dashboard
        TabManager.getInstance().accessTab(SubsystemTab.AUTO).add(autoChooser);
    }
    
    /**
     * Adds an autonomous option to the chooser.
     * @param name Name of the autonomous routine
     */
    private void addAutoOption(String name) {
        autoChooser.addOption(name, drivebase.getAutonomousCommand(name));
    }
    
    /**
     * Gets an autonomous command by name.
     * @param name Name of the autonomous routine
     * @return The autonomous command
     */
    private Command getAutonomousCommand(String name) {
        return drivebase.getAutonomousCommand(name);
    }
    
    /**
     * Returns the autonomous command selected in the chooser.
     * @return The selected autonomous command
     */
    public Command getAutonomousCommand() {
        return autoChooser.getSelected();
    }
    
    /**
     * Sets the motor brake mode for the drivetrain.
     * @param brake True to enable brake mode, false for coast mode
     */
    public void setMotorBrake(boolean brake) {
        drivebase.setMotorBrake(brake);
    }
}
