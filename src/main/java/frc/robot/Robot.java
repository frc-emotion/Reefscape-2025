// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import org.ironmaple.simulation.SimulatedArena;

// import edu.wpi.first.epilogue.Epilogue;
import edu.wpi.first.epilogue.Logged;
import edu.wpi.first.net.WebServer;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.Threads;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import frc.robot.subsystems.led.LedSubsystem;
import frc.robot.util.diagnostics.Faults.FaultManager;

public class Robot extends TimedRobot {
    private final LedSubsystem LEDSubsystem = new LedSubsystem();
    private Command m_autonomousCommand;

    // @Logged(name="RobotContainer")
    private final RobotContainer m_robotContainer;

    public Robot() {
        //Epilogue.bind(this);

        RobotController.setBrownoutVoltage(6.0);

        m_robotContainer = new RobotContainer();

        
        WebServer.start(5800, Filesystem.getDeployDirectory().getPath());

        // Run da fault manager once a seocond
        addPeriodic(() -> FaultManager.update(), 1);

    }

    @Override
    public void robotPeriodic() {
        // Switch thread to high priority to improve loop timing
        Threads.setCurrentThreadPriority(true, 99);
        // Constants.AutonConstants.updateFromDashboard();
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
        
        LEDSubsystem.setLedAlliance();
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

        LEDSubsystem.setLedAlliance();
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
