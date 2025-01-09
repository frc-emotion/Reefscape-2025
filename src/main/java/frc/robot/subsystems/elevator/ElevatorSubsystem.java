package frc.robot.subsystems.elevator;

import edu.wpi.first.units.measure.Distance;
import edu.wpi.first.units.measure.LinearVelocity;
import edu.wpi.first.wpilibj2.command.Subsystem;

/**
 * Provides all the method templates for all versions of the Elevator (Sim and Real).
 */
public interface ElevatorSubsystem extends Subsystem {
    
    
    /**
     * Sets the target position for the elevator.
     * 
     * @param position The target elevator position in encoder counts.
     */
    void setTargetPosition(double position);


    /**
     * Sets the target height for the elevator.
     * 
     * @param height The target elevator height.
     */
    void setTargetHeight(Distance height);

    /**
     * Retrieves the current position of the elevator.
     * 
     * @return The current position in encoder counts.
     */
    double getPosition();

    /**
     * Retrieves the current height of the elevator.
     * 
     * @return The current height of the elevator.
     */
    Distance getHeight();

    /**
     * Retrieves the target position for the elevator.
     * 
     * @return The target position of the elevator in encoder counts.
     */
    double getTargetPosition();

    /**
     * Retrieves the target height for the elevator.
     * 
     * @return The target height of the elevator.
     */
    Distance getTargetHeight();

    /**
     * Retrieves the current velocity of the elevator.
     * 
     * @return The current velocity of the elevator.
     */
    LinearVelocity getVelocity();
}
