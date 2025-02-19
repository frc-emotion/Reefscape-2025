package frc.robot.util;

import java.io.IOException;
import java.util.List;

import org.json.simple.parser.ParseException;

import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.commands.FollowPathCommand;
import com.pathplanner.lib.commands.PathPlannerAuto;
import com.pathplanner.lib.path.EventMarker;
import com.pathplanner.lib.path.PathPlannerPath;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.ParallelDeadlineGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.commands.functional.MainCommandFactory;
import frc.robot.subsystems.arm.ArmSubsystem;
import frc.robot.subsystems.elevator.ElevatorSubsystem;
import frc.robot.subsystems.grabber.GrabberSubsystem;
import frc.robot.util.tasks.*;
import frc.robot.util.tasks.auto.ScoreAlgae;
import frc.robot.util.tasks.general.ScoreCoral;
import frc.robot.util.tasks.positions.HumanPlayerPosition;
import frc.robot.util.tasks.positions.Position;
import frc.robot.util.tasks.positions.StartPosition;
import frc.robot.util.tasks.teleop.PickupCoral;

public class AutoManager {
    private GrabberSubsystem m_GrabberSubsystem;
    private ElevatorSubsystem m_ElevatorSubsystem;
    private ArmSubsystem m_ArmSubsystem;

    public AutoManager(GrabberSubsystem grabberSubsystem, ElevatorSubsystem elevatorSubsystem, ArmSubsystem armSubsystem) {
        m_GrabberSubsystem = grabberSubsystem;
        m_ElevatorSubsystem = elevatorSubsystem;
        m_ArmSubsystem = armSubsystem;
    }
    
    public Command createAuto(StartPosition startPosition, HumanPlayerPosition humanPlayerPosition, List<Task> tasks) {
        SequentialCommandGroup autoCommandGroup = new SequentialCommandGroup();

        for(Task autoTask : tasks) {
            autoCommandGroup.addCommands(getCommandFromTask(startPosition, humanPlayerPosition, autoTask));
        }

        return autoCommandGroup;
    }

    private Command getCommandFromTask(Position lastPosition, HumanPlayerPosition humanPlayerPosition, Task task) {
        if(task instanceof ScoreCoral) {
            ScoreCoral coralTask = (ScoreCoral) task;
            PathPlannerPath pickupPath = getPath(lastPosition, humanPlayerPosition);
            PathPlannerPath scorePath = getPath(humanPlayerPosition, coralTask.position);
            
            if(scorePath == null || pickupPath == null) {
                // TODO: Maybe log an alert here 
                return null;
            }

            if(scorePath.getEventMarkers().size() == 0) {
                scorePath.getEventMarkers().add(
                    new EventMarker(
                        "PrepScoreCoral", 
                        0.6,
                        MainCommandFactory.getIntakeCommand(
                            m_ArmSubsystem, 
                            m_ElevatorSubsystem, 
                            m_GrabberSubsystem,
                            new PickupCoral(humanPlayerPosition)
                        )
                    )
                );
                scorePath.getEventMarkers().add(
                    new EventMarker(
                        "Score Coral", 
                        1.0,
                        MainCommandFactory.getPlaceCommand(
                            m_ArmSubsystem, 
                            m_ElevatorSubsystem, 
                            m_GrabberSubsystem, 
                            task,
                            () -> true
                        )
                    )
                );
            }
            
            return new SequentialCommandGroup(
                new ParallelDeadlineGroup(
                    MainCommandFactory.getIntakeCommand(
                        m_ArmSubsystem, 
                        m_ElevatorSubsystem, 
                        m_GrabberSubsystem,
                        new PickupCoral(humanPlayerPosition)
                    ),
                    AutoBuilder.followPath(pickupPath)
                ),
                AutoBuilder.followPath(scorePath)
                
                // insert score command
            );
            
        } else if(task instanceof ScoreAlgae) {
            ScoreAlgae algaeTask = (ScoreAlgae) task;
            PathPlannerPath pickupPath = getPath(lastPosition, algaeTask.position);
            PathPlannerPath scorePath = getPath(algaeTask.position, algaeTask.scorePosition);

            if(pickupPath == null || scorePath == null) {
                // TODO: Maybe log an alert here 
                return null;
            }

            return new SequentialCommandGroup(
                AutoBuilder.followPath(pickupPath),
                // insert pickup command
                AutoBuilder.followPath(scorePath)
            );
        } else {
            // TODO: Maybe log an alert here
            return null;
        }
    }

    private PathPlannerPath getPath(Position lastPosition, Position nextPosition) {
        try {
            return PathPlannerPath.fromPathFile(lastPosition.getName() + "-" + nextPosition.getName());
        } catch(Exception ex) {
            ex.printStackTrace();
            return null;
        }
        
    }

    private EventMarker findEventMarker(List<EventMarker> markers, String eventName) {
        for(EventMarker marker : markers) {
            if(marker.triggerName().equals(eventName)) {
                return marker;
            }
        }
        return null;
    }
}
