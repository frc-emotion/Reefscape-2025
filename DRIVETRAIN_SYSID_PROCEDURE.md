# Drivetrain SysId - Complete Procedure for Optimal Auto Performance

## 🎯 **Why Run Drivetrain SysId?**

SysId characterizes your drive motors to get **accurate feedforward values** (kS, kV, kA).

**Without SysId:**
- ❌ Paths depend on battery voltage (12.8V vs 11.5V = different speeds)
- ❌ Inconsistent acceleration (early match vs end of match)
- ❌ Motor heating affects performance
- ❌ Carpet friction not compensated
- ❌ Position errors accumulate during long autos

**With SysId:**
- ✅ Voltage-independent path following
- ✅ Consistent performance all match long
- ✅ Compensates for motor heating, battery sag, carpet wear
- ✅ Accurate velocity tracking
- ✅ Better position accuracy (< 5cm errors vs 10-20cm)
- ✅ **Competition-level autonomous performance**

---

## 📋 **Prerequisites**

### **Hardware:**
- [ ] Fully charged battery (>12.5V)
- [ ] Clean wheels (wipe down with damp cloth)
- [ ] Tight module bolts (check all fasteners)
- [ ] 4+ meters of clear, flat space

### **Software:**
- [ ] WPILib SysId tool installed (comes with WPILib)
- [ ] Robot code deployed with SysId bindings
- [ ] Driver station laptop ready
- [ ] NetworkTables connection working

### **Team:**
- [ ] Driver ready at controls
- [ ] Spotter watching robot movement
- [ ] Someone to catch robot if needed (stays alert, not in path)

---

## 🎮 **Button Bindings (Driver Controller)**

**Already configured in your code:**

```
DRIVE MOTOR SysId:
Hold: Left Bumper + Right Bumper + Start

ANGLE MOTOR SysId:
Hold: Left Bumper + Right Bumper + Back
```

**Safety:** Requires holding 3 buttons simultaneously to prevent accidental activation!

---

## 🚀 **Step-by-Step Procedure**

### **Phase 1: Drive Motor Characterization (15 min)**

This characterizes the wheel drive motors to find kS, kV, kA for linear motion.

#### **Setup:**

1. **Position robot** in center of clear area
   - 4+ meters clear in front
   - 4+ meters clear behind
   - Robot facing straight forward

2. **Open WPILib SysId tool**
   - Launch from VS Code: `Ctrl+Shift+P` → "WPILib: Start Tool" → "SysId"
   - Or run standalone from start menu

3. **Configure SysId:**
   - Mechanism: Select "**General Mechanism**"
   - Units: "Meters" (for drivetrain)
   - Test type: Leave defaults

4. **Connect to robot:**
   - Click "Connect to robot" in SysId tool
   - Verify NetworkTables connected (green indicator)
   - Should show "Robot connected"

#### **Run Tests:**

**Test 1: Quasistatic Forward** (slow ramp)
```
1. Driver: Hold Left Bumper + Right Bumper + Start
2. SysId: Click "Quasistatic Forward"
3. Robot moves forward slowly, gradually speeding up
4. Release buttons when test complete (~5 seconds)
5. SysId: Click "Stop" if needed
6. Verify data captured (see graph in SysId)
```

**Test 2: Quasistatic Reverse** (slow ramp backward)
```
1. Driver: Hold Left Bumper + Right Bumper + Start
2. SysId: Click "Quasistatic Reverse"
3. Robot moves backward slowly
4. Release buttons when complete
5. Stop and verify data
```

**Test 3: Dynamic Forward** (fast acceleration)
```
1. Clear MORE space (robot will move faster!)
2. Driver: Hold Left Bumper + Right Bumper + Start
3. SysId: Click "Dynamic Forward"
4. Robot accelerates rapidly forward
5. BE READY to release buttons or disable if needed
6. Stop and verify data
```

**Test 4: Dynamic Reverse** (fast acceleration backward)
```
1. Clear space behind robot
2. Driver: Hold Left Bumper + Right Bumper + Start
3. SysId: Click "Dynamic Reverse"
4. Robot accelerates rapidly backward
5. Stop and verify data
```

#### **Safety Notes:**
- Driver keeps finger on DISABLE at all times
- Spotter watches for obstacles
- If robot moves erratically, DISABLE immediately
- Dynamic tests move FAST - be prepared!

#### **Analyze Drive Data:**

1. **Click "Analyze Data"** in SysId tool
2. **Verify graphs look smooth:**
   - Position should increase linearly
   - Velocity should ramp smoothly
   - No sudden jumps or drops
3. **Read results** from SysId:
   ```
   kS (Static): ___ V      (typical: 0.1 - 0.3)
   kV (Velocity): ___ V    (typical: 2.0 - 2.5)
   kA (Accel): ___ V       (typical: 0.1 - 0.5)
   ```
4. **Save project** (File → Save) - name it "Drivetrain_Drive_YYYYMMDD"

---

### **Phase 2: Angle Motor Characterization (10 min)**

This characterizes the module steering motors (usually don't need feedforward, but good for verification).

#### **Setup:**

1. **Position robot** in center (doesn't need as much space)
2. **Keep SysId tool open** (can use same project or new one)

#### **Run Tests:**

**Same 4 tests as drive, but with ANGLE button:**
```
ANGLE Button Combo: Left Bumper + Right Bumper + Back

1. Quasistatic Forward
2. Quasistatic Reverse
3. Dynamic Forward
4. Dynamic Reverse
```

**What happens:**
- Wheels rotate in place (not driving forward)
- Testing steering motor characteristics
- Usually less dramatic than drive tests

#### **Analyze Angle Data:**

1. **Analyze in SysId**
2. **Note values** (typically lower than drive):
   ```
   kS: ___ V
   kV: ___ V
   kA: ___ V
   ```
3. **Save project** - name it "Drivetrain_Angle_YYYYMMDD"

**Note:** Angle motors usually use position PID, so feedforward less critical. Main value is for validation.

---

### **Phase 3: Apply Results to Robot**

#### **Update Swerve Configuration:**

**Option 1: Update via PathPlanner GUI (RECOMMENDED)**

1. **Open PathPlanner** (separate application)
2. **File → Settings → Robot Config**
3. **Enter Drive Feedforward values:**
   ```
   Drive kS: ___ (from SysId)
   Drive kV: ___ (from SysId)
   ```
4. **Save settings**
5. **Regenerate all paths** (File → Generate All)
6. **Deploy paths** to robot

**Option 2: Update via Code**

Add to `SwerveSubsystem` constructor (after swerve creation):
```java
// Apply SysId feedforward values
replaceSwerveModuleFeedforward(
    0.15,  // kS from SysId
    2.3,   // kV from SysId
    0.25   // kA from SysId
);
```

**Option 3: Update JSON Config** (if using YAGSL JSON configs)

Edit `/deploy/swerve/neo/modules/pidfproperties.json`:
```json
{
  "drive": {
    "p": 0.0020645,
    "i": 0,
    "d": 0,
    "f": 0.0023,  // Set to kV from SysId
    "iz": 0
  }
}
```

---

## 📊 **Verification Testing**

After applying feedforward values:

### **Test 1: Voltage Independence**

1. **Run autonomous** with full battery (>12.5V)
2. **Note position errors** from dashboard/logs
3. **Drain battery** to ~11V (run practice, etc.)
4. **Run SAME autonomous**
5. **Compare position errors** - should be nearly identical!

**Expected:** < 10% difference in errors across voltage range

### **Test 2: Straight Line Accuracy**

1. **Create test path:** 3 meter straight line
2. **Mark start/end** positions with tape
3. **Run path 5 times**
4. **Measure endpoint** each time

**Expected:** All endpoints within 5cm of target

### **Test 3: Complex Path**

1. **Run your best auto routine**
2. **Watch position error** on dashboard (actual vs target pose)
3. **Note maximum error** during path

**Expected:** < 10cm error throughout, < 5cm at waypoints

---

## 🎯 **Expected Feedforward Values**

### **Drive Motors (NEO with 6.75:1 gearing, 4" wheels):**

```
kS: 0.10 - 0.30 V     // Voltage to overcome friction
kV: 2.0 - 2.5 V/(m/s) // Voltage per meter/second
kA: 0.1 - 0.5 V/(m/s²)// Voltage per meter/second²
```

**Interpretation:**
- **kS** = voltage needed to "unstick" the modules from standstill
- **kV** = how much voltage per unit of speed (main value)
- **kA** = voltage needed for acceleration (usually small)

### **If Your Values Are WAY Different:**

**Much higher kS (>0.5):**
- Check for mechanical binding
- Clean/lubricate gears
- Check belt tension
- Verify no modules dragging

**Much higher kV (>3.0):**
- Verify gear ratio is correct in config
- Check wheel diameter measurement
- Ensure motor directions correct

**Negative values:**
- Motor inverted wrong direction
- Re-run SysId with corrected inversion

---

## ⚠️ **Troubleshooting**

### **Robot doesn't move during SysId:**
- Verify button combo correct (3 buttons!)
- Check SysId connected to robot
- Verify robot enabled (not E-stopped)
- Try increasing voltage in SysId settings

### **Robot moves erratically:**
- Could be normal for dynamic tests (fast!)
- Check for loose modules/wheels
- Verify CAN bus health
- Ensure no conflicting commands running

### **SysId shows bad data (choppy graphs):**
- Check battery voltage (needs >12V)
- Verify clean floor (no obstacles/debris)
- Ensure all 4 modules working
- Check for CAN errors in driver station

### **Values seem unreasonable:**
- Double-check units (meters vs feet!)
- Verify gear ratio in config
- Measure wheel diameter accurately
- Compare to similar robots (ask mentors)

---

## 📝 **Data Recording Sheet**

```
Date: ___________
Battery Voltage: _____ V
Team Members: _______________________

DRIVE MOTOR SYSID:
  kS: _____ V
  kV: _____ V/(m/s)
  kA: _____ V/(m/s²)
  R² fit: _____ (should be > 0.95)

ANGLE MOTOR SYSID:
  kS: _____ V
  kV: _____ V/(deg/s)
  kA: _____ V/(deg/s²)
  R² fit: _____

VERIFICATION TESTS:
  Full battery auto error: _____ cm
  Low battery auto error: _____ cm
  Straight line variance: _____ cm
  Max path error: _____ cm

NOTES:
_________________________________
_________________________________
_________________________________
```

---

## 🚀 **After SysId: Performance Gains**

### **Before SysId:**
- Position error: 10-20cm throughout path
- Battery-dependent performance
- Inconsistent between matches
- Struggles with fast accelerations
- Auto may fail late in competition day

### **After SysId:**
- Position error: < 5-10cm throughout path
- Battery-independent (12.8V = 11.5V performance)
- Consistent all day
- Smooth accelerations
- Reliable auto all competition long

### **Competition Impact:**
- **More reliable scoring** (path accuracy)
- **Faster cycle times** (optimized acceleration)
- **Consistent qualification matches** (no battery variance)
- **Works in elims** (when battery may be tired)
- **+10-15 ranking points** from better auto performance

---

## ✅ **Completion Checklist**

### **Before Event:**
- [ ] SysId button bindings deployed
- [ ] WPILib SysId tool ready
- [ ] Practice space reserved (4+ meters clear)
- [ ] Fully charged battery
- [ ] Wheels cleaned

### **During SysId:**
- [ ] Drive Quasistatic Forward complete
- [ ] Drive Quasistatic Reverse complete
- [ ] Drive Dynamic Forward complete
- [ ] Drive Dynamic Reverse complete
- [ ] Angle tests complete (all 4)
- [ ] Data analyzed and saved
- [ ] Values recorded

### **After SysId:**
- [ ] Feedforward values applied to code/PathPlanner
- [ ] Paths regenerated with new FF
- [ ] Code redeployed to robot
- [ ] Verification tests passed
- [ ] Auto performance improved
- [ ] Data sheet saved for reference

---

**You now have COMPETITION-GRADE autonomous performance!** 🏆

**Pro tip:** Re-run SysId if you:
- Change wheels
- Change gear ratios
- Change carpet (comp field vs practice)
- Notice auto degradation
- Add significant weight to robot
