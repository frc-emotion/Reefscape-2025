package frc.robot.subsystems.elevator;

import static edu.wpi.first.units.Units.Amps;
import static edu.wpi.first.units.Units.Inches;
import static edu.wpi.first.units.Units.Meters;
import static edu.wpi.first.units.Units.MetersPerSecond;
import static edu.wpi.first.units.Units.MetersPerSecondPerSecond;
import static edu.wpi.first.units.Units.Pounds;
import static edu.wpi.first.units.Units.Second;
import static edu.wpi.first.units.Units.Volts;

import java.util.function.Supplier;

import com.revrobotics.spark.SparkLowLevel;
import com.revrobotics.spark.SparkMax;

import edu.wpi.first.math.Pair;
import edu.wpi.first.math.controller.ElevatorFeedforward;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.math.system.plant.DCMotor;
import edu.wpi.first.units.measure.Distance;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
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
            .withMechanismCircumference(Meters.of(Inches.of(0.25).in(Meters) * 22))
            .withClosedLoopController(
                ElevatorConstants.kP,
                ElevatorConstants.kI,
                ElevatorConstants.kD,
                MetersPerSecond.of(0.5),
                MetersPerSecondPerSecond.of(0.5)
            )
            .withSoftLimit(Meters.of(0), Meters.of(2))
            .withGearing(new MechanismGearing(GearBox.fromReductionStages(3, 4)))
            .withIdleMode(MotorMode.BRAKE)
            .withTelemetry("ElevatorMotor", TelemetryVerbosity.HIGH)
            .withStatorCurrentLimit(Amps.of(40))
            .withMotorInverted(false)
            .withFollowers(
                Pair.of(elevatorFollower, true) // Inverted relative to leader
            )
            .withFeedforward(new ElevatorFeedforward(
                ElevatorConstants.kS,
                ElevatorConstants.kG,
                ElevatorConstants.kV,
                ElevatorConstants.kA
            ))
            .withControlMode(ControlMode.CLOSED_LOOP);
            
        motor = new SparkWrapper(elevatorMotor, DCMotor.getNEO(2), motorConfig);
        
        robotToMechanism = new MechanismPositionConfig()
            .withMaxRobotHeight(Meters.of(1.5))
            .withMaxRobotLength(Meters.of(0.75))
            .withRelativePosition(new Translation3d(Meters.of(-0.25), Meters.of(0), Meters.of(0.5)));
            
        elevatorConfig = new ElevatorConfig(motor)
            .withStartingHeight(Meters.of(0.5))
            .withHardLimits(Meters.of(0), Meters.of(3))
            .withTelemetry("Elevator", TelemetryVerbosity.HIGH)
            .withMechanismPositionConfig(robotToMechanism)
            .withMass(Pounds.of(16));
            
        elevator = new Elevator(elevatorConfig);
    }

    @Override
    public void periodic() {
        elevator.updateTelemetry();
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
        return elevator.sysId(Volts.of(12), Volts.of(12).per(Second), Second.of(30));
    }
}
