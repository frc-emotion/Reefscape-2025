# Complete ARM SysId & PIDF Tuning Guide
## Based on Your Actual Robot Photos

---

## 📸 **Understanding Your Arm Geometry**

Based on your photos, here's your arm configuration:

```
         125° MAX BACK/UP
              |
              |  ← Gravity RESISTS (must apply voltage to lift)
              |
       25-30° VERTICAL (Photo 1 & 3)
              |  ← ZERO gravity torque (stable equilibrium)
              |
           0° HOPPER REST (Photo 2)
              |  ← Gravity starts ASSISTING
              |
         -45° BUMPER (dangerous!)
              |  ← Gravity PULLS arm down - WILL SLAM!
```

### **The Critical Problem: Gravity Reversal**

**Above vertical (25° to 125°):**
- Gravity pulls arm DOWN
- Motors must LIFT against gravity
- kG adds voltage to fight gravity

**At vertical (~25°):**
- Gravity perpendicular to arm
- Zero gravitational torque
- kG contribution ≈ 0

**Below vertical (0° to -45°):**
- Gravity HELPS arm fall toward bumper
- Motors must RESIST gravity
- kG must BRAKE to prevent slam
- **THIS IS WHY SYSID IS DANGEROUS if started from 0°!**

---

## 🎯 **What is PIDF and How Does SysId Help?**

### **Feedforward (F) - From SysId:**

**kS (Static friction)** - Voltage to overcome friction and "unstick" the arm
```
Typical: 0.1 - 0.3 V
Your current: 0 (likely why arm doesn't move!)
```

**kG (Gravity compensation)** - Voltage to hold arm at any angle
```
Formula: Torque = kG × cos(angle)
Typical for heavy arms: 0.3 - 0.8 V
Your current: 0.088858 (WAY TOO LOW - this is your main problem!)
```

**kV (Velocity)** - Voltage per degree/second of speed
```
Typical: 0.001 - 0.01 V/(deg/s)
Your current: 0.00025 (probably okay)
```

**kA (Acceleration)** - Voltage for acceleration
```
Typical: 0.001 - 0.01 V/(deg/s²)
Your current: 0.001 (probably okay)
```

### **PID (Feedback) - Manual tuning after feedforward:**

**kP (Proportional)** - How aggressively to correct position error
```
Start: 0.002
Increase if: Arm is slow to reach target
Decrease if: Arm oscillates around target
Your current: 0.002058 (good starting point)
```

**kI (Integral)** - Corrects steady-state error over time
```
Start: 0
Only add if: Arm never quite reaches target (rare)
Warning: Can cause overshoot and instability
Your current: 0 (correct!)
```

**kD (Derivative)** - Dampens oscillation
```
Start: 0  
Add if: Arm oscillates even with correct kP
Typical: 0.0001 - 0.001
Your current: 0 (correct for now)
```

### **Why Your Arm Doesn't Work Now:**

**Current kG = 0.088858 is too low:**
1. Not enough voltage to fight gravity at 90° (horizontal)
2. Arm won't move OR needs excessive manual voltage
3. When you add voltage → overshoots because kG isn't compensating
4. **Solution: Run SysId to get CORRECT kG (probably 0.3-0.8)**

---

## 🚀 **COMPLETE SYSID PROCEDURE**

### **Phase 1: Pre-Flight Checklist**

**Hardware:**
- [ ] Robot on blocks OR clear 2 meters around arm sweep
- [ ] Elevator at safe height (mid-range, not bottom)
- [ ] Battery fully charged (>12.5V)
- [ ] All arm bolts tight
- [ ] Motor connected, no CAN errors

**Software:**
- [ ] Code deployed with soft limits (0° to 125°) - ✅ Already done!
- [ ] WPILib SysId tool installed
- [ ] Driver station connected
- [ ] NetworkTables working

**Team:**
- [ ] Operator at controls
- [ ] Spotter watching arm movement
- [ ] Someone ready to disable if needed

---

### **Phase 2: Manual Positioning** ⭐ **CRITICAL STEP**

**DO NOT start SysId from Photo 2 position (0° hopper)!**

**Steps:**

1. **Enable robot in teleop mode**

2. **Manually drive arm to vertical (25-30°)** - Like Photo 1/3 position
   - Operator controller: Left Y-axis
   - Move arm BACK/UP from hopper
   - Stop when arm is pointing straight up (vertical)

3. **Verify position on dashboard:**
   ```
   Look for: "ArmMotor/Position" or "ArmExample/Angle"
   Should read: ~25-30 degrees
   ```

4. **Arm should be stable** at this position (zero gravity torque)

**Why start at 25-30°?**
- ✅ SysId will test range: 0° ← **25°** → 125°
- ✅ Soft limits prevent going negative (past 0°)
- ✅ Never enters bumper danger zone
- ✅ Still gets full gravity data (hopper to max back)

---

### **Phase 3: Run SysId Tests**

**Button Combo (Operator Controller):**
```
Hold: Left Stick + D-Pad Up
```

**In WPILib SysId Tool:**

1. **Open tool** (VS Code: Ctrl+Shift+P → "WPILib: Start Tool" → "SysId")

2. **Configure:**
   - Mechanism: "Arm"
   - Units: "Degrees"
   - Teams: Your team number

3. **Connect to robot** (NetworkTables)

4. **Verify arm at 25-30°** before starting

5. **Run 4 tests in order:**

**Test 1: Quasistatic Forward (Slow Ramp)**
```
Operator: Hold Left Stick + D-Pad Up
SysId: Click "Quasistatic Forward"
What happens: Arm slowly moves 25° → 125° (back/up)
Duration: ~5-10 seconds
Release buttons when complete
```

**Test 2: Quasistatic Reverse (Slow Ramp)**
```
Operator: Hold Left Stick + D-Pad Up
SysId: Click "Quasistatic Reverse"
What happens: Arm slowly moves 25° → 0° (toward hopper)
STOPS at 0° due to soft limit ✅ SAFE!
Release buttons when complete
```

**Test 3: Dynamic Forward (Fast Acceleration)**
```
Operator: Hold Left Stick + D-Pad Up
SysId: Click "Dynamic Forward"
What happens: Arm accelerates rapidly 25° → 125°
BE READY: This is FAST!
Release if unsafe
```

**Test 4: Dynamic Reverse (Fast Acceleration)**
```
Operator: Hold Left Stick + D-Pad Up  
SysId: Click "Dynamic Reverse"
What happens: Arm accelerates rapidly 25° → 0°
STOPS at 0° due to soft limit ✅ SAFE!
Release if unsafe
```

**Safety During Tests:**
- ⚠️ Arm should NEVER go negative (toward bumper)
- ⚠️ Should stop cleanly at 0° and 125°
- ⚠️ If arm behaves erratically → DISABLE immediately
- ⚠️ Spotter watches for binding/obstacles

---

### **Phase 4: Analyze SysId Data**

**In SysId Tool:**

1. **Click "Analyze Data"**

2. **Check graph quality:**
   - Position should be smooth curve
   - Velocity should ramp steadily
   - No sudden jumps or drops
   - If data looks bad → re-run tests

3. **Read feedforward values:**
   ```
   kS: _____ V       (write this down)
   kG: _____ V       (THIS is the critical one!)
   kV: _____ V/(deg/s)
   kA: _____ V/(deg/s²)
   
   R² fit: _____ (should be > 0.95 for good data)
   ```

**Expected Values for Your Arm:**
```
kS: 0.1 - 0.3 V          (overcome friction)
kG: 0.3 - 0.8 V          ← MUCH higher than current 0.088!
kV: 0.001 - 0.01 V/(deg/s)
kA: 0.001 - 0.01 V/(deg/s²)
```

4. **Save project** (File → Save As → "Arm_SysId_YYYYMMDD.json")

---

### **Phase 5: Apply Results to Code**

**Update `ArmConstants.java` lines 70-73:**

```java
// Feedforward Constants
public static final double kS = 0.15;     // ← From SysId
public static final double kG = 0.45;     // ← From SysId (CRITICAL!)
public static final double kV = 0.0025;   // ← From SysId
public static final double kA = 0.0015;   // ← From SysId
```

**Deploy code:**
```bash
./gradlew deploy
```

**Initial Test (CRITICAL):**

1. **Enable robot in teleop**
2. **Manually move arm with joystick**
3. **Observe behavior:**
   - ✅ Should move smoothly
   - ✅ Should hold position when joystick released
   - ✅ No drift or oscillation
   - ✅ Similar speed up and down

4. **If arm drifts DOWN when released:**
   - kG is still too low
   - Increase by 0.1 and redeploy
   - Repeat until arm holds

5. **If arm drifts UP when released:**
   - kG is too high (unlikely)
   - Decrease by 0.05 and redeploy

---

## 🎛️ **PID Tuning (After Feedforward is Correct)**

**Only tune PID AFTER kG is correct and arm holds position!**

### **Step 1: Test Current kP**

**Command arm to specific angle (e.g., 90°):**
```java
// Use macro or test command
arm.setAngle(Degrees.of(90));
```

**Observe:**
- Does it reach 90°?
- How long does it take?
- Does it oscillate?
- Does it overshoot?

### **Step 2: Tune kP**

**Current: kP = 0.002058**

**If arm is SLOW to reach target:**
```
Increase kP by 50%: kP = 0.003
Test again
Continue increasing until responsive
```

**If arm OSCILLATES (bounces around target):**
```
Decrease kP by 25%: kP = 0.0015
Test again
Continue decreasing until smooth
```

**If arm OVERSHOOTS then settles:**
```
This is normal with just kP
If mild → acceptable
If severe → decrease kP slightly
```

**Goal: Fast response without oscillation**
```
Typical final kP: 0.001 - 0.005 for arms
```

### **Step 3: Add kD (Only If Needed)**

**Add kD if:**
- Arm oscillates even with low kP
- Arm overshoots significantly
- Need faster response but kP causes oscillation

**Start small:**
```java
public static final double kD = 0.0001;
```

**Effect:**
- Dampens oscillation
- Slows approach to target
- More stable

**Tune:**
- Increase kD if still oscillating
- Decrease kD if too slow/sluggish
- Typical final kD: 0.0001 - 0.001

### **Step 4: kI (Rarely Needed)**

**Only add kI if:**
- Arm consistently stops SHORT of target
- Small steady-state error persists
- kP and kD tuned but still can't reach exact target

**Start very small:**
```java
public static final double kI = 0.0001;
```

**Warning:**
- kI accumulates error over time
- Can cause overshoot
- Can make system unstable
- Usually NOT needed with correct kG!

---

## 📊 **Verification Tests**

After tuning, run these tests:

### **Test 1: Position Holding**
```
1. Move arm to 90° (horizontal)
2. Release joystick
3. Arm should HOLD position (no drift)
4. Repeat at 0°, 45°, 125°
```
**Expected:** No drift at any angle

### **Test 2: Commanded Positions**
```
1. Command arm to 0° → should reach and hold
2. Command arm to 45° → should reach and hold
3. Command arm to 90° → should reach and hold
4. Command arm to 125° → should reach and hold
```
**Expected:** < 2° error, no oscillation

### **Test 3: Response Time**
```
1. Command arm from 0° to 125°
2. Time how long it takes
3. Should be smooth, not jerky
```
**Expected:** 2-4 seconds for full range

### **Test 4: No Overshoot**
```
1. Command large movements (0° → 125°)
2. Arm should NOT overshoot target
3. Should approach smoothly and settle
```
**Expected:** < 5° overshoot, settles in < 1 second

---

## 🔧 **Troubleshooting Guide**

### **Problem: Arm won't move at all**

**Cause:** kG too low, kS too low, or PID disabled

**Fix:**
1. Check kG from SysId (should be 0.3-0.8)
2. Check kS from SysId (should be 0.1-0.3)
3. Verify motor not inverted wrong direction
4. Check CAN connection
5. Verify no mechanical binding

### **Problem: Arm moves but drifts down**

**Cause:** kG too low (not enough gravity compensation)

**Fix:**
```java
// Increase kG by 0.1
public static final double kG = 0.55; // was 0.45
```
Redeploy and test

### **Problem: Arm drifts up**

**Cause:** kG too high (over-compensating)

**Fix:**
```java
// Decrease kG by 0.05
public static final double kG = 0.40; // was 0.45
```

### **Problem: Arm oscillates around target**

**Cause:** kP too high

**Fix:**
```java
// Decrease kP by 25%
public static final double kP = 0.0015; // was 0.002
```

**Or add kD:**
```java
public static final double kD = 0.0002;
```

### **Problem: Arm moves slowly to target**

**Cause:** kP too low

**Fix:**
```java
// Increase kP by 50%
public static final double kP = 0.003; // was 0.002
```

### **Problem: Arm overshoots then settles**

**Cause:** Normal with just kP, or kD needed

**Fix:**
```java
// Add small kD to dampen
public static final double kD = 0.0001;
```

### **Problem: Arm won't quite reach target**

**Cause:** Steady-state error, or kG slightly off

**Fix:**
1. First, fine-tune kG (try ±0.05)
2. If persists, add tiny kI:
```java
public static final double kI = 0.00005;
```

---

## 🎯 **Tuning Flowchart**

```
START
  ↓
1. Run SysId (safe procedure above)
  ↓
2. Apply kS, kG, kV, kA to code
  ↓
3. Test: Does arm HOLD position when released?
  ↓
  NO → Adjust kG (increase if drifts down, decrease if drifts up)
  ↓
  YES → Feedforward is CORRECT! ✅
  ↓
4. Test: Does arm reach commanded target?
  ↓
  Slow? → Increase kP
  Fast but oscillates? → Decrease kP
  ↓
5. Test: Does it overshoot?
  ↓
  YES → Add kD (start 0.0001)
  NO → PID tuning complete! ✅
  ↓
6. Test: Steady-state error?
  ↓
  YES → Add tiny kI (0.00005)
  NO → FULLY TUNED! 🎉
```

---

## 📋 **Data Recording Sheet**

```
Date: ___________
Battery Voltage: _____ V
Arm Mass: _____ lbs
Team Members: _______________________

SYSID RESULTS:
  kS: _____ V
  kG: _____ V  ← CRITICAL VALUE
  kV: _____ V/(deg/s)
  kA: _____ V/(deg/s²)
  R² fit: _____ (target > 0.95)

INITIAL TEST (with SysId values):
  Holds position? YES / NO
  Drifts up/down? _______
  Adjusted kG to: _____

PID TUNING:
  Starting kP: 0.002058
  Final kP: _____
  Added kD? YES / NO → Value: _____
  Added kI? YES / NO → Value: _____

FINAL PERFORMANCE:
  Position error: _____ degrees
  Response time (0→125°): _____ seconds
  Overshoot: _____ degrees
  Oscillation: NONE / MILD / SEVERE
  
NOTES:
_________________________________
_________________________________
_________________________________
```

---

## ⚠️ **Safety Notes**

### **During SysId:**
- ✅ Start from 25-30° vertical (Photos 1/3 position)
- ❌ NEVER start from 0° hopper (Photo 2 position)
- ✅ Soft limits prevent negative angles
- ✅ Team clear of arm sweep
- ✅ Emergency stop ready

### **After Tuning:**
- ⚠️ Soft limits still prevent negative angles for safety
- ⚠️ To use negative angles (bumper scoring):
  - First verify arm holds perfectly at 0°-125°
  - Then CAREFULLY expand soft limits to -5°
  - Test thoroughly before going to -45°
  - Consider physical padding at bumper

### **Operational:**
- Keep elevator coordinated with arm (anti-tip)
- Monitor motor temperature
- Check for mechanical wear
- Re-run SysId if performance degrades

---

## 🏆 **Expected Final Performance**

**After proper SysId + PID tuning:**

✅ **Position holding:** Arm stays at any angle (0° to 125°) with no drift  
✅ **Smooth movement:** No jerking or oscillation  
✅ **Accurate targeting:** < 2° error at any commanded position  
✅ **Fast response:** Reaches target in 2-4 seconds  
✅ **No overshoot:** Settles smoothly without bouncing  
✅ **Repeatable:** Same performance every time  
✅ **Voltage-independent:** Works at 12.5V or 11V battery  

**The transformation:**
- **Before:** Won't move OR overshoots wildly
- **After:** Precise, controlled, competition-ready! 🎯

---

## 🚀 **Quick Start Summary**

1. **Position arm at 25-30° vertical** (Photo 1/3 position)
2. **Hold Left Stick + D-Pad Up** (operator controller)
3. **Run 4 SysId tests** in WPILib tool
4. **Copy kS, kG, kV, kA** to `ArmConstants.java` lines 70-73
5. **Deploy and test** - arm should hold position
6. **Tune kP** if needed (increase for speed, decrease for stability)
7. **Add kD** if oscillates (start 0.0001)
8. **Done!** 🎉

**Most important:** **kG from SysId** - this fixes the "won't move" and "overshoots" problems!

---

## 📞 **Need Help?**

**Common questions:**

Q: *"Arm still drifts after SysId"*  
A: Increase kG by 0.1 until it holds

Q: *"SysId data looks choppy"*  
A: Check battery voltage (needs >12V), ensure smooth floor, re-run tests

Q: *"Arm oscillates no matter what kP I use"*  
A: Add kD = 0.0002, or check if mechanical binding

Q: *"Can I use negative angles after tuning?"*  
A: Yes, but expand soft limits gradually (-5°, -10°, etc.) and test carefully

**Your setup is ready - button bindings configured, soft limits set, documentation complete!**
