package frc.robot.subsystems.vision;

import java.util.ArrayList;

import org.photonvision.EstimatedRobotPose;

import edu.wpi.first.wpilibj2.command.Subsystem;

public interface VisionSubsystem extends Subsystem {
    public ArrayList<EstimatedRobotPose> getEstimatedPoses();
}