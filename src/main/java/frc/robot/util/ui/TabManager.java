package frc.robot.util.ui;

import edu.wpi.first.util.datalog.DataLog;
import edu.wpi.first.wpilibj.DataLogManager;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;


public class TabManager {

    private static TabManager instance;
    private SubsystemTab[] tabs;

    public enum SubsystemTab {
        DEFAULT,
        DRIVETRAIN,
        GAME,
        AUTO;
    }

    public TabManager() {
        SubsystemTab[] tabs = SubsystemTab.class.getEnumConstants();
        for (SubsystemTab tab : tabs) {
            Shuffleboard.getTab(tab.name());
        }
        this.tabs = tabs;
    }

    /**
     * Retrieves the singleton of the TabManager.
     * 
     * @return The instance of the TabManager.
     */
    public static synchronized TabManager getInstance() {
        if (instance == null) {
            instance = new TabManager();
        }
        return instance;
    }

    /**
     * Retrieves the ShuffleboardTab represented by a SubsystemTab
     * 
     * @param tab The SubsystemTab to be used
     * @return The ShuffleboardTab
     */
    public ShuffleboardTab accessTab(SubsystemTab tab) {
        return Shuffleboard.getTab(tab.name());
    }

    /**
     * Returns the SubsystemTab with the same name as given. Not case sensitive.
     * Throws an IllegalArgumentException if no such SubsystemTab exists.
     * 
     * @param name The name of the tab
     * @return The SubystemTab with a certain name
     * @throws IllegalArgumentException
     */
    public SubsystemTab getSubsystemTab(String name) throws IllegalArgumentException {
        return SubsystemTab.valueOf(name.toUpperCase().trim());
    }

    /**
     * Retrieves all the names of every Tab.
     * 
     * @return All of the names of the SubystemTabs.
     */
    public String[] getTabNames() {
        String[] names = new String[tabs.length];
        for (int i = 0; i < tabs.length; i++) {
            names[i] = tabs[i].name();
        }
        return names;
    }
    // public GenericEntry addWidget(
    // ShuffleboardTab tab,
    // BuiltInWidgets widgetType,
    // String name,
    // Object defaultValue,
    // int[] position,
    // int[] size
    // ) {
    // return tab
    // .add(name, defaultValue)
    // .withPosition(position[0], position[1])
    // .withSize(size[0], size[1])
    // .withWidget(widgetType)
    // .getEntry();
    // }

    // public ComplexWidget addFieldWidget(
    // ShuffleboardTab tab,
    // BuiltInWidgets widgetType,
    // String name,
    // Field2d defaultValue,
    // int[] position,
    // int[] size
    // ) {
    // return tab
    // .add(name, defaultValue)
    // .withPosition(position[0], position[1])
    // .withSize(size[0], size[1])
    // .withWidget(widgetType);
    // }

    /** Puts all NT Data into a wpilog */
    public void logDriveOdometry(boolean beginLog) {
        if (beginLog) {
            DataLogManager.start();
        } else {
            DataLog log = DataLogManager.getLog();
            System.out.println(log);
        }
    }
}
