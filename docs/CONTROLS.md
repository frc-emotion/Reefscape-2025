# Controller Mappings

This document describes all controller mappings for both driver and operator.

## Driver Controller (Port 0)

### Movement
| Control | Function | Notes |
|---------|----------|-------|
| Left Stick Y | Forward/Backward translation | Inverted, field-relative |
| Left Stick X | Left/Right translation | Inverted, field-relative |
| Right Stick X | Rotation | Field-relative angular velocity |

### Speed Modifiers
| Button | Speed | Use Case |
|--------|-------|----------|
| Default | 50% | Normal operation |
| Left Bumper | 35% | Precise positioning |
| Right Bumper | 80% | Fast repositioning |

### Utility
| Button | Function | Notes |
|--------|----------|-------|
| A | Zero Gyro | Resets field-relative heading |
| X | Lock Wheels | Forms X-pattern to resist pushing |
| B | Drive to Pose | Test function - drives to (4,4) |
| Start | (Test Mode) Zero Gyro | |
| Back | (Test Mode) Center Modules | |

## Operator Controller (Port 1)

### Scoring Positions (POV/D-Pad)
| Direction | Function | Height/Angle |
|-----------|----------|--------------|
| POV Up | L4 Coral Position | Highest scoring position |
| POV Right | L3 Coral Position | Third level |
| POV Left | L2 Coral Position | Second level |
| POV Down | Intake Position | Ground/coral station pickup |

### Algae Positions
| Button | Function | Notes |
|--------|----------|-------|
| X | L2 Algae Cleaning | Algae from reef level 2 |
| Y | L3 Algae Cleaning | Algae from reef level 3 |

### Manual Control
| Control | Function | Notes |
|---------|----------|-------|
| Left Stick Y | Manual Arm Control | Squared input for fine control |
| Right Stick Y | Manual Elevator Control | Linear control |

### Intake/Outtake
| Control | Function | Speed/Mode |
|---------|----------|------------|
| Left Trigger | Intake/Grab | Mode depends on selected game piece type |
| Right Trigger | Place/Outtake | Releases game piece |
| Left Bumper | Select Algae Mode | Sets grabber to algae intake mode |
| Right Bumper | Select Coral Mode | Sets grabber to coral intake mode |

### Climb
| Button | Function | Notes |
|--------|----------|-------|
| A | Extend Climb | Manually extend climb mechanism |
| B | Retract Climb | Manually retract climb mechanism |

## Field-Relative vs Robot-Relative

**Field-Relative (Default)**: The robot moves relative to the field. Forward on the controller is always away from your alliance wall, regardless of robot orientation.

**Robot-Relative**: The robot moves relative to its own orientation. Forward on the controller moves in the direction the robot is facing.

## Speed Scaling

All translation inputs use the following scaling:
- **Deadband**: 10% (ignores small stick movements)
- **Slow Mode**: 35% max speed
- **Normal Mode**: 50% max speed  
- **Turbo Mode**: 80% max speed

Rotation uses a turn constant of 6 for appropriate angular velocity.

## Simulation Controls

When running in simulation:
- Driver uses keyboard input mode
- Right trigger axis controls rotation
- Standard movement on WASD (when configured)

## Emergency Stop

**To disable the robot**: Press the space bar on the Driver Station or use the physical E-stop on the robot.

## Auto-Zero Features

The robot will automatically zero the gyro:
- At the start of autonomous mode
- At the start of teleop mode
- When manually triggered with the A button

## Notes for Drivers

1. **Practice field-relative driving** - it's faster once you're used to it
2. **Use slow mode** for precise scoring
3. **Zero the gyro** if the robot feels disoriented
4. **Lock wheels** when defending or being pushed
5. **Communicate with operator** about game piece selection

## Notes for Operators

1. **Select game piece type first** (bumpers) before intaking
2. **POV positions are presets** - use manual control for fine adjustments
3. **Watch for current spikes** - indicates successful grab
4. **Manual control overrides presets** - helps if you miss the target
5. **Coordinate with driver** for intake timing
