package frc.robot.subsystems.elevator;

import static edu.wpi.first.units.Units.Inches;
import static edu.wpi.first.units.Units.Meters;
import static edu.wpi.first.units.Units.MetersPerSecond;
import static edu.wpi.first.units.Units.MetersPerSecondPerSecond;
import static edu.wpi.first.units.Units.Amps;
import static edu.wpi.first.units.Units.Pounds;
import static edu.wpi.first.units.Units.Seconds;

import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;

import edu.wpi.first.epilogue.Logged;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.ElevatorFeedforward;
import edu.wpi.first.math.system.plant.DCMotor;
import edu.wpi.first.units.Units;
import edu.wpi.first.units.measure.Distance;
import edu.wpi.first.units.measure.LinearVelocity;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.constants.PortMap;
import frc.robot.constants.subsystems.ElevatorConstants;
import frc.robot.util.diagnostics.Faults.FaultManager;
import frc.robot.util.diagnostics.Faults.FaultTypes.FaultType;
import yams.gearing.GearBox;
import yams.gearing.MechanismGearing;
import yams.mechanisms.positional.Elevator;
import yams.mechanisms.config.ElevatorConfig;
import yams.motorcontrollers.SmartMotorController;
import yams.motorcontrollers.SmartMotorControllerConfig;
import yams.motorcontrollers.SmartMotorControllerConfig.ControlMode;
import yams.motorcontrollers.SmartMotorControllerConfig.MotorMode;
import yams.motorcontrollers.SmartMotorControllerConfig.TelemetryVerbosity;
import yams.motorcontrollers.local.SparkWrapper;

@Logged
public class ElevatorSubsystem extends SubsystemBase {
    private final SparkMax driveMotor, driveMotor2;
    private final SmartMotorController motor;
    private final Elevator elevator;
    
    private Distance targetHeight = ElevatorConstants.kStartingHeight;

    public ElevatorSubsystem(boolean useTrapezoidal) {
        // Create raw motors
        driveMotor = new SparkMax(PortMap.CANID.ELEVATOR_DRIVE_LEADER.getId(), MotorType.kBrushless);
        driveMotor2 = new SparkMax(PortMap.CANID.ELEVATOR_DRIVE_FOLLOWER.getId(), MotorType.kBrushless);

        // Configure YAMS SmartMotorController
        motor = new SparkWrapper(
            driveMotor,
            DCMotor.getNEO(2), // 2 NEO motors
            new SmartMotorControllerConfig(this)
                .withControlMode(ControlMode.CLOSED_LOOP)
                .withClosedLoopController(
                    ElevatorConstants.kP,
                    ElevatorConstants.kI,
                    ElevatorConstants.kD,
                    ElevatorConstants.kMaxVelocity,
                    ElevatorConstants.kMaxAcceleration
                )
                .withFeedforward(new ElevatorFeedforward(
                    ElevatorConstants.kS,
                    ElevatorConstants.kG,
                    ElevatorConstants.kV,
                    ElevatorConstants.kA
                ))
                .withGearing(ElevatorConstants.kGearing)
                .withSoftLimit(
                    Meters.of(ElevatorConstants.kMinHeight.in(Inches) / 12.0),
                    Meters.of(ElevatorConstants.kMaxHeight.in(Inches) / 12.0)
                )
                .withIdleMode(MotorMode.BRAKE)
                .withTelemetry("Elevator", TelemetryVerbosity.HIGH)
                .withStatorCurrentLimit(Amps.of(ElevatorConstants.kSmartCurrentLimit))
                .withClosedLoopRampRate(Seconds.of(0.25))
                .withOpenLoopRampRate(Seconds.of(0.25))
                .withFollower(driveMotor2, false) // Add follower motor
        );
        
        // Create YAMS Elevator mechanism
        elevator = new Elevator(
            new ElevatorConfig(motor)
                .withCarriageMass(ElevatorConstants.kCarriageMass)
                .withDrumRadius(Meters.of(ElevatorConstants.kDrumRadius.in(Inches) / 12.0))
                .withHardLimit(
                    Meters.of(ElevatorConstants.kMinHeight.in(Inches) / 12.0),
                    Meters.of(ElevatorConstants.kMaxHeight.in(Inches) / 12.0)
                )
                .withStartingPosition(Meters.of(ElevatorConstants.kStartingHeight.in(Inches) / 12.0))
                .withTelemetry("ElevatorMechanism", TelemetryVerbosity.HIGH)
        );

        FaultManager.register(driveMotor);
        FaultManager.register(driveMotor2);
        safetyChecks();
    }

    private void safetyChecks() {
        FaultManager.register(
                () -> driveMotor.getOutputCurrent() > ElevatorConstants.NORMAL_OPERATION_CURRENT
                        + ElevatorConstants.CURRENT_SPIKE_THRESHOLD,
                "Elevator Drive 1",
                "Possible unsafe current spike",
                FaultType.WARNING);

        FaultManager.register(
                () -> driveMotor2.getOutputCurrent() > ElevatorConstants.NORMAL_OPERATION_CURRENT
                        + ElevatorConstants.CURRENT_SPIKE_THRESHOLD,
                "Elevator Drive 2",
                "Possible unsafe current spike",
                FaultType.WARNING);

        FaultManager.register(
                () -> driveMotor.getMotorTemperature() > ElevatorConstants.NORMAL_OPERATION_TEMP
                        + ElevatorConstants.TEMP_SPIKE_THRESHOLD,
                "Elevator Drive 1",
                "Possible unsafe motor temperature",
                FaultType.WARNING);

        FaultManager.register(
                () -> driveMotor2.getMotorTemperature() > ElevatorConstants.NORMAL_OPERATION_TEMP
                        + ElevatorConstants.TEMP_SPIKE_THRESHOLD,
                "Elevator Drive 2",
                "Possible unsafe motor temperature",
                FaultType.WARNING);
    }

    @Override
    public void periodic() {
        // YAMS handles telemetry automatically
        elevator.updateTelemetry();
    }
    
    @Override
    public void simulationPeriodic() {
        // YAMS handles physics simulation
        elevator.simIterate();
    }

    public void setVoltage(double volts) {
        motor.setVoltage(volts);
    }

    /**
     * Gets the YAMS SysId routine for characterization
     */
    public Command getSysIdRoutine() {
        return elevator.sysId();
    }

    public double getTemperature(boolean motor1) {
        if (motor1)
            return driveMotor.getMotorTemperature();

        return driveMotor2.getMotorTemperature();
    }

    public double getCurrentDraw(boolean motor1) {
        if (motor1)
            return driveMotor.getOutputCurrent();

        return driveMotor2.getOutputCurrent();
    }

    public double getVoltage(boolean motor1) {
        if (motor1)
            return driveMotor.get() * RobotController.getBatteryVoltage();

        return driveMotor2.get() * RobotController.getBatteryVoltage();
    }

    public void set(double speed) {
        motor.set(speed);
    }

    public void setWithFeedforward(double speed) {
        // YAMS handles feedforward automatically
        motor.setVoltage(speed * 12.0);
    }

    public void stop() {
        motor.set(0);
    }

    public void setTargetHeight(Distance height) {
        targetHeight = height;
        elevator.setHeight(Meters.of(height.in(Inches) / 12.0)).schedule();
    }

    public boolean isAtSetpoint() {
        return elevator.isNear(
            Meters.of(targetHeight.in(Inches) / 12.0), 
            Meters.of(ElevatorConstants.TOLERABLE_ERROR / 12.0)
        );
    }

    public Trigger atHeight(double height, double tolerance) {
        return new Trigger(() -> MathUtil.isNear(height,
                getHeight().in(Inches),
                tolerance));
    }

    public Distance getHeight() {
        return Inches.of(motor.getPosition().in(Meters) * 12.0); // Convert meters back to inches
    }

    public Distance getTargetHeight() {
        return targetHeight;
    }

    public LinearVelocity getVelocity() {
        return motor.getVelocity();
    }

    public void resetSensorPosition(Distance setpoint) {
        // YAMS handles encoder position
        motor.setPosition(Meters.of(setpoint.in(Inches) / 12.0));
    }
    
    /**
     * Returns a command to set the elevator to a specific height
     */
    public Command setHeight(Distance height) {
        return elevator.setHeight(Meters.of(height.in(Inches) / 12.0));
    }

}
