package frc.robot.subsystems.elevator;

import static edu.wpi.first.units.Units.Second;
import static edu.wpi.first.units.Units.Volts;

import com.revrobotics.spark.SparkLowLevel;
import com.revrobotics.spark.SparkMax;

import edu.wpi.first.math.Pair;
import edu.wpi.first.math.controller.ElevatorFeedforward;
import edu.wpi.first.math.system.plant.DCMotor;
import edu.wpi.first.units.measure.Distance;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.constants.PortMap;
import frc.robot.constants.subsystems.ElevatorConstants;
import yams.gearing.GearBox;
import yams.gearing.MechanismGearing;
import yams.mechanisms.config.ElevatorConfig;
import yams.mechanisms.config.MechanismPositionConfig;
import yams.mechanisms.positional.Elevator;
import yams.motorcontrollers.SmartMotorController;
import yams.motorcontrollers.SmartMotorControllerConfig;
import yams.motorcontrollers.SmartMotorControllerConfig.ControlMode;
import yams.motorcontrollers.SmartMotorControllerConfig.MotorMode;
import yams.motorcontrollers.SmartMotorControllerConfig.TelemetryVerbosity;
import yams.motorcontrollers.local.SparkWrapper;

public class ElevatorSubsystem extends SubsystemBase {
    private final SparkMax elevatorMotor;
    private final SparkMax elevatorFollower;
    private final SmartMotorControllerConfig motorConfig;
    private final SmartMotorController motor;
    private final MechanismPositionConfig robotToMechanism;
    private final ElevatorConfig elevatorConfig;
    private final Elevator elevator;

    public ElevatorSubsystem(boolean useTrapezoidal) {
        elevatorMotor = new SparkMax(PortMap.CANID.ELEVATOR_DRIVE_LEADER.getId(), SparkLowLevel.MotorType.kBrushless);
        elevatorFollower = new SparkMax(PortMap.CANID.ELEVATOR_DRIVE_FOLLOWER.getId(), SparkLowLevel.MotorType.kBrushless);
        
        motorConfig = new SmartMotorControllerConfig(this)
            .withControlMode(ControlMode.CLOSED_LOOP)
            // Mechanism Circumference converts rotations to meters
            .withMechanismCircumference(ElevatorConstants.MECHANISM_CIRCUMFERENCE)
            // Real robot PID
            .withClosedLoopController(
                ElevatorConstants.kP,
                ElevatorConstants.kI,
                ElevatorConstants.kD,
                ElevatorConstants.MAX_VELOCITY,
                ElevatorConstants.MAX_ACCELERATION
            )
            // Simulation PID (can be different from real)
            .withSimClosedLoopController(
                ElevatorConstants.kP,
                ElevatorConstants.kI,
                ElevatorConstants.kD,
                ElevatorConstants.MAX_VELOCITY,
                ElevatorConstants.MAX_ACCELERATION
            )
            // Real robot feedforward
            .withFeedforward(new ElevatorFeedforward(
                ElevatorConstants.kS,
                ElevatorConstants.kG,
                ElevatorConstants.kV,
                ElevatorConstants.kA
            ))
            // Simulation feedforward
            .withSimFeedforward(new ElevatorFeedforward(
                ElevatorConstants.kS,
                ElevatorConstants.kG,
                ElevatorConstants.kV,
                ElevatorConstants.kA
            ))
            .withTelemetry("ElevatorMotor", TelemetryVerbosity.HIGH)
            // Gearing from the motor rotor to final shaft
            .withGearing(new MechanismGearing(GearBox.fromReductionStages(
                ElevatorConstants.GEAR_RATIO_STAGE_1,
                ElevatorConstants.GEAR_RATIO_STAGE_2
            )))
            .withMotorInverted(false)
            .withIdleMode(MotorMode.COAST)
            .withStatorCurrentLimit(ElevatorConstants.STATOR_CURRENT_LIMIT)
            .withFollowers(
                Pair.of(elevatorFollower, true) // Inverted relative to leader
            );
            
        motor = new SparkWrapper(elevatorMotor, DCMotor.getNEO(2), motorConfig);
        
        robotToMechanism = new MechanismPositionConfig()
            .withMaxRobotHeight(ElevatorConstants.MAX_ROBOT_HEIGHT)
            .withMaxRobotLength(ElevatorConstants.MAX_ROBOT_LENGTH)
            .withRelativePosition(ElevatorConstants.ELEVATOR_POSITION);
            
        elevatorConfig = new ElevatorConfig(motor)
            .withStartingHeight(ElevatorConstants.STARTING_HEIGHT)
            .withHardLimits(ElevatorConstants.MIN_HEIGHT, ElevatorConstants.MAX_HEIGHT)
            .withTelemetry("Elevator", TelemetryVerbosity.HIGH)
            .withMechanismPositionConfig(robotToMechanism)
            .withMass(ElevatorConstants.CARRIAGE_MASS);
            
        elevator = new Elevator(elevatorConfig);
        
        // Publish PID values to SmartDashboard for monitoring
        SmartDashboard.putNumber("Elevator PID kP", ElevatorConstants.kP);
        SmartDashboard.putNumber("Elevator PID kI", ElevatorConstants.kI);
        SmartDashboard.putNumber("Elevator PID kD", ElevatorConstants.kD);
        
        // Publish feedforward values to SmartDashboard for monitoring
        SmartDashboard.putNumber("Elevator FF kS", ElevatorConstants.kS);
        SmartDashboard.putNumber("Elevator FF kG", ElevatorConstants.kG);
        SmartDashboard.putNumber("Elevator FF kV", ElevatorConstants.kV);
        SmartDashboard.putNumber("Elevator FF kA", ElevatorConstants.kA);
    }

    @Override
    public void periodic() {
        // YAMS handles telemetry automatically
        elevator.updateTelemetry();
        
        // Note: YAMS doesn't support runtime PID/FF updates
        // Values must be changed in Constants files and redeployed
        // Telemetry is published automatically by YAMS with TelemetryVerbosity.HIGH
    }
    
    @Override
    public void simulationPeriodic() {
        elevator.simIterate();
    }
    
    public Command elevCmd(double dutycycle) {
        return elevator.set(dutycycle);
    }
    
    public Command setHeight(Distance height) {
        return elevator.setHeight(height);
    }
    
    public Command sysId() {
        return elevator.sysId(
            ElevatorConstants.SYSID_STEP_VOLTAGE,
            Volts.of(ElevatorConstants.SYSID_RAMP_RATE_VALUE).per(Second),
            ElevatorConstants.SYSID_TIMEOUT
        );
    }

    public Elevator getElevator() {
        return elevator;
    }
    
    /**
     * Gets the current elevator height from the bottom position.
     */
    public Distance getCurrentHeight() {
        // Get current height from YAMS elevator mechanism
        return elevator.getHeight();
    }

    public double getMotorCurrent() {
        return elevatorMotor.getOutputCurrent();
    }
}
