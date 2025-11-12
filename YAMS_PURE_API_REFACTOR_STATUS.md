# 🔄 YAMS Pure API Refactor - In Progress

**Started:** November 12, 2025  
**Goal:** Remove ALL backwards compatibility and use pure YAMS API throughout codebase

---

## ✅ **Completed** (60 errors remaining, down from 76)

### **1. Subsystems - Backwards Compatibility Removed** ✅

#### **ArmSubsystem**
**Removed methods:**
- ❌ `stop()`
- ❌ `setTargetAngle(Rotation2d)`  
- ❌ `setTargetAngle(Rotation2d, Distance)`
- ❌ `setWithFeedforward(double)`
- ❌ `getRotation()`
- ❌ `isAtSetpoint()`

**Kept pure YAMS API:**
- ✅ `armCmd(double)` - Returns YAMS Command
- ✅ `setAngle(Angle)` - Returns YAMS Command
- ✅ `sysId()` - Returns YAMS Command
- ✅ `getArm()` - Returns YAMS Arm object

#### **ElevatorSubsystem**
**Removed methods:**
- ❌ `stop()`
- ❌ `set(double)`
- ❌ `setTargetHeight(Distance)`
- ❌ `isAtSetpoint()`
- ❌ `getHeight()`
- ❌ `getCurrentDraw(boolean)`
- ❌ `setWithFeedforward(double)`
- ❌ `resetSensorPosition(Distance)`

**Kept pure YAMS API:**
- ✅ `elevCmd(double)` - Returns YAMS Command
- ✅ `setHeight(Distance)` - Returns YAMS Command
- ✅ `sysId()` - Returns YAMS Command
- ✅ `getElevator()` - Returns YAMS Elevator object
- ✅ `getMotorCurrent()` - Direct motor access

---

### **2. Commands Refactored to Pure YAMS** ✅

#### **Arm Commands**
1. ✅ **MoveArmToAngle.java** - Wraps YAMS `setAngle()` Command
   - Removed elevator height dependency
   - Removed `finishWhenAtSetpoint` parameter
   - Now delegates to YAMS Command lifecycle

2. ✅ **MoveArmPosition.java** - Wraps YAMS `setAngle()` Command
   - Simplified to single constructor
   - Removed elevation dependency
   - Delegates to YAMS Command

3. ✅ **ArmAssistedCommand.java** - Uses YAMS `armCmd()`
   - Manual control via YAMS duty cycle commands
   - Properly schedules stop command on end

4. ✅ **ArmManualCommand.java** (both versions) - Uses YAMS `armCmd()`
   - Manual joystick control
   - Uses YAMS Command pattern

#### **Elevator Commands**
1. ✅ **MoveElevatorToHeight.java** - Wraps YAMS `setHeight()` Command
   - Removed `finishWhenAtSetpoint` parameter
   - Delegates to YAMS Command lifecycle

2. ✅ **MoveElevatorPosition.java** - Wraps YAMS `setHeight()` Command
   - Simplified to single constructor
   - Delegates to YAMS Command

3. ✅ **ElevatorManualCommand.java** - Uses YAMS `elevCmd()`
   - Manual control via YAMS duty cycle commands

4. ✅ **MoveElevatorManual.java** - Uses YAMS `elevCmd()`
   - Manual joystick control

5. ✅ **ZeroElevatorCurrent.java** - Uses YAMS `elevCmd()`
   - Removed `resetSensorPosition()` call (YAMS handles internally)
   - Uses `getMotorCurrent()` instead of `getCurrentDraw()`

---

## ⚠️ **Remaining Work** (60 errors)

### **Files Still Needing Updates:**

#### **1. SuperstructureStateMachine.java** (4 errors)
- Line 139: Uses `isAtSetpoint()` on both arm and elevator
- Lines 375-376: Uses `stop()` on both arm and elevator

**Fix needed:** Remove setpoint checks or use YAMS's internal state management

#### **2. MainCommandFactory.java** (~15 errors)
- Multiple constructor calls with old signatures
- Uses `MoveArmPosition(arm, angle, elevatorHeightSupplier)`
- Uses `MoveArmPosition(arm, angle, elevatorHeightSupplier, boolean)`
- Uses `MoveArmToAngle(arm, angle, elevatorHeightSupplier, boolean)`
- Line 198: Uses `getCurrentDraw(boolean)`

**Fix needed:** Update all constructor calls to new signatures

#### **3. Macro Sequences** (~20 errors)
Files with old constructor signatures:
- `ScoreCoralSequence.java`
- `ScoreAlgaeSequence.java`  
- `IntakeCoralSequence.java`
- `IntakeAlgaeSequence.java`
- `ExecuteClimbSequence.java`
- `PrepareClimbSequence.java`

All use:
- `MoveArmToAngle(arm, angle, elevatorHeightSupplier, boolean)`
- `MoveElevatorToHeight(elevator, height, boolean)`

**Fix needed:** Remove elevator height suppliers and boolean finish parameters

---

## 📝 **API Migration Pattern**

### **Old Pattern (Backwards Compat):**
```java
// Old - void methods
armSubsystem.setTargetAngle(angle);
armSubsystem.stop();
boolean done = armSubsystem.isAtSetpoint();
```

### **New Pattern (Pure YAMS):**
```java
// New - Command-returning methods
Command moveCmd = armSubsystem.setAngle(Degrees.of(angle.getDegrees()));
Command stopCmd = armSubsystem.armCmd(0);
// YAMS Commands handle completion internally via isFinished()
```

### **Wrapper Command Pattern:**
```java
public class MoveArmToAngle extends Command {
    private final Command yamsCommand;
    
    public MoveArmToAngle(ArmSubsystem arm, Rotation2d angle) {
        this.yamsCommand = arm.setAngle(Degrees.of(angle.getDegrees()));
        addRequirements(arm);
    }
    
    public void initialize() { yamsCommand.initialize(); }
    public void execute() { yamsCommand.execute(); }
    public boolean isFinished() { return yamsCommand.isFinished(); }
    public void end(boolean interrupted) { yamsCommand.end(interrupted); }
}
```

---

## 🎯 **Next Steps**

1. **Update SuperstructureStateMachine**
   - Remove `isAtSetpoint()` checks
   - Replace `stop()` calls with `armCmd(0)` and `elevCmd(0)`

2. **Update MainCommandFactory**
   - Fix all constructor calls to match new signatures
   - Remove elevator height suppliers
   - Use `getMotorCurrent()` instead of `getCurrentDraw(boolean)`

3. **Update All Macro Sequences**
   - Remove `finishWhenAtSetpoint` boolean parameters
   - Remove elevator height supplier dependencies
   - Use simple constructors

4. **Final Build Test**
   - Verify 0 compilation errors
   - Test that YAMS Commands work correctly

---

## 💡 **Key Insights**

1. **YAMS manages state internally** - No need for `isAtSetpoint()` checks
2. **Commands, not void methods** - All YAMS control returns Command objects
3. **Simpler signatures** - Removed elevation dependencies and finish flags
4. **Proper Command lifecycle** - Initialize, execute, isFinished, end pattern
5. **Manual control** - Use `armCmd(dutyCycle)` and `elevCmd(dutyCycle)`

---

**Progress:** ~45% complete (20/45 files refactored)
