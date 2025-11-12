# Autonomous Mode - Complete Setup & Tuning Guide

## 🔍 **Current Setup Assessment**

### ✅ What's Working

**PathPlanner Integration:**
- ✅ AutoBuilder properly configured in `SwerveSubsystem.setupPathPlanner()`
- ✅ RobotConfig loaded from PathPlanner GUI settings
- ✅ Alliance mirroring enabled (red/blue auto-flip)
- ✅ NamedCommands registered for mechanism positioning (PrepL2/L3/L4, etc.)
- ✅ Auto chooser with 8 routines on dashboard
- ✅ SysId commands available (`sysIdDriveMotorCommand`, `sysIdAngleMotorCommand`)

**Architecture:**
- ✅ Clean separation: PathPlanner for driving, NamedCommands for mechanisms
- ✅ Gyro zeroed on auto start (alliance-aware)
- ✅ Feedforward enabled in path following
- ✅ Using YAGSL swerve library for hardware abstraction

### ⚠️ **CRITICAL ISSUES - NOW FIXED**

**1. Translation PID was WAY TOO LOW** ✅ **FIXED**
```java
// Before: DEFAULT_TRANSLATION_P = 0.005;  // ❌ 1000x too low!
// After:  DEFAULT_TRANSLATION_P = 5.0;    // ✅ Correct!
```
**Impact before fix:** Robot barely followed paths, huge position errors  
**Status:** ✅ Fixed in `AutoConstants.java` line 17

---

**2. SysId NOT Run on Drivetrain** ⚠️ **NEEDS TUNING**
- Module drive feedforward likely untuned
- No kS, kV, kA values from characterization
- Path following inaccurate without proper feedforward

**Impact:** Slower acceleration, less accurate paths, voltage-dependent performance

---

**3. Robot Config Needs Verification**
PathPlanner settings (`pathplanner/settings.json`) may be inaccurate:
- Robot mass: 52.16 kg (115 lbs) - **verify with scale**
- MOI: 6.883 kg·m² - **likely placeholder**
- Wheel radius: 0.048m (1.9") - **measure actual wheel**
- Max drive speed: 5.45 m/s - **verify with SysId**

---

## 🎯 **Complete Tuning Procedure**

### **Phase 1: Translation PID Tuning** ✅ **COMPLETE**

**Already fixed** - Translation kP changed from 0.005 → 5.0

**How to fine-tune if needed:**

1. **Deploy code** with current values
2. **Run a simple straight-line autonomous** ("meter test" or similar)
3. **Observe robot behavior:**

**If robot is sluggish (slow to correct errors):**
- Increase kP by 1.0-2.0
- Try: kP = 7.0, then 10.0 if still slow
  
**If robot oscillates/wobbles around path:**
- Decrease kP by 0.5-1.0
- Try: kP = 4.0, then 3.0 if still oscillating
- Add small kD (start with 0.5)

**If robot has steady-state error (doesn't quite reach target):**
- Add small kI (start with 0.001)
- Be careful - kI can cause overshoot

**Current values are a good starting point for most swerve drives!**

---

### **Phase 2: Run SysId on Drivetrain** ⚠️ **RECOMMENDED**

SysId characterizes your drive motors to get accurate feedforward values.

#### **Why SysId Matters:**
- **kS (static)** - voltage to overcome friction
- **kV (velocity)** - voltage per m/s of speed  
- **kA (acceleration)** - voltage per m/s² of accel

Without these, PathPlanner can't compensate for battery voltage drops, motor heating, carpet friction, etc.

#### **How to Run SysId:**

**Prerequisites:**
1. Clear ~4 meters of open space
2. Fully charged battery
3. Driver station laptop with WPILib SysId tool

**Steps:**

1. **Add SysId button binding** in `DriverControls.java`:
```java
// In configureBindings() method
driverController.start().and(driverController.back()).whileTrue(
    drivebase.sysIdDriveMotorCommand()
);
```

2. **Deploy code**

3. **Open WPILib SysId tool** (standalone or from VS Code)

4. **Select "General Mechanism"** as analysis type

5. **Connect to robot** NetworkTables

6. **Position robot** in open area, facing straight

7. **Run tests** (hold Start + Back buttons):
   - Quasistatic Forward
   - Quasistatic Reverse
   - Dynamic Forward
   - Dynamic Reverse

8. **Analyze data** in SysId tool → Get kS, kV, kA values

9. **Update swerve config** with values:
   - Open PathPlanner GUI
   - Settings → Robot Config
   - Enter kS, kV values from SysId
   - Save and redeploy paths

**Expected values for NEO swerve:**
```
kS ≈ 0.1 - 0.3 V
kV ≈ 2.0 - 2.5 V/(m/s)  
kA ≈ 0.1 - 0.5 V/(m/s²)
```

---

### **Phase 3: Verify Robot Physical Properties**

PathPlanner needs accurate robot dimensions for dynamics calculations.

#### **Measurements to Take:**

**1. Robot Mass** (with battery, bumpers, mechanisms):
```bash
# Weigh robot on scale
Actual mass: ___ kg (___ lbs)
# Update in pathplanner/settings.json line 30
"robotMass": 52.16  // ← Your actual mass
```

**2. Wheel Diameter:**
```bash
# Measure wheel with calipers
Diameter: ___ mm → ___ meters
# Update in pathplanner/settings.json line 33
"driveWheelRadius": 0.048  // ← radius in meters
```

**3. Robot Dimensions:**
```bash
# Measure bumper-to-bumper (with bumpers on!)
Width: ___ meters
Length: ___ meters
# Update in pathplanner/settings.json lines 2-3
"robotWidth": 0.9,   // ← Your actual width
"robotLength": 0.9   // ← Your actual length
```

**4. Moment of Inertia (MOI)** - Advanced:
- Use PathPlanner's MOI calculator
- Or estimate: `MOI ≈ mass × (width² + length²) / 12`
- Update line 31: `"robotMOI": 6.883`

---

### **Phase 4: Rotation PID Tuning** (if needed)

Current rotation kP = 5.0 is usually good, but verify:

**Test procedure:**
1. Run auto that rotates robot (180° turn)
2. Observe rotation behavior

**If rotation is slow/sluggish:**
- Increase kP to 7.0-10.0

**If rotation oscillates/overshoots:**
- Decrease kP to 3.0-4.0
- Add kD = 0.5-1.0

**If rotation has steady error:**
- Add small kI = 0.01-0.05

---

## 📋 **Testing Checklist**

Use this to verify your auto setup is working:

### **Basic Functionality:**
- [ ] Robot deploys without errors
- [ ] Auto chooser shows on dashboard
- [ ] Can select different autos from chooser
- [ ] Gyro zeros when entering auto mode
- [ ] Robot follows paths (roughly) with current PID

### **After Translation PID Fix:**
- [ ] Robot follows straight paths accurately
- [ ] Position error < 10cm throughout path
- [ ] No oscillation or wobbling
- [ ] Reaches endpoints precisely

### **After SysId:**
- [ ] Consistent performance regardless of battery voltage
- [ ] Smooth acceleration/deceleration
- [ ] Accurate velocity tracking
- [ ] Feedforward values in PathPlanner config

### **After Physical Properties Update:**
- [ ] Robot dimensions match actual (including bumpers)
- [ ] Mass is accurate (weighed with battery)
- [ ] Wheel radius is correct (measured)
- [ ] MOI calculated or estimated

### **Integration Testing:**
- [ ] Auto routines complete successfully
- [ ] NamedCommands execute at right times
- [ ] Mechanism positioning works during path
- [ ] Alliance color mirroring works (test both sides)
- [ ] No collisions or tipping

---

## 🚀 **Optimization Tips**

**Path Planning:**
- Use PathPlanner's "Generate" button for optimal trajectories
- Set appropriate velocity/acceleration constraints
- Add rotation targets at key waypoints
- Use event markers for mechanism prep

**Performance:**
- Keep battery charged (>12.5V for consistency)
- Clean wheels before matches (grip affects tracking)
- Check module alignment (use YAGSL alignment tool)
- Monitor CAN bus utilization (should be <70%)

**Debugging:**
- Enable PathPlanner telemetry (already on - HIGH verbosity)
- Watch "Actual vs Target" pose on dashboard
- Log path following errors
- Use AdvantageScope for detailed analysis

---

## 🔧 **Quick Reference - Tuning Parameters**

### **AutoConstants.java**
```java
Translation kP: 5.0   // Increase if slow, decrease if oscillates
Translation kI: 0.0   // Only if steady-state error
Translation kD: 0.0   // Only if oscillates with high kP

Rotation kP: 5.0      // Increase if slow turns, decrease if overshoots
Rotation kI: 0.0      // Rarely needed
Rotation kD: 0.0      // Add if rotation oscillates
```

### **pathplanner/settings.json**
```json
"defaultMaxVel": 3.0,           // Max robot speed (m/s) - from SysId
"defaultMaxAccel": 3.0,         // Max acceleration (m/s²)
"defaultMaxAngVel": 540.0,      // Max rotation speed (deg/s)
"defaultMaxAngAccel": 720.0,    // Max rotation accel (deg/s²)
"robotMass": 52.16,             // Actual robot mass (kg)
"robotMOI": 6.883,              // Moment of inertia (kg·m²)
"driveWheelRadius": 0.048,      // Wheel radius (m)
"driveGearing": 6.75,           // L2 gearing for NEO
"maxDriveSpeed": 5.45           // Theoretical max from gearing
```

---

## ✅ **Summary**

**What's Already Done:**
- ✅ Translation PID fixed (0.005 → 5.0)
- ✅ PathPlanner integration working
- ✅ NamedCommands registered
- ✅ Auto routines configured

**What You Should Do Next:**
1. **Test current auto** - should work MUCH better now
2. **Run SysId** on drive motors (30 min task, big improvement)
3. **Verify robot dimensions** in PathPlanner GUI (5 min)
4. **Fine-tune PIDs** based on testing

**Expected Results:**
- Accurate path following (< 5cm error)
- Consistent performance
- Reliable autonomous scoring
- Competition-ready auto routines

Your autonomous setup is **fundamentally correct** - just needed the PID fix and characterization!
