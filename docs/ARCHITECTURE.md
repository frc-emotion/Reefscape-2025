# Architecture Overview

This document describes the high-level architecture of the 2025 Reefscape robot code.

## Package Structure

```
frc.robot/
├── auto/                    # Autonomous logic
│   └── AutoManager.java    # Builds autonomous command sequences
│
├── commands/               # Robot commands
│   ├── functional/        # High-level command factories
│   └── teleop/           # Teleop-specific commands
│       ├── arm/          # Arm commands
│       ├── Climb/        # Climb commands
│       ├── Elevator/     # Elevator commands
│       ├── Grabber/      # Grabber commands
│       └── Swerve/       # Drivetrain commands
│
├── config/                # Motor and hardware configurations
│   └── subsystems/       # Per-subsystem configs
│       ├── ArmConfig.java
│       ├── ClimbConfig.java
│       ├── ElevatorConfig.java
│       └── GrabberConfig.java
│
├── constants/            # Robot constants
│   ├── AutoConstants.java
│   ├── OperatorConstants.java
│   ├── PortMap.java      # CAN IDs and DIO ports
│   ├── RobotConstants.java
│   ├── VisionConstants.java
│   └── subsystems/       # Per-subsystem constants
│       ├── ArmConstants.java
│       ├── ClimbConstants.java
│       ├── DriveConstants.java
│       ├── ElevatorConstants.java
│       ├── GrabberConstants.java
│       └── LEDConstants.java
│
├── game/                 # Game-specific logic (2025 Reefscape)
│   ├── Task.java        # Base task class
│   ├── GameElement.java # Coral/Algae levels
│   ├── field/          # Field positions
│   │   ├── Position.java
│   │   ├── StartPosition.java
│   │   ├── HumanPlayerPosition.java
│   │   ├── CoralPosition.java
│   │   ├── AlgaePosition.java
│   │   └── AlgaeScorePosition.java
│   └── tasks/          # Game tasks
│       ├── PickupTask.java
│       ├── PickupCoral.java
│       ├── PickupAlgae.java
│       ├── ScoreCoral.java
│       └── ScoreAlgae.java
│
├── subsystems/          # Robot subsystems
│   ├── arm/
│   ├── climb/
│   ├── elevator/
│   ├── grabber/
│   ├── led/
│   ├── swervedrive/
│   └── vision/
│
├── util/                # Utility classes
│   ├── diagnostics/    # Fault management
│   │   └── Faults/
│   ├── helpers/        # Helper utilities
│   │   ├── PIDHelper.java
│   │   └── UnitsUtil.java
│   ├── ui/            # Dashboard/UI management
│   │   └── TabManager.java
│   ├── elasticlib/    # Third-party library
│   └── Configs.java   # [DEPRECATED] Old config facade
│
├── Constants.java       # [DEPRECATED] Old constants facade
├── Robot.java
└── RobotContainer.java
```

## Design Principles

### 1. Separation of Concerns
- **Constants**: Configuration values separated from logic
- **Config**: Hardware configuration separated from constants
- **Game Logic**: Game-specific code isolated in `game/` package
- **Commands**: Reusable command logic
- **Subsystems**: Hardware abstraction

### 2. Subsystem Organization
Each subsystem follows a consistent pattern:
- Constants in `constants/subsystems/[Subsystem]Constants.java`
- Motor config in `config/subsystems/[Subsystem]Config.java`
- Commands in `commands/teleop/[Subsystem]/`
- Subsystem class in `subsystems/[subsystem]/`

### 3. Command-Based Architecture
Following WPILib's command-based pattern:
- **Subsystems**: Manage hardware and state
- **Commands**: Encapsulate actions and behaviors
- **Command Groups**: Combine commands for complex behaviors
- **Triggers**: Bind commands to controller inputs

### 4. Game Abstraction
Game-specific logic lives in `frc.robot.game`:
- **Tasks**: High-level objectives (score coral, pickup algae)
- **Field Positions**: Enums for field locations
- **Game Elements**: Enums for coral/algae levels

This allows easy adaptation to future games by swapping the `game/` package.

## Data Flow

### Teleop Control Flow
```
Controller Input
    ↓
RobotContainer (button bindings)
    ↓
Command (teleop commands)
    ↓
Subsystem (hardware control)
    ↓
Motors/Sensors
```

### Autonomous Control Flow
```
Auto Selector (StartPosition, Tasks)
    ↓
AutoManager.createAuto()
    ↓
PathPlanner Paths + Event Markers
    ↓
Command Groups
    ↓
Subsystems
    ↓
Motors/Sensors
```

### Constant/Config Loading
```
Subsystem Constructor
    ↓
Load Config (from config.subsystems.*)
    ↓
Load Constants (from constants.subsystems.*)
    ↓
Configure Motors
    ↓
Ready for Commands
```

## Key Components

### MainCommandFactory
Central factory for creating complex command sequences:
- `getArmElevatorPositionCommand()` - Coordinated arm+elevator movement
- `getIntakeCommand()` - Pickup game pieces
- `getPlaceCommand()` - Score game pieces
- `getPrepClimbCommand()` / `getClimbCommand()` - Climbing sequences

### AutoManager
Builds autonomous routines:
- Takes list of Tasks and generates command sequence
- Integrates with PathPlanner for path following
- Adds event markers for actions along paths

### PortMap
Centralized port definitions:
- CAN IDs for all motor controllers
- DIO channels for sensors
- Eliminates magic numbers throughout code

## Conventions

### Naming
- **Classes**: PascalCase (e.g., `ArmConstants`)
- **Methods**: camelCase (e.g., `getIntakeCommand`)
- **Constants**: UPPER_SNAKE_CASE (e.g., `CORAL_L1_HEIGHT`)
- **Packages**: lowercase (e.g., `frc.robot.subsystems.arm`)

### File Organization
- One public class per file
- File name matches class name
- Package structure reflects logical grouping

### Documentation
- Javadoc for all public classes and methods
- Inline comments for complex logic
- README files in key directories

## Testing Strategy

1. **Simulation**: Test logic without hardware
2. **Practice Robot**: Validate on practice hardware
3. **Competition Robot**: Final validation before matches

## Future Improvements

Areas identified for potential enhancement:
- [ ] Extract RobotContainer button bindings to `controls/` package
- [ ] Rename command folders to lowercase for consistency
- [ ] Comprehensive unit tests for command logic
- [ ] State machine for game piece handling
- [ ] Advanced path planning with dynamic obstacles
