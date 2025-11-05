package frc.robot.subsystems.elevator;

import static edu.wpi.first.units.Units.Degrees;
import static edu.wpi.first.units.Units.DegreesPerSecond;
import static edu.wpi.first.units.Units.Inches;
import static edu.wpi.first.units.Units.Meter;
import static edu.wpi.first.units.Units.Meters;
import static edu.wpi.first.units.Units.MetersPerSecond;
import static edu.wpi.first.units.Units.Volts;

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
import edu.wpi.first.units.measure.MutAngle;
import edu.wpi.first.units.measure.MutAngularVelocity;
import edu.wpi.first.units.measure.MutDistance;
import edu.wpi.first.units.measure.MutVoltage;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.sysid.SysIdRoutineLog;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine;
import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine.Direction;
import frc.robot.config.subsystems.ElevatorConfig;
import frc.robot.constants.PortMap;
import frc.robot.constants.subsystems.ElevatorConstants;
import frc.robot.util.helpers.PIDHelper;
import frc.robot.util.diagnostics.Faults.FaultManager;
import frc.robot.util.diagnostics.Faults.FaultTypes.FaultType;

@Logged
public class ElevatorSubsystem extends SubsystemBase {
    private SparkMax driveMotor, driveMotor2;

    private Object controller;

    private RelativeEncoder boreEncoder;

    private double currentGoal;

    private SparkMaxConfig config = new SparkMaxConfig();

    private final PIDHelper pidHelper;

    private final ElevatorFeedforward feedforward;

    private final SysIdRoutine sysIDElevatorRoutine;
    private MutVoltage appliedVoltageLead = Volts.mutable(0);
    private MutVoltage appliedVoltageFollow = Volts.mutable(0);
    private MutDistance position = Meters.mutable(0);

    public ElevatorSubsystem(boolean useTrapezoidal) {
        driveMotor = new SparkMax(PortMap.CANID.ELEVATOR_DRIVE_LEADER.getId(), MotorType.kBrushless);
        driveMotor2 = new SparkMax(PortMap.CANID.ELEVATOR_DRIVE_FOLLOWER.getId(), MotorType.kBrushless);

        driveMotor.configure(ElevatorConfig.ELEVATOR_CONFIG, ResetMode.kResetSafeParameters,
                PersistMode.kPersistParameters);

        driveMotor2.configure(
                ElevatorConfig.ELEVATOR_FOLLOWER_CONFIG,
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
            ((ProfiledPIDController) controller).setTolerance(0.1);
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

        sysIDElevatorRoutine = new SysIdRoutine(
            new SysIdRoutine.Config(),
            new SysIdRoutine.Mechanism(volts -> {setVoltage(volts.in(Volts));}, this::logElevator, this)
        );
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
        // Elevator 1 = 17 - Leader
        SmartDashboard.putNumber("Elevator/Leader/Output", driveMotor.get());
        SmartDashboard.putNumber("Elevator/Leader/Voltage", driveMotor.getBusVoltage());
        SmartDashboard.putNumber("Elevator/Leader/Current", driveMotor.getOutputCurrent());
        SmartDashboard.putNumber("Elevator/Leader/Temp", driveMotor.getMotorTemperature());
        SmartDashboard.putNumber("Elevator/Leader/Target", currentGoal);
        SmartDashboard.putNumber("Elevator/Leader/Position", getPosition());
        SmartDashboard.putBoolean("Elevator/Leader/PersianGoal", controllerAtSetpoint());

        // Elevator 2 = 18 - Follower
        SmartDashboard.putNumber("Elevator/Follower/Output", driveMotor2.get());
        SmartDashboard.putNumber("Elevator/Follower/Voltage", driveMotor2.getBusVoltage());
        SmartDashboard.putNumber("Elevator/Follower/Current", driveMotor2.getOutputCurrent());
        SmartDashboard.putNumber("Elevator/Follower/Temp", driveMotor2.getMotorTemperature());
    }

    public void setVoltage(double volts) {
        driveMotor.setVoltage(volts);
    }

    public void logElevator(SysIdRoutineLog log) {
        log.motor("elevator-lead-motor")
            .voltage(
                appliedVoltageLead.mut_replace(
                    getVoltage(true), Volts
                )
            )
            .linearPosition(
                position.mut_replace(
                    getHeight()
                )
            );

        log.motor("elevator-follow-motor")
            .voltage(
                appliedVoltageFollow.mut_replace(
                    getVoltage(false), Volts
                )
            );
            
    }

    public Command getSysIdDynamic(Direction direction) {
        return sysIDElevatorRoutine.dynamic(direction);
    }

    public Command getSysIdQuasistatic(Direction direction) {
        return sysIDElevatorRoutine.quasistatic(direction);
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
            return driveMotor.get() * RobotController.getBatteryVoltage();

        return driveMotor2.get() * RobotController.getBatteryVoltage();
    }

    public void set(double speed) {
        driveMotor.set(MathUtil.clamp(speed, -1, 1));
    }

    public void setWithFeedforward(double speed) {
        double inVoltage = speed * 12.0;
        double ff = feedforward.calculate(getVelocity().in(MetersPerSecond));

        driveMotor.setVoltage(
            MathUtil.clamp(inVoltage + ff, -RobotController.getBatteryVoltage(), RobotController.getBatteryVoltage())
        );
    }

    public void stop() {
        driveMotor.stopMotor();
    }

    public void reachGoal(double goal) {
        
        double voltsOut = MathUtil.clamp(
                ((ProfiledPIDController) controller).calculate(getPosition(), goal) +
                        feedforward.calculate(getVelocity().in(MetersPerSecond)),
                -RobotController.getBatteryVoltage(),
                RobotController.getBatteryVoltage());
        
        System.out.println("MEOW " + voltsOut);
        driveMotor.setVoltage(voltsOut);
    }

    public void setTargetPosition(double position) {
        currentGoal = position;
        reachGoal(position);
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
    
    public boolean controllerAtSetpoint() {
        return ((ProfiledPIDController) controller).atSetpoint();
    }

    public boolean isAtSetpoint(double target) {
        return (getHeight()
                .compareTo(
                        Distance.ofBaseUnits(target, Inches).minus(Units.Inches.of(ElevatorConstants.TOLERABLE_ERROR))) > 0)
                &&
                getHeight().compareTo(Distance.ofBaseUnits(target, Inches).plus(Units.Inches.of(ElevatorConstants.TOLERABLE_ERROR))) < 0;
    }

    public Trigger atHeight(double height, double tolerance) {
        return new Trigger(() -> MathUtil.isNear(height,
                getHeight().in(Inches),
                tolerance));
    }

    public double getPosition() {
        return boreEncoder.getPosition() * ElevatorConstants.inchesPerCount;
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

    public static double convertDistanceToEncoderCounts(Distance height) {
        return height.in(Inches) / ElevatorConstants.inchesPerCount;
    }

}
