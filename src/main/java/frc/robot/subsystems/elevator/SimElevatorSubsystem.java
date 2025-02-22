package frc.robot.subsystems.elevator;

import static edu.wpi.first.units.Units.Kilograms;
import static edu.wpi.first.units.Units.Meters;
import static edu.wpi.first.units.Units.MetersPerSecond;

import edu.wpi.first.math.controller.ElevatorFeedforward;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.system.plant.DCMotor;
import edu.wpi.first.math.trajectory.TrapezoidProfile.Constraints;
import edu.wpi.first.units.measure.Distance;
import edu.wpi.first.units.measure.LinearVelocity;
import edu.wpi.first.wpilibj.simulation.ElevatorSim;
import frc.robot.Constants.ElevatorConstants;
import frc.robot.util.PIDHelper;

public class SimElevatorSubsystem {
    private final ElevatorSim elevatorSim;

    private final PIDHelper pidHelper;

    private final ProfiledPIDController pidController;
    
    private final ElevatorFeedforward feedforward;

    public SimElevatorSubsystem() {
        elevatorSim = new ElevatorSim(
            DCMotor.getNEO(2),
            ElevatorConstants.kGearRatio,
            ElevatorConstants.kCarriageMass.in(Kilograms),
            ElevatorConstants.kDrumRadius.in(Meters),
            ElevatorConstants.kMinHeight.in(Meters),
            ElevatorConstants.kMaxHeight.in(Meters),
            true,
            ElevatorConstants.kStartingHeight.in(Meters)
        );

        pidController = new ProfiledPIDController(
            ElevatorConstants.kP,
            ElevatorConstants.kI,
            ElevatorConstants.kD,
            new Constraints(
                ElevatorConstants.MAX_MOTOR_RPM,
                ElevatorConstants.MAX_MOTOR_ACCELERATION
            )
        );

        feedforward = new ElevatorFeedforward(
            ElevatorConstants.kS,
            ElevatorConstants.kG,
            ElevatorConstants.kV,
            ElevatorConstants.kA
        );

        pidHelper = new PIDHelper(
            "Sim Elevator",
            ElevatorConstants.kP,
            ElevatorConstants.kI,
            ElevatorConstants.kD,
            (kP) -> pidController.setP(kP),
            (kI) -> pidController.setI(kI),
            (kD) -> pidController.setD(kD)
        );
    }

    public void reachGoal(Distance goal) {
        double pidValue = pidController.calculate(getHeight().in(Meters), goal.in(Meters));
        double ffValue = feedforward.calculate(getVelocity().in(MetersPerSecond));

        elevatorSim.setInputVoltage(pidValue + ffValue);
    }

    public Distance getHeight() {
        return Meters.of(elevatorSim.getPositionMeters());
    }

    public LinearVelocity getVelocity() {
        return MetersPerSecond.of(elevatorSim.getVelocityMetersPerSecond());
    }
}
