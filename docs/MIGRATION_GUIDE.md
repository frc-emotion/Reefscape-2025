# Migration Guide - 2025 Code Refactor

This guide helps you migrate existing code to the new refactored structure.

## Package Structure Changes

### Constants
**Old Location:** `frc.robot.Constants.*`  
**New Location:** `frc.robot.constants.*` and `frc.robot.constants.subsystems.*`

```java
// OLD
import frc.robot.Constants.ArmConstants;
import frc.robot.Constants.ElevatorConstants;
import frc.robot.Constants.Ports;

// NEW
import frc.robot.constants.subsystems.ArmConstants;
import frc.robot.constants.subsystems.ElevatorConstants;
import frc.robot.constants.PortMap;
```

**Port Access Changes:**
```java
// OLD
Constants.Ports.CANID.ARM_ANGLE.getId()
Constants.Ports.DIO.ALGAE_BEAM_BREAK.getChannel()

// NEW
PortMap.CANID.ARM_ANGLE.getId()
PortMap.DIO.ALGAE_BEAM_BREAK.getChannel()
```

### Motor Configurations
**Old Location:** `frc.robot.util.Configs.*`  
**New Location:** `frc.robot.config.subsystems.*`

```java
// OLD
import frc.robot.util.Configs.ArmConfigs;
import frc.robot.util.Configs.ElevatorConfigs;

// NEW
import frc.robot.config.subsystems.ArmConfig;
import frc.robot.config.subsystems.ElevatorConfig;

// Usage Change
// OLD: Configs.ArmConfigs.ARM_CONFIG
// NEW: ArmConfig.ARM_CONFIG
```

### Game Elements and Tasks
**Old Location:** `frc.robot.util.tasks.*`  
**New Location:** `frc.robot.game.*`

```java
// OLD
import frc.robot.util.tasks.Task;
import frc.robot.util.tasks.general.CoralLevel;
import frc.robot.util.tasks.general.ScoreCoral;
import frc.robot.util.tasks.teleop.PickupCoral;
import frc.robot.util.tasks.positions.Position;

// NEW
import frc.robot.game.Task;
import frc.robot.game.GameElement.CoralLevel;
import frc.robot.game.tasks.ScoreCoral;
import frc.robot.game.tasks.PickupCoral;
import frc.robot.game.field.Position;
```

### Autonomous Logic
**Old Location:** `frc.robot.util.AutoManager`  
**New Location:** `frc.robot.auto.AutoManager`

```java
// OLD
import frc.robot.util.AutoManager;

// NEW
import frc.robot.auto.AutoManager;
```

### Utility Classes
**Old Location:** `frc.robot.util.*`  
**New Locations:** Organized by category

```java
// Diagnostics/Fault Management
// OLD: import frc.robot.util.Faults.FaultManager;
// NEW: import frc.robot.util.diagnostics.Faults.FaultManager;

// UI/Dashboard
// OLD: import frc.robot.util.TabManager;
// NEW: import frc.robot.util.ui.TabManager;

// Helper Utilities
// OLD: import frc.robot.util.PIDHelper;
// NEW: import frc.robot.util.helpers.PIDHelper;

// OLD: import frc.robot.util.UnitsUtil;
// NEW: import frc.robot.util.helpers.UnitsUtil;
```

## Quick Migration Checklist

### For Each File You're Working On:

1. **Update constant imports:**
   - [ ] Replace `Constants.ArmConstants` → `ArmConstants` (from `constants.subsystems`)
   - [ ] Replace `Constants.Ports.CANID` → `PortMap.CANID`
   - [ ] Replace `Constants.Ports.DIO` → `PortMap.DIO`

2. **Update config imports:**
   - [ ] Replace `Configs.ArmConfigs` → `ArmConfig`
   - [ ] Replace `Configs.ElevatorConfigs` → `ElevatorConfig`
   - [ ] Replace `Configs.GrabberConfigs` → `GrabberConfig`
   - [ ] Replace `Configs.ClimbConfigs` → `ClimbConfig`

3. **Update task/game imports:**
   - [ ] Replace `frc.robot.util.tasks.Task` → `frc.robot.game.Task`
   - [ ] Replace `frc.robot.util.tasks.general.*` → `frc.robot.game.tasks.*`
   - [ ] Replace `frc.robot.util.tasks.teleop.*` → `frc.robot.game.tasks.*`
   - [ ] Replace `frc.robot.util.tasks.positions.*` → `frc.robot.game.field.*`

4. **Update util imports:**
   - [ ] Check if utilities moved to `util.diagnostics`, `util.ui`, or `util.helpers`

5. **Update auto imports:**
   - [ ] Replace `frc.robot.util.AutoManager` → `frc.robot.auto.AutoManager`

## Backward Compatibility

The old `Constants` and `Configs` classes are still available but deprecated. They act as facades to the new structure, so existing code will continue to work with deprecation warnings until you migrate.

**Example of deprecated access (still works but warned):**
```java
// This still works but shows deprecation warnings
double kP = Constants.ArmConstants.kP;  // ⚠️ Deprecated

// Migrate to:
double kP = ArmConstants.kP;  // ✅ Current
```

## IDE Auto-Fix

Most modern Java IDEs can help with migration:

1. **IntelliJ IDEA / VS Code:**
   - Deprecation warnings will appear with yellow underlines
   - Press `Alt+Enter` (or `Cmd+.` on Mac) on deprecated imports
   - Select "Replace with..." to auto-fix

2. **Organize Imports:**
   - IntelliJ: `Ctrl+Alt+O` (Windows/Linux) or `Cmd+Option+O` (Mac)
   - VS Code: `Shift+Alt+O`

## Common Patterns

### Pattern 1: Subsystem Constructor
```java
// OLD
import frc.robot.Constants.Ports;
import frc.robot.util.Configs.ArmConfigs;

public ArmSubsystem() {
    motor = new SparkMax(Ports.CANID.ARM_ANGLE.getId(), MotorType.kBrushless);
    motor.configure(ArmConfigs.ARM_CONFIG, ...);
}

// NEW
import frc.robot.constants.PortMap;
import frc.robot.config.subsystems.ArmConfig;

public ArmSubsystem() {
    motor = new SparkMax(PortMap.CANID.ARM_ANGLE.getId(), MotorType.kBrushless);
    motor.configure(ArmConfig.ARM_CONFIG, ...);
}
```

### Pattern 2: Command Factory
```java
// OLD
import frc.robot.Constants.ElevatorConstants;
import frc.robot.util.tasks.general.ScoreCoral;
import frc.robot.util.tasks.general.CoralLevel;

Distance height = ElevatorConstants.CORAL_L1_HEIGHT;
ScoreCoral task = new ScoreCoral(CoralLevel.L1);

// NEW
import frc.robot.constants.subsystems.ElevatorConstants;
import frc.robot.game.tasks.ScoreCoral;
import frc.robot.game.GameElement.CoralLevel;

Distance height = ElevatorConstants.CORAL_L1_HEIGHT;
ScoreCoral task = new ScoreCoral(CoralLevel.L1);
```

### Pattern 3: Autonomous Setup
```java
// OLD
import frc.robot.util.AutoManager;
import frc.robot.util.tasks.positions.StartPosition;

AutoManager autoManager = new AutoManager(...);
Command auto = autoManager.createAuto(StartPosition.S1, ...);

// NEW
import frc.robot.auto.AutoManager;
import frc.robot.game.field.StartPosition;

AutoManager autoManager = new AutoManager(...);
Command auto = autoManager.createAuto(StartPosition.S1, ...);
```

## Testing After Migration

After migrating a file:

1. **Build the project** to ensure no compilation errors
2. **Run in simulation** if available
3. **Test on practice robot** before deploying to competition robot

## Questions?

If you encounter issues during migration:
- Check this guide for the correct import
- Look at already-migrated files (like `MainCommandFactory.java`) for examples
- The old classes are still there for reference (though deprecated)
