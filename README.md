# Reefscape 2025 - FRC Robot Code

This repository contains the robot code for the 2025 FRC season game "Reefscape".

## 🤖 Robot Overview

This robot is designed to play the 2025 FRC game, featuring:
- **Swerve Drive**: Advanced drivetrain for omnidirectional movement
- **Elevator System**: Vertical positioning for game piece placement
- **Arm Mechanism**: Precise angle control for scoring
- **Grabber**: Intake system for both Coral and Algae game pieces
- **Climb System**: End-game climbing mechanism
- **Vision**: AprilTag-based field positioning

## 🚀 Quick Start

### Prerequisites
- WPILib 2025 (or latest)
- Java JDK 17+
- VS Code with WPILib extension

### Building and Deploying
```bash
# Build the project
./gradlew build

# Deploy to robot
./gradlew deploy

# Run simulation
./gradlew simulateJava
```

## 📁 Project Structure

```
src/main/java/frc/robot/
├── Robot.java                  # Main robot class
├── RobotContainer.java         # Robot subsystem container
├── constants/                  # All robot constants organized by category
├── config/                     # Motor and subsystem configurations
├── subsystems/                 # Robot subsystems
│   ├── arm/                    # Arm subsystem
│   ├── climb/                  # Climb mechanism
│   ├── elevator/               # Elevator system
│   ├── grabber/                # Game piece intake
│   ├── swerve/                 # Swerve drivetrain
│   ├── led/                    # LED indicators
│   └── vision/                 # Vision processing
├── commands/                   # Command-based logic
│   ├── auto/                   # Autonomous commands
│   ├── teleop/                 # Teleoperated commands
│   └── groups/                 # Reusable command groups
├── controls/                   # Controller bindings
├── auto/                       # Autonomous routines and builders
├── game/                       # Game-specific logic
└── util/                       # Utility classes
```

## 🎮 Controls

See [CONTROLS.md](docs/CONTROLS.md) for detailed controller mappings.

**Driver (Xbox Controller 0)**
- Left Stick: Translation
- Right Stick: Rotation
- Bumpers: Speed modifiers
- A Button: Zero gyro

**Operator (Xbox Controller 1)**
- POV: Preset positions for scoring
- Triggers: Intake/Outtake
- Bumpers: Game piece type selection

## 🔧 Subsystems

### Swerve Drive
- 4-module swerve drivetrain using YAGSL library
- Field-oriented control with gyro correction
- PathPlanner integration for autonomous

### Elevator + Arm
- Coordinated motion for precise game piece placement
- Preset positions for all scoring levels
- Soft limits and collision detection

### Grabber
- Dual-mode intake for Coral and Algae
- Current-based game piece detection
- Time-of-flight sensors for piece confirmation

## 📊 Development

### Code Standards
- Use meaningful variable names
- Follow WPILib command-based programming paradigms
- Document public methods with JavaDoc
- Keep subsystems focused and independent

### Adding New Features
1. Create/modify subsystems in `subsystems/`
2. Add constants to appropriate files in `constants/`
3. Create commands in `commands/`
4. Register commands in RobotContainer or AutoBuilder
5. Update documentation

### Tuning Constants
All tunable constants are organized in the `constants/` package:
- **Subsystem constants**: PID values, physical dimensions, limits
- **Port mappings**: CAN IDs, DIO ports
- **Operator constants**: Controller sensitivity, deadbands

## 🏁 Autonomous

Autonomous routines use PathPlanner for path following. Paths are stored in `src/main/deploy/pathplanner/`.

Named commands are registered in `auto/AutoCommands.java` and can be triggered from PathPlanner event markers.

## 🧪 Testing

```bash
# Run unit tests
./gradlew test

# Run with robot simulation
./gradlew simulateJava
```

## 📝 License

This project is licensed under the WPILib BSD License - see [WPILib-License.md](WPILib-License.md)

## 🤝 Contributing

See [CONTRIBUTING.md](docs/CONTRIBUTING.md) for contribution guidelines.

## 📞 Team Contact

FRC Team: [Your Team Number]
