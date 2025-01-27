// package frc.robot.commands.teleop.Swerve;

// import edu.wpi.first.math.geometry.Rotation2d;
// import edu.wpi.first.math.kinematics.SwerveModuleState;
// import edu.wpi.first.wpilibj2.command.Command;
// import edu.wpi.first.wpilibj2.command.Commands;
// import frc.robot.subsystems.swerve.SwerveSubsystemOLD;

// public class SwerveXCommandOLD extends Command{
//     public static Command xCommand(SwerveSubsystemOLD swerveSubsystem) {
//     return Commands.run(
//         () -> {
//             swerveSubsystem.setModuleStates(new SwerveModuleState[] {
//                 new SwerveModuleState(0, Rotation2d.fromDegrees(45)),
//                 new SwerveModuleState(0, Rotation2d.fromDegrees(-45)),
//                 new SwerveModuleState(0, Rotation2d.fromDegrees(-45)),
//                 new SwerveModuleState(0, Rotation2d.fromDegrees(45))}
//             );
//         });
//   }
// }
