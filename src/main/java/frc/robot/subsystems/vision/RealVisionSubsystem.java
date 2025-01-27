package frc.robot.subsystems.vision;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.opencv.aruco.EstimateParameters;
import org.opencv.photo.Photo;
import org.photonvision.EstimatedRobotPose;
import org.photonvision.PhotonCamera;
import org.photonvision.PhotonPoseEstimator;
import org.photonvision.PhotonPoseEstimator.PoseStrategy;

import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.apriltag.AprilTagFields;
import frc.robot.util.Constants.VisionConstants;

public class RealVisionSubsystem implements VisionSubsystem {
    private final PhotonCamera frontRightCamera, frontLeftCamera, backCamera;

    private final PhotonPoseEstimator frontRightPoseEstimator, frontLeftPoseEstimator, backPoseEstimator;

    private EstimatedRobotPose[] lastPoses;

    public RealVisionSubsystem() {
        frontRightCamera = new PhotonCamera("frontRightCamera");
        frontLeftCamera = new PhotonCamera("frontLeftCamera");
        backCamera = new PhotonCamera("backLeftCamera");
        
            // TODO: Update field layouts
        frontRightPoseEstimator = new PhotonPoseEstimator(AprilTagFieldLayout.loadField(AprilTagFields.k2024Crescendo), PoseStrategy.MULTI_TAG_PNP_ON_COPROCESSOR, VisionConstants.kFrontRightTransform);
        frontLeftPoseEstimator = new PhotonPoseEstimator(AprilTagFieldLayout.loadField(AprilTagFields.k2024Crescendo), PoseStrategy.MULTI_TAG_PNP_ON_COPROCESSOR, VisionConstants.kFrontLeftTransform);
        backPoseEstimator = new PhotonPoseEstimator(AprilTagFieldLayout.loadField(AprilTagFields.k2024Crescendo), PoseStrategy.MULTI_TAG_PNP_ON_COPROCESSOR, VisionConstants.kBackTransform);

        lastPoses = new EstimatedRobotPose[3];
    }

    @Override
    public ArrayList<EstimatedRobotPose> getEstimatedPoses() {
        ArrayList<EstimatedRobotPose> poses = new ArrayList<>();
        
        poses.addAll(getEstimatedPoses(frontRightPoseEstimator, frontRightCamera));
        poses.addAll(getEstimatedPoses(frontLeftPoseEstimator, frontLeftCamera));
        poses.addAll(getEstimatedPoses(backPoseEstimator, backCamera));

        return poses;
    }

    private static List<EstimatedRobotPose> getEstimatedPoses(PhotonPoseEstimator estimator, PhotonCamera camera) {
        var results = camera.getAllUnreadResults();

        ArrayList<EstimatedRobotPose> estimatedRobotPoses = new ArrayList<>(results.size());

        for(var result : results) {
            var estimate = estimator.update(result);
            if(estimate.isPresent()) {
                estimatedRobotPoses.add(estimate.get());
            }
        }

        estimatedRobotPoses.trimToSize();
        return estimatedRobotPoses;
    }
}
