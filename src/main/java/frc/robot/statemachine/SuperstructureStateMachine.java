package frc.robot.statemachine;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.subsystems.arm.ArmSubsystem;
import frc.robot.subsystems.elevator.ElevatorSubsystem;
import frc.robot.subsystems.grabber.GrabberSubsystem;
import frc.robot.subsystems.swerve.SwerveSubsystem;

/**
 * Simplified state machine that coordinates robot modes.
 * 
 * YAMS commands handle all mechanism coordination, so this only tracks:
 * - Robot state (READY/CLIMBING/MANUAL)
 * - Game piece detection (auto-updated from sensors)
 * - Control mode (MANUAL/MACRO)
 * - Drive mode (SLOW/MEDIUM/TURBO/LOCKED)
 */
public class SuperstructureStateMachine extends SubsystemBase {
    
    // State tracking
    private RobotState state;
    private boolean hasGamePiece;
    
    // Mode tracking
    private ControlMode controlMode;
    private DriveMode driveMode;
    
    // Subsystem references (for emergency stop and sensors)
    private final ArmSubsystem arm;
    private final ElevatorSubsystem elevator;
    private final GrabberSubsystem grabber;
    
    /**
     * Creates a new SuperstructureStateMachine.
     * 
     * @param arm Arm subsystem
     * @param elevator Elevator subsystem
     * @param grabber Grabber subsystem
     * @param drive Swerve drive subsystem (unused, kept for compatibility)
     */
    public SuperstructureStateMachine(
            ArmSubsystem arm,
            ElevatorSubsystem elevator,
            GrabberSubsystem grabber,
            SwerveSubsystem drive) {
        this.arm = arm;
        this.elevator = elevator;
        this.grabber = grabber;
        
        // Initialize to safe defaults
        this.state = RobotState.READY;
        this.hasGamePiece = false;
        this.controlMode = ControlMode.MACRO;
        this.driveMode = DriveMode.MEDIUM;
    }
    
    @Override
    public void periodic() {
        // Auto-detect game pieces from sensors
        hasGamePiece = grabber.hasGamePiece();
        
        // Update dashboard
        SmartDashboard.putString("Robot State", state.name());
        SmartDashboard.putBoolean("Has Game Piece", hasGamePiece);
        SmartDashboard.putString("Control Mode", controlMode.toString());
        SmartDashboard.putString("Drive Mode", driveMode.toString());
    }
    
    // ========== SIMPLE QUERIES ==========
    
    /**
     * Gets the current robot state.
     * @return Current state (READY/CLIMBING/MANUAL)
     */
    public RobotState getMechanismState() {
        return state;
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
     * Checks if the robot has a game piece.
     * @return True if holding a game piece
     */
    public boolean hasGamePiece() {
        return hasGamePiece;
    }
    
    /**
     * Checks if the robot is climbing.
     * @return True if in climbing mode
     */
    public boolean isClimbing() {
        return state == RobotState.CLIMBING;
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
    
    // ========== CLIMBING COMMANDS ==========
    
    /**
     * Starts climbing mode - locks out all other operations.
     * @return Command to enter climbing mode
     */
    public Command startClimbing() {
        return Commands.runOnce(() -> {
            state = RobotState.CLIMBING;
            System.out.println("CLIMBING MODE ENABLED");
        });
    }
    
    /**
     * Finishes climbing and returns to normal operation.
     * @return Command to exit climbing mode
     */
    public Command finishClimbing() {
        return Commands.runOnce(() -> {
            state = RobotState.READY;
            System.out.println("Climbing complete - READY");
        });
    }
    
    // ========== CONTROL MODE ==========
    
    /**
     * Enables manual control mode.
     * Operator has direct joystick control, state machine monitors only.
     * @return Command to enable manual mode
     */
    public Command enableManualMode() {
        return Commands.runOnce(() -> {
            if (state.allowsManualOverride()) {
                controlMode = ControlMode.MANUAL;
                state = RobotState.MANUAL;
                System.out.println("MANUAL CONTROL MODE");
            } else {
                System.err.println("Cannot enable manual mode while climbing");
            }
        });
    }
    
    /**
     * Enables macro control mode.
     * Automated sequences control mechanisms.
     * @return Command to enable macro mode
     */
    public Command enableMacroMode() {
        return Commands.runOnce(() -> {
            controlMode = ControlMode.MACRO;
            if (state == RobotState.MANUAL) {
                state = RobotState.READY;
            }
            System.out.println("MACRO CONTROL MODE");
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
     * Cycles to the next drive mode (SLOW -> MEDIUM -> TURBO -> SLOW).
     * @return Command to cycle drive mode
     */
    public Command cycleDriveMode() {
        return Commands.runOnce(() -> {
            driveMode = driveMode.next();
            System.out.println("Drive mode: " + driveMode.getDescription());
        });
    }
    
    // ========== EMERGENCY STOP ==========
    
    /**
     * Emergency stop - immediately stops all mechanisms and returns to ready state.
     * @return Command for emergency stop
     */
    public Command emergencyStop() {
        return Commands.runOnce(() -> {
            // Stop all mechanisms
            arm.armCmd(0).schedule();
            elevator.elevCmd(0).schedule();
            grabber.stop();
            
            // Reset state
            state = RobotState.READY;
            hasGamePiece = false;
            
            System.err.println("!!! EMERGENCY STOP ACTIVATED !!!");
        });
    }
}
