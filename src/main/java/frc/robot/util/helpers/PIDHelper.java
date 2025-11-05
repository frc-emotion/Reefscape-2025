package frc.robot.util.helpers;

import java.util.function.DoubleConsumer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * A generic helper for tuning PID gains and max motion parameters via SmartDashboard.
 */
public class PIDHelper {
    // PID gains
    private double p;
    private double i;
    private double d;
    private final DoubleConsumer updateP;
    private final DoubleConsumer updateI;
    private final DoubleConsumer updateD;
    private final String keyP;
    private final String keyI;
    private final String keyD;

    // Max motion parameters
    private boolean includeMaxMotion;
    private double maxVelocity;
    private double maxAcceleration;
    private double allowedError;
    private DoubleConsumer updateMaxVelocity;
    private DoubleConsumer updateMaxAcceleration;
    private DoubleConsumer updateAllowedError;
    private String keyMaxVelocity;
    private String keyMaxAcceleration;
    private String keyAllowedError;

    /**
     * Constructor for PID-only tuning.
     */
    public PIDHelper(String subsystemName, double defaultP, double defaultI, double defaultD,
                     DoubleConsumer updateP, DoubleConsumer updateI, DoubleConsumer updateD) {
        this.p = defaultP;
        this.i = defaultI;
        this.d = defaultD;
        this.updateP = updateP;
        this.updateI = updateI;
        this.updateD = updateD;
        this.keyP = subsystemName + " PID kP";
        this.keyI = subsystemName + " PID kI";
        this.keyD = subsystemName + " PID kD";

        SmartDashboard.putNumber(keyP, p);
        SmartDashboard.putNumber(keyI, i);
        SmartDashboard.putNumber(keyD, d);
        this.includeMaxMotion = false;
    }

    /**
     * Constructor for tuning both PID gains and max motion parameters.
     */
    public PIDHelper(String subsystemName, double defaultP, double defaultI, double defaultD,
                     DoubleConsumer updateP, DoubleConsumer updateI, DoubleConsumer updateD,
                     double defaultMaxVelocity, double defaultMaxAcceleration, double defaultAllowedError,
                     DoubleConsumer updateMaxVelocity, DoubleConsumer updateMaxAcceleration, DoubleConsumer updateAllowedError) {
        this(subsystemName, defaultP, defaultI, defaultD, updateP, updateI, updateD);
        
        this.includeMaxMotion = true;
        this.maxVelocity = defaultMaxVelocity;
        this.maxAcceleration = defaultMaxAcceleration;
        this.allowedError = defaultAllowedError;
        this.updateMaxVelocity = updateMaxVelocity;
        this.updateMaxAcceleration = updateMaxAcceleration;
        this.updateAllowedError = updateAllowedError;
        this.keyMaxVelocity = subsystemName + " Max Velocity";
        this.keyMaxAcceleration = subsystemName + " Max Acceleration";
        this.keyAllowedError = subsystemName + " Allowed Error";

        SmartDashboard.putNumber(keyMaxVelocity, maxVelocity);
        SmartDashboard.putNumber(keyMaxAcceleration, maxAcceleration);
        SmartDashboard.putNumber(keyAllowedError, allowedError);
    }

    /**
     * Call this method periodically to update parameters from SmartDashboard.
     */
    public void update() {
        // Update PID parameters.
        double newP = SmartDashboard.getNumber(keyP, p);
        double newI = SmartDashboard.getNumber(keyI, i);
        double newD = SmartDashboard.getNumber(keyD, d);
        if (newP != p) {
            p = newP;
            updateP.accept(p);
        }
        if (newI != i) {
            i = newI;
            updateI.accept(i);
        }
        if (newD != d) {
            d = newD;
            updateD.accept(d);
        }
        // Update max motion parameters if enabled.
        if (includeMaxMotion) {
            double newMaxVelocity = SmartDashboard.getNumber(keyMaxVelocity, maxVelocity);
            double newMaxAcceleration = SmartDashboard.getNumber(keyMaxAcceleration, maxAcceleration);
            double newAllowedError = SmartDashboard.getNumber(keyAllowedError, allowedError);
            if (newMaxVelocity != maxVelocity) {
                maxVelocity = newMaxVelocity;
                updateMaxVelocity.accept(maxVelocity);
            }
            if (newMaxAcceleration != maxAcceleration) {
                maxAcceleration = newMaxAcceleration;
                updateMaxAcceleration.accept(maxAcceleration);
            }
            if (newAllowedError != allowedError) {
                allowedError = newAllowedError;
                updateAllowedError.accept(allowedError);
            }
        }
    }
}