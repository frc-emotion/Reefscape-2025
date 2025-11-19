// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.util;

import com.revrobotics.spark.SparkMax;

import edu.wpi.first.wpilibj.PowerDistribution;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.subsystems.swerve.SwerveSubsystem;
import frc.robot.subsystems.arm.ArmSubsystem;
import frc.robot.subsystems.elevator.ElevatorSubsystem;
import frc.robot.subsystems.grabber.GrabberSubsystem;
import swervelib.SwerveModule;

/**
 * Monitors current draw by reading directly from motor controllers.
 * No PDH channel mapping needed - uses built-in SparkMax current sensing.
 */
public class MotorCurrentMonitor {
    
    private final PowerDistribution pdh;
    private final SwerveSubsystem swerve;
    private final ArmSubsystem arm;
    private final ElevatorSubsystem elevator;
    private final GrabberSubsystem grabber;
    
    /**
     * Creates a motor current monitor.
     * 
     * @param pdh PDH for total battery stats
     * @param swerve Swerve subsystem
     * @param arm Arm subsystem
     * @param elevator Elevator subsystem
     * @param grabber Grabber subsystem
     */
    public MotorCurrentMonitor(PowerDistribution pdh, 
                               SwerveSubsystem swerve,
                               ArmSubsystem arm,
                               ElevatorSubsystem elevator,
                               GrabberSubsystem grabber) {
        this.pdh = pdh;
        this.swerve = swerve;
        this.arm = arm;
        this.elevator = elevator;
        this.grabber = grabber;
    }
    
    /**
     * Updates all current monitoring to SmartDashboard.
     * Call this periodically (e.g., every 100ms).
     */
    public void update() {
        updateBatteryStats();
        updateSwerveCurrents();
        updateSubsystemCurrents();
        updateSummary();
    }
    
    /**
     * Updates overall battery statistics from PDH.
     */
    private void updateBatteryStats() {
        double voltage = pdh.getVoltage();
        double totalCurrent = pdh.getTotalCurrent();
        double totalPower = pdh.getTotalPower();
        
        SmartDashboard.putNumber("Power/Battery/Voltage", voltage);
        SmartDashboard.putNumber("Power/Battery/TotalCurrent", totalCurrent);
        SmartDashboard.putNumber("Power/Battery/TotalPower", totalPower);
        SmartDashboard.putNumber("Power/Battery/Temperature", pdh.getTemperature());
        
        // Status indicator with color coding
        String status;
        if (voltage < 11.0) {
            status = "🔴 CRITICAL";
        } else if (voltage < 11.5) {
            status = "🟠 Low";
        } else if (voltage < 12.0) {
            status = "🟡 Fair";
        } else {
            status = "🟢 Good";
        }
        SmartDashboard.putString("Power/Battery/Status", status);
        
        // Voltage drop indicator for your 2V issue
        double voltageDrop = 12.6 - voltage; // Assuming fresh battery is ~12.6V
        SmartDashboard.putNumber("Power/Battery/VoltageDrop", voltageDrop);
        if (voltageDrop > 1.5) {
            SmartDashboard.putBoolean("Power/Battery/HighDrop", true);
        } else {
            SmartDashboard.putBoolean("Power/Battery/HighDrop", false);
        }
    }
    
    /**
     * Updates swerve drive current breakdown.
     * Reads directly from each swerve module's motor controllers via YAGSL.
     */
    private void updateSwerveCurrents() {
        SwerveModule[] modules = swerve.getSwerveDrive().getModules();
        
        double totalDriveCurrent = 0;
        double totalAngleCurrent = 0;
        
        // Read current directly from motor controllers
        for (int i = 0; i < modules.length; i++) {
            SwerveModule module = modules[i];
            
            // Get current from motor controllers
            // Check if motor object is instance of SparkMax and if it is assume that we can get current
            if (!(module.getDriveMotor().getMotor() instanceof SparkMax) || 
                !(module.getAngleMotor().getMotor() instanceof SparkMax)) {
                SmartDashboard.putString("Power/Swerve/" + getModuleName(i) + "/Error", "Non-SparkMax motor");
                continue; // Skip if not SparkMax
            }
            
            double driveCurrent = ((SparkMax) module.getDriveMotor().getMotor()).getOutputCurrent();
            double angleCurrent = ((SparkMax) module.getAngleMotor().getMotor()).getOutputCurrent();
            
            totalDriveCurrent += driveCurrent;
            totalAngleCurrent += angleCurrent;
            
            // Per-module breakdown
            String moduleName = getModuleName(i);
            SmartDashboard.putNumber("Power/Swerve/" + moduleName + "/Drive", driveCurrent);
            SmartDashboard.putNumber("Power/Swerve/" + moduleName + "/Angle", angleCurrent);
            SmartDashboard.putNumber("Power/Swerve/" + moduleName + "/Total", driveCurrent + angleCurrent);
        }
        
        // Aggregate swerve stats
        double swerveTotalCurrent = totalDriveCurrent + totalAngleCurrent;
        SmartDashboard.putNumber("Power/Swerve/TotalCurrent", swerveTotalCurrent);
        SmartDashboard.putNumber("Power/Swerve/DriveMotorsCurrent", totalDriveCurrent);
        SmartDashboard.putNumber("Power/Swerve/AngleMotorsCurrent", totalAngleCurrent);
        
        // Calculate percentage of total battery current
        double totalBatteryCurrent = pdh.getTotalCurrent();
        if (totalBatteryCurrent > 0.1) { // Avoid division by zero
            double swervePercent = (swerveTotalCurrent / totalBatteryCurrent) * 100;
            SmartDashboard.putNumber("Power/Swerve/PercentOfTotal", swervePercent);
        }
        
        // Flag if angle motors are drawing too much (indicator of rotation issue)
        if (totalAngleCurrent > 60) { // 4 motors × 15A average = 60A is high
            SmartDashboard.putBoolean("Power/Swerve/HighAngleCurrent", true);
        } else {
            SmartDashboard.putBoolean("Power/Swerve/HighAngleCurrent", false);
        }
    }
    
    /**
     * Updates current for other subsystems.
     */
    private void updateSubsystemCurrents() {
        // Arm current (single motor)
        double armCurrent = arm.getMotorCurrent();
        SmartDashboard.putNumber("Power/Arm/Current", armCurrent);
        
        // Elevator current (leader + follower through getMotorCurrent)
        double elevatorCurrent = elevator.getMotorCurrent();
        SmartDashboard.putNumber("Power/Elevator/Current", elevatorCurrent);
        
        // Grabber - already monitored in GrabberSubsystem.periodic()
        // We can read it from there or add a getter
        // For now, just note it's available at "Grabber/1/Current"
    }
    
    /**
     * Creates a summary view with key metrics.
     */
    private void updateSummary() {
        // Get all subsystem currents
        double swerveCurrent = SmartDashboard.getNumber("Power/Swerve/TotalCurrent", 0);
        double armCurrent = SmartDashboard.getNumber("Power/Arm/Current", 0);
        double elevatorCurrent = SmartDashboard.getNumber("Power/Elevator/Current", 0);
        double grabberCurrent = SmartDashboard.getNumber("Grabber/1/Current", 0);
        
        // Calculate what we can account for
        double accountedCurrent = swerveCurrent + armCurrent + elevatorCurrent + grabberCurrent;
        double totalCurrent = pdh.getTotalCurrent();
        double unaccountedCurrent = totalCurrent - accountedCurrent;
        
        SmartDashboard.putNumber("Power/Summary/AccountedCurrent", accountedCurrent);
        SmartDashboard.putNumber("Power/Summary/UnaccountedCurrent", Math.max(0, unaccountedCurrent));
        
        // Create a text summary for quick viewing
        String summary = String.format(
            "V:%.1f | Total:%.0fA | Swerve:%.0fA (Drive:%.0fA Angle:%.0fA) | Arm:%.0fA | Elev:%.0fA",
            pdh.getVoltage(),
            totalCurrent,
            swerveCurrent,
            SmartDashboard.getNumber("Power/Swerve/DriveMotorsCurrent", 0),
            SmartDashboard.getNumber("Power/Swerve/AngleMotorsCurrent", 0),
            armCurrent,
            elevatorCurrent
        );
        SmartDashboard.putString("Power/Summary/QuickView", summary);
    }
    
    /**
     * Gets a readable name for a swerve module by index.
     */
    private String getModuleName(int index) {
        switch (index) {
            case 0: return "FrontLeft";
            case 1: return "FrontRight";
            case 2: return "BackLeft";
            case 3: return "BackRight";
            default: return "Module" + index;
        }
    }
    
    /**
     * Prints detailed current breakdown to console.
     * Useful for debugging in the terminal.
     */
    public void printDetailedReport() {
        System.out.println("\n========== MOTOR CURRENT REPORT ==========");
        System.out.println(String.format("Battery: %.2fV | Total Current: %.1fA | Power: %.1fW",
            pdh.getVoltage(), pdh.getTotalCurrent(), pdh.getTotalPower()));
        
        System.out.println("\n--- Swerve Drive ---");
        SwerveModule[] modules = swerve.getSwerveDrive().getModules();
        for (int i = 0; i < modules.length; i++) {
            SwerveModule module = modules[i];
            if (!(module.getDriveMotor().getMotor() instanceof SparkMax) || 
                !(module.getAngleMotor().getMotor() instanceof SparkMax)) {
                    System.out.println(String.format("  %s: Drive=ERROR, Angle=ERROR", getModuleName(i)));
                    continue; // Skip if not SparkMax
                }
        
            
            double driveCurrent = ((SparkMax) module.getDriveMotor().getMotor()).getOutputCurrent();
            double angleCurrent = ((SparkMax) module.getAngleMotor().getMotor()).getOutputCurrent();

            System.out.println(String.format("  %s: Drive=%.1fA, Angle=%.1fA",
                getModuleName(i),
                driveCurrent,
                angleCurrent));
        }
        
        System.out.println("\n--- Other Subsystems ---");
        System.out.println(String.format("  Arm: %.1fA", arm.getMotorCurrent()));
        System.out.println(String.format("  Elevator: %.1fA", elevator.getMotorCurrent()));
        System.out.println(String.format("  Grabber: %.1fA", 
            SmartDashboard.getNumber("Grabber/1/Current", 0)));
        
        System.out.println("==========================================\n");
    }
}
