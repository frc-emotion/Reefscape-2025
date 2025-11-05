package frc.robot.statemachine;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.game.Task;
import frc.robot.game.tasks.PickupTask;
import frc.robot.subsystems.arm.ArmSubsystem;
import frc.robot.subsystems.climb.ClimbSubsystem;
import frc.robot.subsystems.elevator.ElevatorSubsystem;
import frc.robot.subsystems.grabber.GrabberSubsystem;
import frc.robot.subsystems.swerve.SwerveSubsystem;

/**
 * Central state machine that coordinates all robot subsystems.
 * Manages mechanism states, drive modes, and control modes.
 * 
 * Key responsibilities:
 * - Track current robot state
 * - Validate state transitions
 * - Provide state-aware commands
 * - Handle manual/macro mode switching
 * - Manage drive speed modes
 * - Ensure safe multi-subsystem coordination
 */
public class SuperstructureStateMachine extends SubsystemBase {
    
    // Current states
    private RobotState mechanismState;
    private DriveMode driveMode;
    private ControlMode controlMode;
    
    // Subsystem references
    private final ArmSubsystem arm;
    private final ElevatorSubsystem elevator;
    private final GrabberSubsystem grabber;
    private final ClimbSubsystem climb;
    private final SwerveSubsystem drive;
    
    // State tracking
    private boolean hasGamePiece;
    private Task currentTask;
    
    /**
     * Creates a new SuperstructureStateMachine.
     * 
     * @param arm Arm subsystem
     * @param elevator Elevator subsystem
     * @param grabber Grabber subsystem
     * @param climb Climb subsystem
     * @param drive Swerve drive subsystem
     */
    public SuperstructureStateMachine(
            ArmSubsystem arm,
            ElevatorSubsystem elevator,
            GrabberSubsystem grabber,
            ClimbSubsystem climb,
            SwerveSubsystem drive) {
        this.arm = arm;
        this.elevator = elevator;
        this.grabber = grabber;
        this.climb = climb;
        this.drive = drive;
        
        // Initialize to safe defaults
        this.mechanismState = RobotState.IDLE;
        this.driveMode = DriveMode.MEDIUM;
        this.controlMode = ControlMode.MACRO;
        this.hasGamePiece = false;
        this.currentTask = null;
    }
    
    @Override
    public void periodic() {
        // Update dashboard
        updateDashboard();
        
        // Update game piece detection
        updateGamePieceStatus();
        
        // Auto-transition from certain states
        handleAutoTransitions();
    }
    
    // ========== STATE QUERIES ==========
    
    /**
     * Gets the current mechanism state.
     * @return Current robot state
     */
    public RobotState getMechanismState() {
        return mechanismState;
    }
    
    /**
     * Gets the current drive mode.
     * @return Current drive mode
     */
    public DriveMode getDriveMode() {
        return driveMode;
    }
    
    /**
     * Gets the current control mode.
     * @return Current control mode
     */
    public ControlMode getControlMode() {
        return controlMode;
    }
    
    /**
     * Checks if the robot is in manual control mode.
     * @return True if in manual mode
     */
    public boolean isInManualMode() {
        return controlMode == ControlMode.MANUAL;
    }
    
    /**
     * Checks if the robot is in macro control mode.
     * @return True if in macro mode
     */
    public boolean isInMacroMode() {
        return controlMode == ControlMode.MACRO;
    }
    
    /**
     * Checks if the robot has a game piece.
     * @return True if holding a game piece
     */
    public boolean hasGamePiece() {
        return hasGamePiece;
    }
    
    /**
     * Checks if all mechanisms are at their target positions.
     * @return True if at setpoint
     */
    public boolean isAtTargetPosition() {
        return arm.isAtSetpoint() && elevator.isAtSetpoint();
    }
    
    /**
     * Checks if it's safe to move mechanisms.
     * @return True if safe to command mechanism movement
     */
    public boolean isSafeToMove() {
        // Don't allow mechanism movement during climb
        if (mechanismState == RobotState.CLIMBING || mechanismState == RobotState.CLIMBING_PREP) {
            return false;
        }
        return true;
    }
    
    // ========== STATE TRANSITIONS ==========
    
    /**
     * Validates if a transition to a new state is allowed.
     * @param newState Target state
     * @return True if transition is valid
     */
    public boolean canTransitionTo(RobotState newState) {
        // Manual override can always be enabled (except during climb)
        if (newState == RobotState.MANUAL_OVERRIDE) {
            return mechanismState.allowsManualOverride();
        }
        
        // Can't transition from manual override in macro mode
        if (mechanismState == RobotState.MANUAL_OVERRIDE && controlMode == ControlMode.MACRO) {
            return false;
        }
        
        // Climbing states have restricted transitions
        if (mechanismState == RobotState.CLIMBING) {
            return newState == RobotState.IDLE || newState == RobotState.CLIMBING;
        }
        
        // Can't score without a game piece
        if (newState == RobotState.POSITIONING || newState == RobotState.SCORING) {
            return hasGamePiece;
        }
        
        return true;
    }
    
    /**
     * Transitions to a new mechanism state.
     * @param newState Target state
     * @return True if transition succeeded
     */
    private boolean transitionState(RobotState newState) {
        if (!canTransitionTo(newState)) {
            System.err.println("Invalid state transition: " + mechanismState + " -> " + newState);
            return false;
        }
        
        System.out.println("State transition: " + mechanismState + " -> " + newState);
        mechanismState = newState;
        return true;
    }
    
    /**
     * Creates a command to transition to idle state.
     * @return Command that transitions to idle
     */
    public Command transitionToIdle() {
        return Commands.runOnce(() -> transitionState(RobotState.IDLE));
    }
    
    /**
     * Creates a command to transition to stowed state.
     * @return Command that stows the robot
     */
    public Command transitionToStowed() {
        return Commands.runOnce(() -> {
            transitionState(RobotState.STOWED);
            currentTask = null;
        });
    }
    
    /**
     * Creates a command to transition to intaking state.
     * @param pickupTask The pickup task to execute
     * @return Command that prepares for intake
     */
    public Command transitionToIntaking(PickupTask pickupTask) {
        return Commands.runOnce(() -> {
            if (transitionState(RobotState.INTAKING)) {
                currentTask = pickupTask;
                hasGamePiece = false;
            }
        });
    }
    
    /**
     * Creates a command to transition to holding state.
     * @return Command that marks game piece as acquired
     */
    public Command transitionToHolding() {
        return Commands.runOnce(() -> {
            transitionState(RobotState.HOLDING);
            hasGamePiece = true;
        });
    }
    
    /**
     * Creates a command to transition to positioning state.
     * @param scoreTask The scoring task to execute
     * @return Command that prepares for scoring
     */
    public Command transitionToPositioning(Task scoreTask) {
        return Commands.runOnce(() -> {
            if (transitionState(RobotState.POSITIONING)) {
                currentTask = scoreTask;
            }
        });
    }
    
    /**
     * Creates a command to transition to scoring state.
     * @return Command that marks robot as actively scoring
     */
    public Command transitionToScoring() {
        return Commands.runOnce(() -> {
            transitionState(RobotState.SCORING);
        });
    }
    
    /**
     * Creates a command to mark scoring as complete.
     * @return Command that returns to idle after scoring
     */
    public Command transitionScoringComplete() {
        return Commands.runOnce(() -> {
            hasGamePiece = false;
            currentTask = null;
            transitionState(RobotState.IDLE);
        });
    }
    
    /**
     * Creates a command to transition to climb prep state.
     * @return Command that prepares for climbing
     */
    public Command transitionToClimbPrep() {
        return Commands.runOnce(() -> transitionState(RobotState.CLIMBING_PREP));
    }
    
    /**
     * Creates a command to transition to climbing state.
     * @return Command that marks robot as climbing
     */
    public Command transitionToClimbing() {
        return Commands.runOnce(() -> transitionState(RobotState.CLIMBING));
    }
    
    // ========== CONTROL MODE ==========
    
    /**
     * Enables manual control mode.
     * Mechanisms controlled directly by joysticks, state machine monitors only.
     * @return Command to enable manual mode
     */
    public Command enableManualMode() {
        return Commands.runOnce(() -> {
            if (mechanismState.allowsManualOverride()) {
                controlMode = ControlMode.MANUAL;
                mechanismState = RobotState.MANUAL_OVERRIDE;
                System.out.println("Manual control mode ENABLED");
            } else {
                System.err.println("Cannot enable manual mode from state: " + mechanismState);
            }
        });
    }
    
    /**
     * Enables macro control mode.
     * State machine controls mechanisms through automated sequences.
     * @return Command to enable macro mode
     */
    public Command enableMacroMode() {
        return Commands.runOnce(() -> {
            controlMode = ControlMode.MACRO;
            if (mechanismState == RobotState.MANUAL_OVERRIDE) {
                mechanismState = RobotState.IDLE;
            }
            System.out.println("Macro control mode ENABLED");
        });
    }
    
    /**
     * Toggles between manual and macro control modes.
     * @return Command to toggle control mode
     */
    public Command toggleControlMode() {
        return Commands.either(
            enableMacroMode(),
            enableManualMode(),
            this::isInManualMode
        );
    }
    
    // ========== DRIVE MODE ==========
    
    /**
     * Sets the drive speed mode.
     * @param mode Target drive mode
     * @return Command to set drive mode
     */
    public Command setDriveMode(DriveMode mode) {
        return Commands.runOnce(() -> {
            driveMode = mode;
            System.out.println("Drive mode: " + mode.getDescription());
        });
    }
    
    /**
     * Cycles to the next drive mode.
     * @return Command to cycle drive mode
     */
    public Command cycleDriveMode() {
        return Commands.runOnce(() -> {
            driveMode = driveMode.next();
            System.out.println("Drive mode: " + driveMode.getDescription());
        });
    }
    
    // ========== SAFETY & HELPERS ==========
    
    /**
     * Emergency stop - immediately stops all mechanisms and returns to idle.
     * @return Command for emergency stop
     */
    public Command emergencyStop() {
        return Commands.runOnce(() -> {
            arm.stop();
            elevator.stop();
            grabber.stop();
            climb.stop();
            mechanismState = RobotState.IDLE;
            hasGamePiece = false;
            currentTask = null;
            System.err.println("EMERGENCY STOP ACTIVATED");
        });
    }
    
    /**
     * Gets the current task being executed.
     * @return Current task, or null if none
     */
    public Task getCurrentTask() {
        return currentTask;
    }
    
    /**
     * Updates game piece detection status.
     */
    private void updateGamePieceStatus() {
        // Update based on grabber sensors
        boolean detected = grabber.hasGamePiece();
        
        // Auto-transition to HOLDING if we detect a game piece while intaking
        if (mechanismState == RobotState.INTAKING && detected && !hasGamePiece) {
            transitionToHolding();
        }
        
        // Update status
        hasGamePiece = detected;
    }
    
    /**
     * Handles automatic state transitions based on conditions.
     */
    private void handleAutoTransitions() {
        // Auto-transition from POSITIONING to IDLE when at target
        if (mechanismState == RobotState.POSITIONING && isAtTargetPosition()) {
            // Don't auto-transition, let scoring command handle it
        }
        
        // Auto-transition from SCORING back to IDLE when complete
        if (mechanismState == RobotState.SCORING && !hasGamePiece) {
            transitionState(RobotState.IDLE);
        }
    }
    
    /**
     * Updates dashboard with current state information.
     */
    private void updateDashboard() {
        SmartDashboard.putString("Robot State", mechanismState.toString());
        SmartDashboard.putString("Drive Mode", driveMode.toString());
        SmartDashboard.putString("Control Mode", controlMode.toString());
        SmartDashboard.putBoolean("Has Game Piece", hasGamePiece);
        SmartDashboard.putBoolean("At Target Position", isAtTargetPosition());
        SmartDashboard.putString("Current Task", currentTask != null ? currentTask.getClass().getSimpleName() : "None");
    }
}
