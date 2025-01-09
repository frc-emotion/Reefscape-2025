package frc.robot.subsystems.elevator;

import static edu.wpi.first.units.Units.Meters;
import static edu.wpi.first.units.Units.MetersPerSecond;

import edu.wpi.first.math.controller.ElevatorFeedforward;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.system.plant.DCMotor;
import edu.wpi.first.units.measure.Distance;
import edu.wpi.first.units.measure.LinearVelocity;
import edu.wpi.first.wpilibj.simulation.ElevatorSim;
import frc.robot.util.Constants.ElevatorConstants;

public class SimElevatorSubsystem implements ElevatorSubsystem {
    private final ElevatorSim elevatorSim;

    private final ElevatorFeedforward feedforward;

    private final PIDController pidController;

    private double targetPosition;
    
    public SimElevatorSubsystem() {
        elevatorSim = new ElevatorSim(
            DCMotor.getNEO(ElevatorConstants.kMotorCount),
            ElevatorConstants.kGearRatio,
            ElevatorConstants.kCarriageMassKg,
            ElevatorConstants.kDrumRadiusMeters,
            ElevatorConstants.kMinHeightMeters,
            ElevatorConstants.kMaxHeightMeters,
            true,
            ElevatorConstants.kStartingHeightMeters
        );

        feedforward = new ElevatorFeedforward(
            ElevatorConstants.kS,
            ElevatorConstants.kG,
            ElevatorConstants.kV
        );

        pidController = new PIDController(
            ElevatorConstants.kP,
            ElevatorConstants.kI,
            ElevatorConstants.kD
        );
    }

    /**
     * See the setTargetHeight method. 
     */
    @Override
    public void setTargetPosition(double position) {
        setTargetHeight(Distance.ofBaseUnits(position, Meters));
    }

    @Override
    public void setTargetHeight(Distance height) {
        targetPosition = height.magnitude();
        double pidValue = pidController.calculate(getHeight().magnitude(), height.magnitude());
        
        elevatorSim.setInputVoltage(
            feedforward.calculate(pidValue)
        );
    }

    /**
     * Calls getHeight() and returns the height as a double.
     * 
     * @return The height of the elevator in meters.
     */
    @Override
    public double getPosition() {
        return getHeight().magnitude();
    }

    @Override
    public Distance getHeight() {
        return Distance.ofBaseUnits(elevatorSim.getPositionMeters(), Meters);
    }

    /**
     * Returns the target position as a double.
     * 
     * @return The target height of the elevator in meters.
     */
    @Override
    public double getTargetPosition() {
        return targetPosition;
    }

    /**
     * Returns the target position as a Distance.
     * 
     * @return The target height of the elevator.
     */
    @Override
    public Distance getTargetHeight() {
        return Distance.ofBaseUnits(targetPosition, Meters);
    }

    @Override
    public LinearVelocity getVelocity() {
        return LinearVelocity.ofBaseUnits(elevatorSim.getVelocityMetersPerSecond(), MetersPerSecond);
    }
}
