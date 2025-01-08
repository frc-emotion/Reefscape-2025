package frc.robot.util;

import edu.wpi.first.units.Units;
import edu.wpi.first.units.measure.*;

public class UnitsUtil {
    /**
     * Converts to meters per second squared
     */
    public static double convertToMPSS(LinearAcceleration acceleration) {
        return Units.MetersPerSecondPerSecond.convertFrom(
            acceleration.magnitude(),
            acceleration.unit()
        );
    }

    /**
     * Converts to degrees per second squared
     */
    public static double convertToDPSS(AngularAcceleration angularAcceleration) {
        return Units.DegreesPerSecondPerSecond.convertFrom(
            angularAcceleration.magnitude(),
            angularAcceleration.unit()
        );
    }

    /**
     * Converts to meters per second
     */
    public static double convertToMPS(LinearVelocity velocity) {
        return Units.MetersPerSecond.convertFrom(
            velocity.magnitude(),
            velocity.unit()
        );
    }

    /**
     * Converts to degrees per second
     */
    public static double convertToDPS(AngularVelocity angularVelocity) {
        return Units.DegreesPerSecond.convertFrom(
            angularVelocity.magnitude(),
            angularVelocity.unit()
        );
    }
}
