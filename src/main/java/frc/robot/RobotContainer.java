// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.epilogue.Logged;
import edu.wpi.first.wpilibj.DataLogManager;
import edu.wpi.first.wpilibj.PowerDistribution;
import edu.wpi.first.wpilibj.PowerDistribution.ModuleType;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.commands.teleop.Swerve.SwerveDriveCommand;
import frc.robot.commands.teleop.Swerve.SwerveXCommand;
import frc.robot.subsystems.swerve.*;
import frc.robot.util.Constants;
import frc.robot.util.Constants.IOConstants;
import frc.robot.util.Faults.FaultManager;

public class RobotContainer {
    private final CommandXboxController m_driverController = new CommandXboxController(IOConstants.DRIVER_PORT);
    private final CommandXboxController m_operatorController = new CommandXboxController(IOConstants.OPERATOR_PORT);

    PowerDistribution m_PDH = new PowerDistribution(Constants.Ports.CANID.PDH.getId(), ModuleType.kRev);

    @Logged(name="SwerveSubsystem")
    private final SwerveSubsystem m_swerveSubsystem;

    public RobotContainer() {
        //FaultManager.register(m_PDH);

        DataLogManager.start();
        
        m_swerveSubsystem = new SwerveSubsystem();

        configureBindings();
        configureCommands();
    }

    private void configureCommands() {
        m_swerveSubsystem.setDefaultCommand(
            new SwerveDriveCommand(
                m_swerveSubsystem,
                () -> m_driverController.getLeftX(),
                () -> -m_driverController.getLeftY(),
                () -> m_driverController.getRightX()
            )
        );
    }

    private void configureBindings() {

        // When driver presses x, set wheels in x formation
        // m_driverController.x().onTrue(SwerveXCommand.xCommand(m_swerveSubsystem));

    }

    public Command getAutonomousCommand() {
        return Commands.print("No autonomous command configured");
    }
}
