package frc.robot.util;

/**
 * Interface to enforce a standardized fault checking strategy.
 *
 * @param <T> The type of fault data object.
 */
public interface FaultCheckable<T> {
    /**
     * Checks if the fault is present in the given fault data.
     *
     * @param faults The fault data object containing current fault states.
     * @return True if the fault is present, false otherwise.
     */
    boolean isPresent(T faults);
}