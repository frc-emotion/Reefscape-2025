package frc.robot.subsystems.arm;

import static edu.wpi.first.units.Units.Degrees;
import static edu.wpi.first.units.Units.DegreesPerSecond;
import static edu.wpi.first.units.Units.KilogramMetersPerSecond;
import static edu.wpi.first.units.Units.Meter;
import static edu.wpi.first.units.Units.Second;
import static edu.wpi.first.units.Units.Volts;
import static edu.wpi.first.units.Units.VoltsPerMeterPerSecond;

import org.dyn4j.geometry.Rotation;

import com.revrobotics.RelativeEncoder;
import com.revrobotics.spark.ClosedLoopSlot;
import com.revrobotics.spark.SparkAbsoluteEncoder;
import com.revrobotics.spark.SparkClosedLoopController;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkMaxConfig;
import com.revrobotics.spark.SparkBase.ControlType;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;

import edu.wpi.first.epilogue.Logged;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.ArmFeedforward;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.units.measure.Distance;
import edu.wpi.first.units.measure.MutAngle;
import edu.wpi.first.units.measure.MutAngularVelocity;
import edu.wpi.first.units.measure.MutVoltage;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.sysid.SysIdRoutineLog;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine;
import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine.Direction;
import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine.Mechanism;
import frc.robot.config.subsystems.ArmConfig;
import frc.robot.constants.PortMap;
import frc.robot.constants.subsystems.ArmConstants;
import frc.robot.constants.subsystems.ElevatorConstants;
import frc.robot.constants.subsystems.GrabberConstants;
import frc.robot.util.helpers.PIDHelper;
import frc.robot.util.diagnostics.Faults.FaultManager;
import frc.robot.util.diagnostics.Faults.FaultTypes.FaultType;

@Logged
public class ArmSubsystem extends SubsystemBase {
    private final SparkMax armMotor;

    private final RelativeEncoder armEncoder;

    private final ProfiledPIDController pidController;

    private final ArmFeedforward feedforward;

    private Rotation2d targetAngle;

    private final PIDHelper pidHelper;

    private final SparkMaxConfig config = new SparkMaxConfig();

    private double currentGoal, persianGoal;

        // SysId
    private final SysIdRoutine sysIDArmRoutine;
    private MutVoltage appliedVoltage = Volts.mutable(0);
    private MutAngle angle = Degrees.mutable(0);
    private MutAngularVelocity angularVelocity = DegreesPerSecond.mutable(0);

    public ArmSubsystem() {
        armMotor = new SparkMax(PortMap.CANID.ARM_ANGLE.getId(), MotorType.kBrushless);

        armMotor.configure(ArmConfig.ARM_CONFIG, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

        armEncoder = armMotor.getEncoder();

        pidController = new ProfiledPIDController(ArmConstants.kP, ArmConstants.kI, ArmConstants.kD,
                new TrapezoidProfile.Constraints(ArmConstants.kMaxVelocity,
                        ArmConstants.kMaxAcceleration));
        pidController.setTolerance(3.5);

        feedforward = new ArmFeedforward(ArmConstants.kS, ArmConstants.kG, ArmConstants.kV, ArmConstants.kA);

        targetAngle = getRotation();

        FaultManager.register(armMotor);

        pidHelper = new PIDHelper(
                "Arm",
                ArmConstants.kP,
                ArmConstants.kI,
                ArmConstants.kD,
                (newP) -> config.closedLoop.p(newP),
                (newI) -> config.closedLoop.i(newI),
                (newD) -> config.closedLoop.d(newD),
                ArmConstants.kMaxVelocity,
                ArmConstants.kMaxAcceleration,
                2,
                (newMaxVel) -> config.closedLoop.maxMotion.maxVelocity(newMaxVel),
                (newMaxAccel) -> config.closedLoop.maxMotion.maxAcceleration(newMaxAccel),
                (newAllowedError) -> config.closedLoop.maxMotion.allowedClosedLoopError(newAllowedError));

        safetyChecks();

        sysIDArmRoutine = new SysIdRoutine(
            new SysIdRoutine.Config(
                Volts.of(0.25).per(Second),
                Volts.of(3),
                null
            ),
            new SysIdRoutine.Mechanism(volts -> {setVoltage(volts.in(Volts));}, this::logArm, this)
        );
    }

    /**
     * Sets the target angle for the arm. Constrained by the limits defined in
     * currentConstraints.
     * 
     * @param angle The target angle to rotate to
     */
    public void setTargetAngle(Rotation2d angle, Distance elevatorHeight) {
        targetAngle = constrain(angle, elevatorHeight);

        double pidValue = pidController.calculate(getRotation().getDegrees(), targetAngle.getDegrees());
        double ffValue = feedforward.calculate(getRotation().getRadians(), getVelocity().getRadians());

        armMotor.set(MathUtil.clamp(ffValue + pidValue, -12, 12));

    }

    private void safetyChecks() {
        FaultManager.register(
                () -> armMotor.getOutputCurrent() > ArmConstants.NORMAL_OPERATION_CURRENT
                        + ArmConstants.CURRENT_SPIKE_THRESHOLD,
                "Arm Drive",
                "Possible unsafe current spike",
                FaultType.WARNING);

        FaultManager.register(
                () -> armMotor.getMotorTemperature() > ArmConstants.NORMAL_OPERATION_TEMP
                        + ArmConstants.TEMP_SPIKE_THRESHOLD,
                "Arm Drive",
                "Possible unsafe motor temperature",
                FaultType.WARNING);

    }

    /**
     * Sets the target angle for the arm. Constrained by the limits defined in
     * currentConstraints.
     * 
     * @param angle The target angle to rotate to
     */
    public void setTargetAngle(Rotation2d angle) {
        targetAngle = constrain(angle, ElevatorConstants.CORAL_INTAKE_HEIGHT);
        currentGoal = targetAngle.getDegrees();

        // pidController.setReference(
        // targetAngle.getDegrees(),
        // ControlType.kMAXMotionPositionControl,
        // ClosedLoopSlot.kSlot0,
        // feedforward.calculate(getRotation().getRadians(),
        // getVelocity().getRadians()));

        double pidValue = pidController.calculate(getRotation().getDegrees(), currentGoal);
        double ffValue = feedforward.calculate(getRotation().getRadians(), getVelocity().getRadians());

        armMotor.set(MathUtil.clamp(ffValue + pidValue, -12, 12));
    }

    /**
     * Sends raw input to the motor
     * 
     * @param power The power input to the motor within [-1, 1].
     */
    public void set(double power) {
        armMotor.set(power);
    }

    public void setWithFeedforward(double percentage) {
        armMotor.set(
                MathUtil.clamp(
                        percentage * ArmConstants.kMaxOutput
                                + feedforward.calculate(getRotation().getRadians(), getVelocity().getRadians()),
                        -1, 1));
    }

    /**
     * Constrains the rotation of the arm depending on the height of the elevator.
     * 
     * @param targetRotation The desired rotation for the arm.
     * @param elevatorHeight The current height of the elevator.
     * @return The new target rotation, taking into account limits.
     */
    public Rotation2d constrain(Rotation2d targetRotation, Distance elevatorHeight) {
        System.out.println(targetRotation);

        if (elevatorHeight.isNear(
                Meter.of(0),
                Meter.of(ArmConstants.kMaxHeightConstrained))) {
                    System.out.println("SALAMALKAUMWALAKAUM SALAAMMMMMMMMM WAIT WAIT WAI0WT WAIT AWIT WOAAAAAAO");
            return Rotation2d.fromDegrees(MathUtil.clamp(targetRotation.getDegrees(), ArmConstants.kMinRotationConstrained, ArmConstants.kMaxRotationConstrained )); // the problem gng frfr yurrrr
        } else {
            if (targetRotation.getDegrees() > ArmConstants.kMaxRotation) {
                System.out.println("YURRRRRRR");
                return Rotation2d.fromDegrees(ArmConstants.kMaxRotation);
            } else if (targetRotation.getDegrees() < ArmConstants.kMinRotation) {
                System.out.println("blud");
                return Rotation2d.fromDegrees(ArmConstants.kMinRotation);
            }
        }

        persianGoal = targetRotation.getDegrees();

        return targetRotation;
    }

    public Rotation2d getRotation() {
        return Rotation2d
                .fromDegrees((armEncoder.getPosition() * ArmConstants.kConversionFactor) + ArmConstants.kZeroOffset);
    }

    public double persianTest() {
        return armEncoder.getPosition();
    }

    public Rotation2d getTargetRotation() {
        return targetAngle;
    }

    public Rotation2d getVelocity() {
        return Rotation2d.fromDegrees(armEncoder.getVelocity() * ArmConstants.kConversionFactor);
    }

    public void stop() {
        set(0);
    }

    public double getCurrent() {
        return armMotor.getOutputCurrent();
    }

    public double getVoltage() {
        return armMotor.get() * RobotController.getBatteryVoltage();  
    }

    public void setVoltage(double volts) {
        armMotor.setVoltage(volts);
    }

    private void logArm(SysIdRoutineLog log) {
        log.motor("arm-motor")
            .voltage(
                appliedVoltage.mut_replace(
                    getVoltage(), Volts
                )
            )
            .angularPosition(
                angle.mut_replace(
                    getRotation().getDegrees(), Degrees
                )
            )
            .angularVelocity(
                angularVelocity.mut_replace(
                    getVelocity().getDegrees(), DegreesPerSecond
                )
            );
    }

    public Command getSysIdQuasistatic(Direction direction) {
        return sysIDArmRoutine.quasistatic(direction);
    }

    public Command getSysIdDynamic(Direction direction) {
        return sysIDArmRoutine.dynamic(direction);
    }

    @Override
    public void periodic() {
        pidHelper.update();

        SmartDashboard.putNumber("Arm/1/Output", armMotor.get());
        SmartDashboard.putNumber("Arm/1/Voltage", armMotor.getBusVoltage());
        SmartDashboard.putNumber("Arm/1/Current", armMotor.getOutputCurrent());
        SmartDashboard.putNumber("Arm/1/Temp", armMotor.getMotorTemperature());
        SmartDashboard.putNumber("Arm/1/Position", getRotation().getDegrees());
        SmartDashboard.putNumber("Arm/1/Target", currentGoal);
        SmartDashboard.putNumber("Arm/1/Velocity", getVelocity().getDegrees());
        SmartDashboard.putNumber("Arm/1/Persian", persianTest());
        SmartDashboard.putNumber("Arm/1/SALAMALAKUM", persianGoal);
    }

    public boolean isAtSetpoint() {
        return pidController.atSetpoint();
    }

}
