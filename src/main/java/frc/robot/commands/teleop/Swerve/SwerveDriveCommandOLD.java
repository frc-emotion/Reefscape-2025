// package frc.robot.commands.teleop.Swerve;

// import java.util.function.Supplier;

// import edu.wpi.first.epilogue.Logged;
// import edu.wpi.first.math.filter.SlewRateLimiter;
// import edu.wpi.first.math.kinematics.ChassisSpeeds;
// import edu.wpi.first.units.measure.*;
// import edu.wpi.first.wpilibj.DriverStation;
// import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
// import edu.wpi.first.wpilibj2.command.Command;
// import frc.robot.subsystems.swerve.SwerveSubsystemOLD;
// import frc.robot.util.UnitsUtil;
// import frc.robot.util.Constants.DriveConstants;
// import frc.robot.util.Constants.IOConstants;

// public class SwerveDriveCommandOLD extends Command {
//     private SwerveSubsystemOLD swerveDrive;

//     private final Supplier<Double> xSupplier, ySupplier, thetaSupplier;

//     /*
//      * Limiters should be replaced by a single limiter that limits based on the
//      * acceleration of each of the modules, rather than the directions the robot can move.
//      */
//     private final SlewRateLimiter xLimiter, yLimiter, thetaLimiter;
//     @Logged
//     private double maxSpeed, maxAngularSpeed;

//     public SwerveDriveCommandOLD(
//         SwerveSubsystemOLD drive, 
//         Supplier<Double> xSupplier, 
//         Supplier<Double> ySupplier, 
//         Supplier<Double> thetaSupplier
//     ) {
//         this(
//             drive, xSupplier, ySupplier, thetaSupplier, 
//             DriveConstants.kNormalDriveAcceleration, 
//             DriveConstants.kNormalDriveAngularAcceleration,
//             DriveConstants.kNormalDriveSpeed,
//             DriveConstants.kNormalDriveAngularSpeed
//         );
//     }

//     public SwerveDriveCommandOLD(
//         SwerveSubsystemOLD drive, 
//         Supplier<Double> xSupplier, 
//         Supplier<Double> ySupplier, 
//         Supplier<Double> thetaSupplier,
//         LinearAcceleration maxAcceleration,
//         AngularAcceleration maxAnglularAcceleration,
//         LinearVelocity maxVelocity,
//         AngularVelocity maxAngularVelocity
//     ) {
//         this.swerveDrive = drive;
//         this.xSupplier = xSupplier;
//         this.ySupplier = ySupplier;
//         this.thetaSupplier = thetaSupplier;

//         maxSpeed = UnitsUtil.convertToMPS(maxVelocity);
//         maxAngularSpeed = UnitsUtil.convertToDPS(maxAngularVelocity);

//         xLimiter = new SlewRateLimiter(UnitsUtil.convertToMPSS(maxAcceleration));
//         yLimiter = new SlewRateLimiter(UnitsUtil.convertToMPSS(maxAcceleration));
//         thetaLimiter = new SlewRateLimiter(UnitsUtil.convertToDPSS(maxAnglularAcceleration));

//         addRequirements(swerveDrive);
//     }

//     @Override
//     public void initialize() {
//         System.out.println("This ran");
//         swerveDrive.updateShuffleboard();
//         System.out.println("This completed");
//     }

//     @Override
//     public void execute() {
//         double x = xLimiter.calculate(
//             (Math.abs(xSupplier.get()) > IOConstants.DRIVER_DEADZONE ? xSupplier.get() : 0)
//         ) * maxSpeed;

//         double y = yLimiter.calculate(
//             (Math.abs(ySupplier.get()) > IOConstants.DRIVER_DEADZONE ? ySupplier.get() : 0) 
//         ) * maxSpeed;

//         // double theta = thetaLimiter.calculate(
//         //     (Math.abs(thetaSupplier.get()) > IOConstants.DRIVER_DEADZONE ? thetaSupplier.get() : 0) 
//         // ) * maxAngularSpeed;

//         double theta = (Math.abs(thetaSupplier.get()) > IOConstants.DRIVER_DEADZONE ? thetaSupplier.get() : 0) * maxAngularSpeed;

//         ChassisSpeeds speeds = new ChassisSpeeds(x, y, theta);

//         System.out.println("Swerve Speeds: " + x + " " + y + " " + theta);
        
//         swerveDrive.drive(speeds, true);

//     }
// }