package frc.robot.subsystems.elevator;

import static edu.wpi.first.units.Units.Inches;
import static edu.wpi.first.units.Units.MetersPerSecond;

import com.revrobotics.RelativeEncoder;
import com.revrobotics.spark.ClosedLoopSlot;
import com.revrobotics.spark.SparkBase.ControlType;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.ClosedLoopSlot;
import com.revrobotics.spark.SparkClosedLoopController;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.config.SparkFlexConfig;
import com.revrobotics.spark.config.SparkMaxConfig;

import edu.wpi.first.epilogue.Logged;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.ElevatorFeedforward;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.math.trajectory.TrapezoidProfile.Constraints;
import edu.wpi.first.units.Units;
import edu.wpi.first.units.measure.Distance;
import edu.wpi.first.units.measure.LinearVelocity;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.Constants.Ports;
import frc.robot.util.Configs.ElevatorConfigs;
import frc.robot.util.PIDHelper;
import frc.robot.util.Faults.FaultManager;
import frc.robot.util.Faults.FaultTypes.FaultType;
import frc.robot.Constants.ElevatorConstants;;

@Logged
public class ElevatorSubsystem extends SubsystemBase {
    private SparkMax driveMotor, driveMotor2;

    private Object controller;

    private RelativeEncoder boreEncoder;

    private double currentGoal;

    private SparkMaxConfig config = new SparkMaxConfig();

    private final PIDHelper pidHelper;

    private final ElevatorFeedforward feedforward;

    public ElevatorSubsystem(boolean useTrapezoidal) {
        driveMotor = new SparkMax(Ports.CANID.ELEVATOR_DRIVE_1.getId(), MotorType.kBrushless);
        driveMotor2 = new SparkMax(Ports.CANID.ELEVATOR_DRIVE_2.getId(), MotorType.kBrushless);

        driveMotor.configure(ElevatorConfigs.ELEVATOR_CONFIG, ResetMode.kResetSafeParameters,
                PersistMode.kPersistParameters);

        driveMotor2.configure(
                ElevatorConfigs.ELEVATOR_FOLLOWER_CONFIG,
                ResetMode.kResetSafeParameters,
                PersistMode.kPersistParameters);

        feedforward = new ElevatorFeedforward(
                ElevatorConstants.kS,
                ElevatorConstants.kG,
                ElevatorConstants.kV,
                ElevatorConstants.kA);

        if (useTrapezoidal) {
            controller = new ProfiledPIDController(ElevatorConstants.kP, ElevatorConstants.kI, ElevatorConstants.kD,
                    new TrapezoidProfile.Constraints(ElevatorConstants.MAX_MOTOR_RPM,
                            ElevatorConstants.MAX_MOTOR_ACCELERATION));
        } else {
            controller = driveMotor.getClosedLoopController();
        }

        boreEncoder = driveMotor.getEncoder();

        FaultManager.register(driveMotor);
        FaultManager.register(driveMotor2);

        pidHelper = new PIDHelper(
                "Elevator",
                ElevatorConstants.kP,
                ElevatorConstants.kI,
                ElevatorConstants.kD,
                (newP) -> config.closedLoop.p(newP),
                (newI) -> config.closedLoop.i(newI),
                (newD) -> config.closedLoop.d(newD),
                ElevatorConstants.MAX_MOTOR_RPM,
                ElevatorConstants.MAX_MOTOR_ACCELERATION,
                2,
                (newMaxVel) -> config.closedLoop.maxMotion.maxVelocity(newMaxVel),
                (newMaxAccel) -> config.closedLoop.maxMotion.maxAcceleration(newMaxAccel),
                (newAllowedError) -> config.closedLoop.maxMotion.allowedClosedLoopError(newAllowedError));

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
        pidHelper.update();

        SmartDashboard.putNumber("Elevator/1/Output", driveMotor.get());
        SmartDashboard.putNumber("Elevator/1/Voltage", driveMotor.getBusVoltage());
        SmartDashboard.putNumber("Elevator/1/Current", driveMotor.getOutputCurrent());
        SmartDashboard.putNumber("Elevator/1/Temp", driveMotor.getMotorTemperature());
        SmartDashboard.putNumber("Elevator/1/Target", currentGoal);

        SmartDashboard.putNumber("Elevator/2/Output", driveMotor2.get());
        SmartDashboard.putNumber("Elevator/2/Voltage", driveMotor2.getBusVoltage());
        SmartDashboard.putNumber("Elevator/2/Current", driveMotor2.getOutputCurrent());
        SmartDashboard.putNumber("Elevator/2/Temp", driveMotor2.getMotorTemperature());
    }

    public void changeMaxMotion(double mv, double ma, double ae) {
        config.closedLoop.maxMotion.maxVelocity(mv).maxAcceleration(ma).allowedClosedLoopError(ae);
        driveMotor.configure(config, ResetMode.kResetSafeParameters,
                PersistMode.kPersistParameters);
    }

    public void changePID(double p, double i, double d) {
        config.closedLoop.pid(p, i, d);
        driveMotor.configure(config, ResetMode.kResetSafeParameters,
                PersistMode.kPersistParameters);
    }

    public void configure() {
        driveMotor.configure(config, ResetMode.kResetSafeParameters,
                PersistMode.kPersistParameters);
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
            return driveMotor.getBusVoltage();

        return driveMotor2.getBusVoltage();
    }

    public void moveSpeed(double speed) {
        driveMotor.set(MathUtil.clamp(speed, -1, 1));
    }

    public void stop() {
        driveMotor.stopMotor();
    }

    public void reachGoal(double goal) {
        double voltsOut = MathUtil.clamp(
                ((ProfiledPIDController) controller).calculate(getHeight().in(Inches), goal) +
                        feedforward.calculateWithVelocities(getVelocity().in(MetersPerSecond),
                                ((ProfiledPIDController) controller).getSetpoint().velocity),
                -7,
                7); // 7 is the max voltage to send out.
        driveMotor.setVoltage(voltsOut);
    }

    public void setTargetPosition(double position) {
        currentGoal = position;
        if (controller instanceof ProfiledPIDController) {
            // ((ProfiledPIDController) controller).setGoal(new
            // TrapezoidProfile.State(position, 0));
            run(() -> reachGoal(position));
        } else {
            ((SparkClosedLoopController) controller).setReference(position, ControlType.kMAXMotionPositionControl, ClosedLoopSlot.kSlot0, feedforward.calculate(getVelocity().in(MetersPerSecond)));
        }
    }

    public void setTargetHeight(Distance height) {
        setTargetPosition(height.in(Inches));
    }

    public boolean isAtSetpoint() {
        return (getHeight()
                .compareTo(
                        getTargetHeight().minus(Units.Inches.of(ElevatorConstants.TOLERABLE_ERROR))) > 0)
                &&
                getHeight().compareTo(getTargetHeight().plus(Units.Inches.of(ElevatorConstants.TOLERABLE_ERROR))) < 0;
    }

    public Trigger atHeight(double height, double tolerance) {
        return new Trigger(() -> MathUtil.isNear(height,
                getHeight().in(Inches),
                tolerance));
    }

    public double getPosition() {
        return boreEncoder.getPosition();
    }

    public Distance getHeight() {
        return Distance.ofBaseUnits(getPosition(), Units.Inches);
    }

    public double getTargetPosition() {
        return currentGoal;
    }

    public Distance getTargetHeight() {
        return Distance.ofBaseUnits(currentGoal, Units.Inches);
    }

    public LinearVelocity getVelocity() {
        return LinearVelocity.ofBaseUnits(boreEncoder.getVelocity(), Units.InchesPerSecond);
    }

    public void resetSensorPosition(Distance setpoint) {
        boreEncoder.setPosition(setpoint.in(Inches));
    }

}
