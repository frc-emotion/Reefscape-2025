// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import org.ironmaple.simulation.SimulatedArena;

// import edu.wpi.first.epilogue.Epilogue;
import edu.wpi.first.wpilibj.DataLogManager;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.PowerDistribution;
import edu.wpi.first.wpilibj.PowerDistribution.ModuleType;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.Threads;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import frc.robot.util.MotorCurrentMonitor;
import frc.robot.util.diagnostics.Faults.FaultManager;

public class Robot extends TimedRobot {
    private Command m_autonomousCommand;

    // @Logged(name="RobotContainer")
    private final RobotContainer m_robotContainer;
    
    // Power monitoring for battery diagnostics
    private final PowerDistribution pdh = new PowerDistribution(1, ModuleType.kRev);
    private MotorCurrentMonitor motorCurrentMonitor; // Initialized after subsystems

    public Robot() {
        //Epilogue.bind(this);

        // Start data logging for SysId and analysis
        DataLogManager.start();
        DriverStation.startDataLog(DataLogManager.getLog());

        RobotController.setBrownoutVoltage(6.0);

        m_robotContainer = new RobotContainer();
        
        // Initialize motor current monitor with all subsystems
        motorCurrentMonitor = new MotorCurrentMonitor(
            pdh,
            m_robotContainer.getDrivebase(),
            m_robotContainer.getArmSubsystem(),
            m_robotContainer.getElevatorSubsystem(),
            m_robotContainer.getGrabberSubsystem()
        );

        // WebServer not available in current setup
        // WebServer.start(5800, Filesystem.getDeployDirectory().getPath());

        // Run da fault manager once a seocond
        addPeriodic(() -> FaultManager.update(), 1);
        
        // Monitor motor current draw every 100ms with detailed breakdown
        addPeriodic(() -> motorCurrentMonitor.update(), 0.1);

    }

    @Override
    public void robotPeriodic() {
        // Switch thread to high priority to improve loop timing
        Threads.setCurrentThreadPriority(true, 99);
        CommandScheduler.getInstance().run();

        // Return to normal thread priority     
        Threads.setCurrentThreadPriority(false, 10);
    }

    @Override
    public void disabledInit() {
        
    }

    @Override
    public void disabledPeriodic() {
    }

    @Override
    public void disabledExit() {}

    @Override
    public void autonomousInit() {
        m_autonomousCommand = m_robotContainer.getAutonomousCommand();

        if (m_autonomousCommand != null) {
        m_autonomousCommand.schedule();
        }
    }

    @Override
    public void autonomousPeriodic() {}

    @Override
    public void autonomousExit() {}

    @Override
    public void teleopInit() {
        if (m_autonomousCommand != null) {
            m_autonomousCommand.cancel();
        }
        // Heading PID tuning is handled by PIDHelper in SwerveSubsystem.periodic()
        // No need to manually set anything here - NetworkTables updates apply automatically!
    }

    @Override
    public void teleopPeriodic() {

    }
    @Override
    public void teleopExit() {}

    @Override
    public void testInit() {
        CommandScheduler.getInstance().cancelAll();
    }

    @Override
    public void testPeriodic() {}

    @Override
    public void testExit() {}

    @Override
    public void simulationPeriodic() {
        SimulatedArena.getInstance().simulationPeriodic();
    }
}
