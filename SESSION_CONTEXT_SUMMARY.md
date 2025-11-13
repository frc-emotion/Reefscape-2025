# Complete Session Context Summary
## ARM SysId, Tuning, and Gearing Configuration

**Date:** November 12, 2025  
**Project:** Reefscape 2025 FRC Robot  
**Main Goal:** Configure and tune arm mechanism using SysId for optimal autonomous performance

---

## 🎯 **Session Overview**

This session focused on:
1. Understanding and implementing SysId (System Identification) for the arm mechanism
2. Diagnosing and fixing gear ratio configuration issues
3. Creating comprehensive tuning guides and procedures
4. Setting up safe SysId practices to prevent damage during characterization

---

## 📸 **Robot Physical Configuration**

### **Arm Geometry (From Photos)**

The robot has an arm mechanism with the following positions:

```
    125° (Max Back/Up) - Safe scoring position
         |
      25-30° (Vertical) - ⭐ CRITICAL: Start SysId from here
         |
        0° (Hopper Rest) - Arm resting on hopper intake
         |
      -45° (Bumper) - ⚠️ DANGER ZONE - gravity pulls arm into bumper
```

**Key Physical Details:**
- **Chain drive system** (visible in photos, not direct gears)
- Small motor sprocket connects to large arm sprocket via chain
- Difficult to count teeth visually
- Heavy arm mechanism (combined with elevator)

### **Gravity Reversal Problem**

**Critical Safety Issue:**
- **Above vertical (25° to 125°):** Gravity pulls arm DOWN → motors must LIFT
- **At vertical (~25°):** Zero gravitational torque → stable equilibrium
- **Below vertical (0° to -45°):** Gravity ASSISTS falling → motors must RESIST → **WILL SLAM INTO BUMPER if not controlled!**

---

## ⚙️ **Gear Ratio Configuration Issue**

### **The Problem**

**Old code had conversion factor:**
```java
kConversionFactor = ((180.0 / 7.15) / 1.21915) * (90.0 / 93.415);
// Calculated value: ~19.89 degrees per motor rotation
// Implied gear ratio: 360° ÷ 19.89° = ~18:1
```

**Current code says:**
```java
// ArmConstants.java
GEAR_RATIO_STAGE_1 = 3.0;  // First reduction
GEAR_RATIO_STAGE_2 = 4.0;  // Second reduction
// Total: 3 × 4 = 12:1
```

**⚠️ CRITICAL MISMATCH:** 18:1 vs 12:1 = **50% error!**

### **What This Causes**

If actual gearing is 18:1 but code says 12:1:

```
Command arm to 90°:
- YAMS calculates: 3 motor rotations needed (based on 12:1)
- Actual result: 3 ÷ 18 = 0.167 rotations = 60°
- ERROR: 33% undershoot!
```

**Symptoms:**
- Position errors (commands 90°, goes to 60°)
- Soft limits trigger at wrong angles
- Velocity too slow (33% slower than expected)
- SysId data would be completely invalid
- PID tuning impossible

### **Solution Implemented**

Added debug code to `ArmSubsystem.java` to measure actual gear ratio:

```java
// Lines 159-167 in ArmSubsystem.java
double motorRotations = armMotor.getEncoder().getPosition();
double armDegrees = getCurrentAngle().getDegrees();
double armRotations = armDegrees / 360.0;
double measuredGearRatio = (armRotations != 0) ? motorRotations / armRotations : 0;

SmartDashboard.putNumber("ARM_DEBUG/MotorRotations", motorRotations);
SmartDashboard.putNumber("ARM_DEBUG/ArmDegrees", armDegrees);
SmartDashboard.putNumber("ARM_DEBUG/MeasuredGearRatio", measuredGearRatio);
```

**Usage:**
1. Deploy code
2. Manually rotate arm exactly 360° (full rotation)
3. Read `ARM_DEBUG/MeasuredGearRatio` from dashboard
4. Update `GEAR_RATIO_STAGE_1` in constants
5. Redeploy and verify

---

## 📋 **SysId Configuration**

### **What is SysId?**

SysId (System Identification) characterizes mechanism physics to find optimal feedforward constants.

**Measures:**
- **kS** - Static friction voltage (overcome stiction)
- **kG** - Gravity compensation voltage (hold against gravity)
- **kV** - Velocity feedforward (voltage per speed)
- **kA** - Acceleration feedforward (voltage for acceleration)

### **Why It's Critical**

**Current kG = 0.088858** → **TOO LOW!**

This causes:
- ❌ Arm won't move (not enough voltage to fight gravity)
- ❌ OR needs excessive voltage → overshoots (no compensation)
- ❌ Can't hold position (drifts down)

**Expected kG after SysId: 0.3 - 0.8** (for heavy arm)

### **SysId Button Bindings**

**Operator Controller:**
- **ARM SysId:** Hold Left Stick + D-Pad Up
- **ELEVATOR SysId:** Hold Left Stick + D-Pad Right

**Driver Controller:**
- **DRIVE Motors SysId:** Hold Left Bumper + Right Bumper + Y
- **ANGLE Motors SysId:** Hold Left Bumper + Right Bumper + X

**Safety:** Multi-button combos prevent accidental activation

---

## 🚨 **Safe SysId Procedure for Arm**

### **CRITICAL: Start Position**

**❌ WRONG - Don't start from 0° (hopper):**
- SysId will try to move negative (toward bumper)
- Past vertical, gravity reverses
- ARM SLAMS INTO BUMPER! 💥

**✅ CORRECT - Start from 25-30° (vertical):**
1. Manually drive arm to vertical position using joystick
2. Verify on dashboard: reads ~25-30 degrees
3. Arm should be stable (zero gravity torque)
4. NOW press SysId button combo
5. SysId tests range: 0° ← 25° → 125° (safe!)

### **Safety Features Implemented**

**Soft Limits (Line 99 in ArmSubsystem.java):**
```java
.withSoftLimits(Degrees.of(0), Degrees.of(ArmConstants.kMaxRotation))
```

- Prevents SysId from going past 0° into negative angles
- Stops arm before bumper danger zone
- Can be expanded after tuning is verified

**SysId Voltages (ArmConstants.java lines 101-106):**
```java
SYSID_STEP_VOLTAGE = 2V      // Reduced from 3V for safety
SYSID_RAMP_RATE = 1.5 V/s    // Reduced from 3.0 for safety  
SYSID_TIMEOUT = 10s          // Reduced from 30s
```

### **Complete SysId Procedure**

**Phase 1: Pre-Flight**
- Battery >12.5V
- Clear area around arm
- Soft limits configured (0° to 125°)
- WPILib SysId tool open and connected

**Phase 2: Position Arm**
- Enable robot teleop
- Manually move arm to 25-30° vertical
- Verify position on dashboard
- Arm should be stable

**Phase 3: Run Tests**
1. Quasistatic Forward (slow, 25° → 125°)
2. Quasistatic Reverse (slow, 25° → 0°, stops at soft limit)
3. Dynamic Forward (fast, 25° → 125°)
4. Dynamic Reverse (fast, 25° → 0°, stops at soft limit)

**Phase 4: Analyze & Apply**
1. Review data quality (R² > 0.95)
2. Copy kS, kG, kV, kA from SysId tool
3. Update ArmConstants.java lines 70-73
4. Deploy and verify arm holds position

---

## 🎛️ **PIDF Tuning Explained**

### **Feedforward (From SysId)**

**kS (Static Friction):**
- Voltage to overcome friction and "unstick" mechanism
- Typical: 0.1 - 0.3 V
- Current: 0 (why arm doesn't move!)

**kG (Gravity Compensation):** ← **MOST IMPORTANT**
- Voltage to hold arm at any angle
- Formula: `Torque = kG × cos(angle)`
- Typical for heavy arms: 0.3 - 0.8 V
- Current: 0.088858 (**WAY TOO LOW!**)
- **This is why arm either won't move or overshoots**

**kV (Velocity):**
- Voltage per degree/second of speed
- Typical: 0.001 - 0.01 V/(deg/s)
- Current: 0.00025 (probably okay)

**kA (Acceleration):**
- Voltage for acceleration
- Typical: 0.001 - 0.01 V/(deg/s²)
- Current: 0.001 (probably okay)

### **PID (Tuned After Feedforward is Correct)**

**kP (Proportional):**
- How aggressively to correct position error
- Start: 0.002
- Increase if arm is slow to target
- Decrease if arm oscillates
- Current: 0.002058 (good starting point)

**kI (Integral):**
- Corrects steady-state error over time
- Start: 0 (usually not needed!)
- Only add if arm never quite reaches target
- Can cause overshoot and instability
- Current: 0 (correct!)

**kD (Derivative):**
- Dampens oscillation
- Start: 0
- Add if arm oscillates even with low kP
- Typical: 0.0001 - 0.001
- Current: 0 (correct for now, add if needed after SysId)

### **Tuning Order (CRITICAL)**

```
1. Run SysId → Get kS, kG, kV, kA
2. Apply to code → Deploy
3. Test: Does arm HOLD position when released?
   NO → Adjust kG (increase if drifts down)
   YES → Feedforward correct! ✅
4. Test: Does arm reach commanded targets?
   Slow? → Increase kP
   Oscillates? → Decrease kP
5. Test: Does it overshoot?
   YES → Add kD (start 0.0001)
6. Test: Steady-state error?
   YES → Add tiny kI (0.00005)
7. DONE! ✅
```

---

## 🔧 **Gearing Concepts Explained**

### **What GearBox.fromReductionStages() Does**

```java
.withGearing(new MechanismGearing(GearBox.fromReductionStages(
    ArmConstants.GEAR_RATIO_STAGE_1,  // = 3.0
    ArmConstants.GEAR_RATIO_STAGE_2   // = 4.0
)))
```

**Tells YAMS:**
- Motor has 2 gear reduction stages: 3:1 then 4:1
- Total: 3 × 4 = 12:1 reduction
- **12 motor rotations = 1 arm rotation (360°)**

**YAMS automatically converts:**
- Commanded degrees → motor rotations
- Motor encoder position → arm degrees
- Arm velocity → motor velocity
- Torque multiplication (motor torque × 12 = arm torque)

**Example:**
```
Command arm to 30°:
1. YAMS calculates: 30° ÷ 360° = 0.0833 rotations
2. With 12:1 gearing: 0.0833 × 12 = 1 motor rotation
3. Motor rotates 1 full turn
4. Arm moves to 30° ✅
```

### **Measuring Gear Ratio (Software Method)**

**Easiest approach (no counting teeth):**

1. Deploy code with debug (already added to ArmSubsystem.java)
2. Enable robot
3. Put arm at 0° (hopper) and reset encoder OR note starting position
4. Manually rotate arm exactly 360° using joystick
5. Read `ARM_DEBUG/MeasuredGearRatio` from dashboard
6. That number is your ACTUAL gear ratio!
7. Update constants:
```java
GEAR_RATIO_STAGE_1 = [measured_value];  // e.g., 18.0
GEAR_RATIO_STAGE_2 = 1.0;
```

**Alternative (if can't do full rotation):**
1. Command arm to specific angle (e.g., 90°)
2. Measure ACTUAL angle with phone level app
3. Calculate: `Real Ratio = Current Ratio × (Commanded / Actual)`
4. Example: Current 12:1, commanded 90°, actual 60°
   - Real = 12 × (90/60) = 18:1

---

## 📚 **Documentation Created**

### **Files Created This Session**

1. **`COMPLETE_ARM_TUNING_GUIDE.md`** (20+ pages)
   - Photo-based arm geometry explanation
   - Complete SysId procedure
   - PIDF tuning flowchart
   - Troubleshooting guide
   - Data recording sheet

2. **`ARM_SYSID_SAFE_PROCEDURE.md`**
   - Detailed safety procedure
   - Gravity reversal explanation
   - Safe starting position guide

3. **`HEAVY_MECHANISM_TUNING_GUIDE.md`**
   - General heavy mechanism tuning theory
   - Applies to both arm and elevator

4. **`DRIVETRAIN_SYSID_PROCEDURE.md`**
   - Complete drivetrain SysId guide
   - Button bindings for drive/angle motors
   - PathPlanner configuration

5. **`SYSID_QUICK_REFERENCE.md`**
   - Quick lookup for all SysId button bindings
   - Summary of procedures

6. **`AUTONOMOUS_TUNING_GUIDE.md`**
   - Auto mode setup and tuning
   - PathPlanner integration guide

---

## 💻 **Code Changes Made**

### **1. ArmSubsystem.java - Added Debug Code**

**Lines 159-167:**
```java
// TEMPORARY: Gear ratio measurement helper
double motorRotations = armMotor.getEncoder().getPosition();
double armDegrees = getCurrentAngle().getDegrees();
double armRotations = armDegrees / 360.0;
double measuredGearRatio = (armRotations != 0) ? motorRotations / armRotations : 0;

SmartDashboard.putNumber("ARM_DEBUG/MotorRotations", motorRotations);
SmartDashboard.putNumber("ARM_DEBUG/ArmDegrees", armDegrees);
SmartDashboard.putNumber("ARM_DEBUG/MeasuredGearRatio", measuredGearRatio);
```

**Purpose:** Measure actual gear ratio via software without counting teeth

### **2. ArmConstants.java - Fixed Minimum Rotation**

**Changed Line 56:**
```java
// FROM:
public static final double kMinRotation = -45;  // Dangerous!

// TO:
public static final double kMinRotation = 0;    // Safe for SysId
```

### **3. SysId Button Bindings (Already Configured)**

**OperatorControls.java (Lines 81-89):**
```java
// ARM SysId - Hold Left Stick + D-Pad Up
controller.leftStick().and(controller.povUp()).whileTrue(armSubsystem.sysId());

// ELEVATOR SysId - Hold Left Stick + D-Pad Right
controller.leftStick().and(controller.povRight()).whileTrue(elevatorSubsystem.sysId());
```

**DriverControls.java (Lines 117-127):**
```java
// DRIVE MOTOR SysId - Hold Both Bumpers + Y
controller.leftBumper().and(controller.rightBumper()).and(controller.y()).whileTrue(
    drivebase.sysIdDriveMotorCommand());

// ANGLE MOTOR SysId - Hold Both Bumpers + X  
controller.leftBumper().and(controller.rightBumper()).and(controller.x()).whileTrue(
    drivebase.sysIdAngleMotorCommand());
```

### **4. Soft Limits (Already Configured)**

**ArmSubsystem.java Line 99:**
```java
.withSoftLimits(Degrees.of(0), Degrees.of(ArmConstants.kMaxRotation))
```

---

## ⚡ **Immediate Next Steps**

### **Priority 1: Verify Gear Ratio (BLOCKING EVERYTHING)**

```
1. Deploy code (already has debug)
2. Enable robot
3. Manually rotate arm 360°
4. Read ARM_DEBUG/MeasuredGearRatio
5. Update ArmConstants.java if needed
6. Redeploy and test
```

**Expected result:** Likely ~18:1 based on old conversion factor

### **Priority 2: Run Arm SysId**

```
1. Fix gear ratio first (Priority 1)
2. Position arm at 25-30° vertical
3. Hold Left Stick + D-Pad Up (operator)
4. Run 4 SysId tests in WPILib tool
5. Copy kS, kG, kV, kA to ArmConstants.java
6. Expected kG: 0.3-0.8 (much higher than current 0.088)
7. Deploy and verify arm holds position
```

### **Priority 3: PID Tuning (After SysId)**

```
1. Test with kP = 0.002 (current value)
2. Command arm to 90°, observe behavior
3. Adjust kP if too slow or oscillates
4. Add kD if needed for damping
5. Rarely need kI
```

### **Priority 4: Drivetrain SysId**

```
1. Clear 4+ meters of space
2. Hold Left Bumper + Right Bumper + Y (drive)
3. Hold Left Bumper + Right Bumper + X (angle)
4. Apply values to PathPlanner settings
5. Test autonomous routines
```

---

## 🚨 **Critical Safety Notes**

### **DO NOT:**
- ❌ Start SysId from 0° (hopper position)
- ❌ Remove soft limits before verifying kG is correct
- ❌ Run SysId with battery <12V
- ❌ Skip manual positioning to 25° vertical
- ❌ Ignore position errors (sign of wrong gearing)

### **ALWAYS:**
- ✅ Start SysId from 25-30° vertical
- ✅ Verify gear ratio before SysId
- ✅ Test in safe range (0° to 125°) first
- ✅ Have emergency stop ready
- ✅ Monitor telemetry during tests
- ✅ Fix feedforward (kG) before tuning PID

---

## 🔍 **Known Issues to Address**

### **1. Gear Ratio Uncertainty**

**Status:** CRITICAL - BLOCKING  
**Evidence:** Old code ~18:1, current code 12:1  
**Impact:** 33% position errors, invalid SysId data  
**Fix:** Use debug code to measure actual ratio  

### **2. Low kG Value**

**Status:** CRITICAL - BLOCKING  
**Current:** 0.088858  
**Expected:** 0.3 - 0.8  
**Impact:** Arm won't move or overshoots  
**Fix:** Run SysId to get correct value  

### **3. No kS Value**

**Status:** HIGH  
**Current:** 0 (no static friction compensation)  
**Expected:** 0.1 - 0.3  
**Impact:** Arm may stick, inconsistent movement  
**Fix:** SysId will measure this  

---

## 📊 **Expected Performance After Tuning**

### **Before SysId:**
- ❌ Arm won't move OR overshoots wildly
- ❌ Can't hold position (drifts)
- ❌ Inconsistent behavior
- ❌ Position errors 10-30%
- ❌ Dangerous near gravity reversal point

### **After SysId + PID Tuning:**
- ✅ Smooth, controlled movement
- ✅ Holds position at any angle (no drift)
- ✅ Accurate targeting (< 2° error)
- ✅ Fast response (2-4s for full range)
- ✅ No overshoot
- ✅ Repeatable performance
- ✅ Voltage-independent (works at 12V or 11V)
- ✅ Safe operation through full range

---

## 🎯 **Key Concepts Summary**

### **SysId**
- Measures mechanism physics empirically
- Provides feedforward constants (kS, kG, kV, kA)
- Required for optimal performance
- Must be done from safe starting position (25° for arm)

### **Gear Ratio**
- Tells YAMS how motor rotation relates to mechanism movement
- CRITICAL to get correct before any other tuning
- Can be measured via software (encoder + position)
- Wrong ratio breaks EVERYTHING (position, velocity, limits, SysId)

### **Feedforward vs PID**
- **Feedforward (kG):** Handles known physics (gravity, friction)
- **PID:** Corrects errors and unknowns
- **MUST tune feedforward FIRST, then PID**
- Most performance comes from correct kG!

### **Gravity Reversal**
- Arm has direction flip at vertical (25°)
- Above: gravity pulls down, motors lift
- Below: gravity pulls toward bumper, motors resist
- Makes SysId dangerous if not handled correctly
- Solution: Start from vertical, use soft limits

---

## 📞 **Quick Reference**

### **SysId Button Bindings:**
- ARM: Left Stick + D-Pad Up (operator)
- ELEVATOR: Left Stick + D-Pad Right (operator)
- DRIVE: L Bumper + R Bumper + Y (driver)
- ANGLE: L Bumper + R Bumper + X (driver)

### **Dashboard Values to Monitor:**
- `ARM_DEBUG/MeasuredGearRatio` - Actual gear ratio
- `ARM_DEBUG/MotorRotations` - Motor encoder position
- `ARM_DEBUG/ArmDegrees` - Arm angle
- `ArmMotor/Position` - YAMS telemetry
- `ArmExample/Angle` - YAMS telemetry

### **Constants to Update After SysId:**
- `ArmConstants.kS` (Line 70)
- `ArmConstants.kG` (Line 71) ← **CRITICAL**
- `ArmConstants.kV` (Line 72)
- `ArmConstants.kA` (Line 73)

### **Critical Files:**
- `/src/main/java/frc/robot/constants/subsystems/ArmConstants.java`
- `/src/main/java/frc/robot/subsystems/arm/ArmSubsystem.java`
- `/COMPLETE_ARM_TUNING_GUIDE.md`

---

**This summary captures everything discussed in the session. Use it to continue work in another chat or share with teammates!**
