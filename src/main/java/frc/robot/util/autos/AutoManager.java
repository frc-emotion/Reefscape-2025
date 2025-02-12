package frc.robot.util.autos;

import java.io.IOException;
import java.util.List;

import org.json.simple.parser.ParseException;

import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.commands.FollowPathCommand;
import com.pathplanner.lib.commands.PathPlannerAuto;
import com.pathplanner.lib.path.PathPlannerPath;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.util.autos.positions.HumanPlayerPosition;
import frc.robot.util.autos.positions.Position;
import frc.robot.util.autos.positions.StartPosition;
import frc.robot.util.autos.tasks.*;

public class AutoManager {
    
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

            return new SequentialCommandGroup(
                AutoBuilder.followPath(pickupPath),
                // insert pickup command
                AutoBuilder.followPath(scorePath)
                // insert score command
            );
            
        } else if(task instanceof ScoreAlgae) {
            ScoreAlgae algaeTask = (ScoreAlgae) task;
            PathPlannerPath pickupPath = getPath(lastPosition, algaeTask.position);
            PathPlannerPath scorePath = getPath(algaeTask.position, algaeTask.scorePosition);
            PathPlannerPath returnPath = getPath(algaeTask.scorePosition, humanPlayerPosition);

            if(pickupPath == null || scorePath == null || returnPath == null) {
                // TODO: Maybe log an alert here 
                return null;
            }

            return new SequentialCommandGroup(
                AutoBuilder.followPath(pickupPath),
                // insert pickup command
                AutoBuilder.followPath(scorePath),
                // insert score command
                AutoBuilder.followPath(returnPath)
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
}
