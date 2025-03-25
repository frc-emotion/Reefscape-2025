// package frc.robot.subsystems.vision;

// import static edu.wpi.first.units.Units.Microseconds;
// import static edu.wpi.first.units.Units.Seconds;

// import java.util.ArrayList;
// import java.util.List;
// import java.util.Optional;

// import org.photonvision.EstimatedRobotPose;
// import org.photonvision.PhotonCamera;
// import org.photonvision.PhotonPoseEstimator;
// import org.photonvision.PhotonPoseEstimator.PoseStrategy;
// import org.photonvision.targeting.PhotonPipelineResult;
// import org.photonvision.targeting.PhotonTrackedTarget;

// import edu.wpi.first.apriltag.AprilTagFieldLayout;
// import edu.wpi.first.apriltag.AprilTagFields;
// import edu.wpi.first.math.Matrix;
// import edu.wpi.first.math.VecBuilder;
// import edu.wpi.first.math.geometry.Rotation3d;
// import edu.wpi.first.math.geometry.Transform3d;
// import edu.wpi.first.math.geometry.Translation3d;
// import edu.wpi.first.math.numbers.N1;
// import edu.wpi.first.math.numbers.N3;
// import edu.wpi.first.networktables.NetworkTablesJNI;
// import frc.robot.Constants.Ports.VisionConstants;
// import frc.robot.Robot;

// public class Vision {
//     private static final AprilTagFieldLayout APRIL_TAG_FIELD_LAYOUT = AprilTagFieldLayout.loadField(AprilTagFields.k2024Crescendo);

//     enum Cameras {
//         FRONT_RIGHT(
//                 "front-right-swerve",
//                 new Translation3d(0, 0, 0),
//                 new Rotation3d(0, 0, 0),
//                 APRIL_TAG_FIELD_LAYOUT,
//                 VecBuilder.fill(4, 4, 8),
//                 VecBuilder.fill(4, 4, 8)); //,
//         // FRONT_LEFT(
//         //         "front-left-swerve",
//         //         new Translation3d(0, 0, 0),
//         //         new Rotation3d(0, 0, 0),
//         //         VecBuilder.fill(4, 4, 8),
//         //         VecBuilder.fill(4, 4, 8)),
//         // BACK(
//         //         "limelight",
//         //         new Translation3d(0, 0, 0),
//         //         new Rotation3d(0, 0, 0),
//         //         VecBuilder.fill(4, 4, 8),
//         //         VecBuilder.fill(4, 4, 8));

//         public final PhotonCamera camera;

//         public final PhotonPoseEstimator poseEstimator;

//         private final Matrix<N3, N1> singleTagStdDevs, multiTagStdDevs;

//         private final Transform3d robotToCameraTransform;

//         private Matrix<N3, N1> currentStdDevs;

//         public List<PhotonPipelineResult> photonResults = new ArrayList<>();

//         private double lastReadTimestamp;

//         private Optional<EstimatedRobotPose> estimatedRobotPose;

//         Cameras(
//                 String name,
//                 Translation3d robotToCameraTranslation,
//                 Rotation3d robotToCameraRotation,
//                 AprilTagFieldLayout fieldLayout,
//                 Matrix<N3, N1> singleTagStdDevs,
//                 Matrix<N3, N1> multiTagStdDevs) {
//             camera = new PhotonCamera(name);

//             this.robotToCameraTransform = new Transform3d(robotToCameraTranslation, robotToCameraRotation);

//             poseEstimator = new PhotonPoseEstimator(fieldLayout, PoseStrategy.MULTI_TAG_PNP_ON_COPROCESSOR, robotToCameraTransform);
            
//             this.singleTagStdDevs = singleTagStdDevs;
//             this.multiTagStdDevs = multiTagStdDevs;

//             currentStdDevs = singleTagStdDevs;
//             lastReadTimestamp = 0;
//         }

//         private void updateUnreadResults() {
//             double mostRecentTimestamp = photonResults.isEmpty() ? 0.0 : photonResults.get(0).getTimestampSeconds();
//             double currentTimestamp = Microseconds.of(NetworkTablesJNI.now()).in(Seconds);
//             double debounceTime = VisionConstants.DEBOUNCE_TIME_SEC;

//             for (PhotonPipelineResult result : photonResults) {
//                 mostRecentTimestamp = Math.max(mostRecentTimestamp, result.getTimestampSeconds());
//             }

//             if ((photonResults.isEmpty() || (currentTimestamp - mostRecentTimestamp >= debounceTime)) && (currentTimestamp - lastReadTimestamp) >= debounceTime) {
//                 photonResults = camera.getAllUnreadResults();
//                 lastReadTimestamp = currentTimestamp;
//                 photonResults.sort((PhotonPipelineResult a, PhotonPipelineResult b) -> {
//                     return a.getTimestampSeconds() >= b.getTimestampSeconds() ? 1 : -1;
//                 });
//                 if (!photonResults.isEmpty()) {
//                     updateEstimatedPose();
//                 }
//             }
//         }

//         private void updateEstimatedPose() {
//             Optional<EstimatedRobotPose> visionEstimate = Optional.empty();
//             for(var result : photonResults) {
//                 visionEstimate = poseEstimator.update(result);
//                 updateStdDevs(visionEstimate, result.getTargets());
//             }
//             estimatedRobotPose = visionEstimate;
//         }

//         public void updateStdDevs(Optional<EstimatedRobotPose> estimatedPose, List<PhotonTrackedTarget> targets) {
//             if (estimatedPose.isEmpty()) {
//                 currentStdDevs = singleTagStdDevs;
//                 return;
//             }

//             int numTags = 0;
//             double avgDist = 0;

//             for (PhotonTrackedTarget target : targets) {
//                 double tagDist = getDistanceToTagMeters(estimatedPose, target);

//                 if (tagDist == -1)
//                     continue;

//                 numTags++;
//                 avgDist += tagDist;
//             }

//             if (numTags == 0) {
//                 currentStdDevs = singleTagStdDevs;
//                 return;
//             }

//             avgDist /= numTags;

//             if (numTags > 1) {
//                 currentStdDevs = multiTagStdDevs;
//             } else {
//                 currentStdDevs = singleTagStdDevs;
//             }

//             if (numTags == 1 && avgDist > VisionConstants.ACCURACY_LIMIT) {
//                 currentStdDevs = VecBuilder.fill(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
//             } else {
//                 currentStdDevs = currentStdDevs.times(1.0 + (avgDist * avgDist / 30));
//             }
//         }

//         public double getDistanceToTagMeters(Optional<EstimatedRobotPose> estimatedPose, PhotonTrackedTarget target) {
//             if (estimatedPose.isEmpty()) {
//                 return -1;
//             } else {
//                 var tagPose = poseEstimator.getFieldTags().getTagPose(target.getFiducialId());

//                 if (tagPose.isEmpty()) {
//                     return -1;
//                 }

//                 return tagPose.get().toPose2d().getTranslation()
//                         .getDistance(estimatedPose.get().estimatedPose.toPose2d().getTranslation());
//             }
//         }
//     }
// }