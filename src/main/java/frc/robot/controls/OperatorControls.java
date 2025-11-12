package frc.robot.controls;

import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.commands.macros.scoring.ScoreCoralSequence;
import frc.robot.commands.macros.scoring.ScoreAlgaeSequence;
import frc.robot.commands.manual.ArmManualCommand;
import frc.robot.commands.manual.ElevatorManualCommand;
import frc.robot.commands.atomic.grabber.IntakeGamePiece;
import frc.robot.commands.atomic.grabber.EjectGamePiece;
import frc.robot.constants.OperatorConstants;
import frc.robot.game.GameElement.CoralLevel;
import frc.robot.game.field.AlgaeScorePosition;
import frc.robot.statemachine.SuperstructureStateMachine;
import frc.robot.subsystems.arm.ArmSubsystem;
import frc.robot.subsystems.elevator.ElevatorSubsystem;
import frc.robot.subsystems.grabber.GrabberSubsystem;
import frc.robot.subsystems.grabber.GrabberSubsystem.GrabType;

/**
 * Manages operator Xbox controller bindings for manipulator subsystems.
 * Controls arm, elevator, and grabber mechanisms.
 */
public class OperatorControls {
    
    private final CommandXboxController controller;
    private final SuperstructureStateMachine stateMachine;
    private final ArmSubsystem armSubsystem;
    private final ElevatorSubsystem elevatorSubsystem;
    private final GrabberSubsystem grabberSubsystem;
    
    // Arm manual control scaling
    private static final double ARM_MANUAL_SCALE = 0.58;
    
    /**
     * Creates operator controls for all manipulator subsystems.
     * 
     * @param controller The operator's Xbox controller
     * @param stateMachine The state machine coordinator
     * @param armSubsystem The arm subsystem
     * @param elevatorSubsystem The elevator subsystem
     * @param grabberSubsystem The grabber subsystem
     */
    public OperatorControls(
            CommandXboxController controller,
            SuperstructureStateMachine stateMachine,
            ArmSubsystem armSubsystem,
            ElevatorSubsystem elevatorSubsystem,
            GrabberSubsystem grabberSubsystem) {
        this.controller = controller;
        this.stateMachine = stateMachine;
        this.armSubsystem = armSubsystem;
        this.elevatorSubsystem = elevatorSubsystem;
        this.grabberSubsystem = grabberSubsystem;
    }
    
    /**
     * Configures all operator button bindings.
     */
    public void configureBindings() {
        configureDefaultCommands();
        configureCoralScoringButtons();
        configureAlgaeScoringButtons();
        configureGrabberButtons();
    }
    
    /**
     * Sets up default commands for arm and elevator manual control.
     */
    private void configureDefaultCommands() {
        // Arm manual control with squared inputs for finer control
        armSubsystem.setDefaultCommand(
                new ArmManualCommand(armSubsystem, () -> {
                    double raw = -controller.getLeftY() * ARM_MANUAL_SCALE;
                    double sign = Math.signum(raw);
                    double squared = Math.pow(raw, 2);
                    return sign * squared;
                }));
        
        // Elevator manual control
        elevatorSubsystem.setDefaultCommand(
                new ElevatorManualCommand(elevatorSubsystem, () -> -controller.getRightY()));
    }
    
    /**
     * Configures D-Pad buttons for coral scoring positions.
     * Uses new macro sequences for coordinated scoring.
     */
    private void configureCoralScoringButtons() {
        // D-Pad Up: Level 4 (highest)
        controller.povUp().whileTrue(
                new ScoreCoralSequence(
                        stateMachine,
                        armSubsystem,
                        elevatorSubsystem,
                        grabberSubsystem,
                        CoralLevel.L4));
        
        // D-Pad Right: Level 3
        controller.povRight().whileTrue(
                new ScoreCoralSequence(
                        stateMachine,
                        armSubsystem,
                        elevatorSubsystem,
                        grabberSubsystem,
                        CoralLevel.L3));
        
        // D-Pad Left: Level 2
        controller.povLeft().whileTrue(
                new ScoreCoralSequence(
                        stateMachine,
                        armSubsystem,
                        elevatorSubsystem,
                        grabberSubsystem,
                        CoralLevel.L2));
        
        // D-Pad Down: Level 1 / Ground position
        controller.povDown().whileTrue(
                new ScoreCoralSequence(
                        stateMachine,
                        armSubsystem,
                        elevatorSubsystem,
                        grabberSubsystem,
                        CoralLevel.L1));
    }
    
    /**
     * Configures X and Y buttons for algae scoring positions.
     * Uses new macro sequences for coordinated scoring.
     */
    private void configureAlgaeScoringButtons() {
        // Button X: Algae Processor
        controller.x().whileTrue(
                new ScoreAlgaeSequence(
                        stateMachine,
                        armSubsystem,
                        elevatorSubsystem,
                        grabberSubsystem,
                        AlgaeScorePosition.P));
        
        // Button Y: Algae Reef
        controller.y().whileTrue(
                new ScoreAlgaeSequence(
                        stateMachine,
                        armSubsystem,
                        elevatorSubsystem,
                        grabberSubsystem,
                        AlgaeScorePosition.R1));
    }
    
    /**
     * Configures bumpers and triggers for grabber control.
     */
    private void configureGrabberButtons() {
        // Right bumper: Set target type to Coral
        controller.rightBumper().whileTrue(
                Commands.runOnce(() -> grabberSubsystem.setTargetType(GrabType.CORAL)));
        
        // Left bumper: Set target type to Algae
        controller.leftBumper().whileTrue(
                Commands.runOnce(() -> grabberSubsystem.setTargetType(GrabType.ALGAE)));
        
        // Right trigger: Place/outtake game piece
        controller.rightTrigger(OperatorConstants.DEADBAND).whileTrue(
                new EjectGamePiece(grabberSubsystem));
        
        // Left trigger: Grab/intake game piece
        controller.leftTrigger(OperatorConstants.DEADBAND).whileTrue(
                new IntakeGamePiece(grabberSubsystem));
    }
}
