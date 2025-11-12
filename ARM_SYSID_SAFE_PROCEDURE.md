# ARM SysId Safe Procedure - For Arms with Gravity Reversal

## 🚨 YOUR SPECIFIC PROBLEM

Your arm has a **gravity direction flip**:

```
      BACK/UP (125°)
           |
           |  ← Gravity RESISTS (kG compensates to lift)
        (25°) VERTICAL - Gravity contribution = 0
           |
        (0°) HOPPER
           |  
           |  ← Gravity ASSISTS (kG must RESIST falling!)
      (-45°) BUMPER ⚠️ DANGER - Can slam into bumper!
```

**Why standard SysId is dangerous:**
1. SysId starts from 0° (hopper)
2. Tries to move forward (negative angles)
3. Passes vertical (~-20°)
4. **Gravity flips direction** - now PULLS arm toward bumper
5. **ARM SLAMS INTO BUMPER** at high speed

---

## ✅ SAFE SYSID PROCEDURE

### **Step 1: Set Safe Soft Limits (ALREADY DONE)**

I've updated `ArmSubsystem.java` line 99:
```java
.withSoftLimits(Degrees.of(0), Degrees.of(125))
```

This prevents SysId from going past 0° (into the danger zone).

### **Step 2: Manually Position Arm Before SysId**

**CRITICAL:** Start SysId from a SAFE position, not from hopper!

**Before running SysId:**
1. **Manually drive arm to vertical (25°)** using joystick
2. **Verify arm is stable at 25°**
3. **NOW start SysId** - it will move in SAFE range (0° to 125°)

**Why this works:**
- SysId will move arm ±range from starting position
- Starting at 25° means it goes: 0° ← 25° → 125°
- NEVER goes negative (past vertical into danger zone)
- Gets full gravity range: fighting gravity (125°) and assisted by gravity (0°)

### **Step 3: Run SysId Tests**

With arm starting at **25° vertical**:

1. **Quasistatic Forward** (slow movement):
   - Arm moves from 25° → 125° (back/up)
   - Measures gravity resistance
   
2. **Quasistatic Reverse** (slow movement):
   - Arm moves from 25° → 0° (toward hopper)
   - Measures gravity assistance
   - **STOPS at 0° due to soft limit** ✅ Safe!

3. **Dynamic Forward** (fast movement):
   - Arm accelerates 25° → 125°
   
4. **Dynamic Reverse** (fast movement):
   - Arm accelerates 25° → 0°
   - **STOPS at 0° due to soft limit** ✅ Safe!

### **Step 4: What SysId Learns**

Even though you don't test the -45° to 0° range, SysId still calculates correct kG because:

**Physics of gravity on arms:**
```
Torque = kG × cos(angle)
```

SysId measures from 0° to 125° and calculates:
- At 0° (hopper): Small gravity pull backward
- At 25° (vertical): Zero gravity torque
- At 90° (horizontal): Maximum gravity torque
- At 125° (back): Moderate gravity torque

From this data, it calculates the kG coefficient that applies at ALL angles, including -45°.

**Result:** When you later use the arm at -45° (with proper soft limit removal), kG will correctly compensate for gravity pulling toward bumper!

---

## 📋 DETAILED PROCEDURE

### **BEFORE SysId:**

1. ✅ **Deploy code** with soft limits (0° to 125°) - already done!

2. ✅ **Enable robot** in test mode

3. ✅ **Manually drive arm to 25° vertical** using operator joystick
   - Use left Y-axis on operator controller
   - Arm should be roughly straight up

4. ✅ **Verify position** on SmartDashboard
   - Look for "ArmMotor/Position" or similar
   - Should read ~25°

5. ✅ **Clear area** - no obstacles in 0° to 125° range

### **DURING SysId:**

1. ✅ **Open WPILib SysId tool**

2. ✅ **Select "Arm" mechanism**

3. ✅ **Connect to robot NetworkTables**

4. ✅ **Hold Left Stick + D-Pad Up** to trigger SysId

5. ✅ **Run each test:**
   - Quasistatic Forward
   - Quasistatic Reverse
   - Dynamic Forward  
   - Dynamic Reverse

6. ✅ **Monitor for safety:**
   - Arm should NEVER go negative (toward bumper)
   - Should stop at 0° (hopper) when going forward
   - Should stop at 125° when going back

### **AFTER SysId:**

1. ✅ **Analyze data** in SysId tool

2. ✅ **Copy kS, kG, kV, kA** to `ArmConstants.java`:
```java
// ArmConstants.java lines 70-73
public static final double kS = ???;  // From SysId
public static final double kG = ???;  // From SysId (will be higher!)
public static final double kV = ???;  // From SysId  
public static final double kA = ???;  // From SysId
```

3. ✅ **TEST with soft limits** first:
   - Deploy with new kG
   - Test movement in 0° to 125° range
   - Verify smooth, controlled motion

4. ✅ **OPTIONAL: Expand soft limits** (only if needed):
   - Once kG is tuned and arm is smooth in safe range
   - Can CAREFULLY test negative angles
   - Change soft limits to include -10°, -20°, etc.
   - **Monitor closely** - gravity will HELP arm toward bumper
   - Add physical stops/padding at bumper if needed

---

## ⚠️ SAFETY CHECKLIST

- [ ] Soft limits set to (0°, 125°) - prevents negative angles
- [ ] Arm manually positioned at 25° before starting SysId
- [ ] Team members clear of arm sweep range
- [ ] Emergency stop ready (disable robot button)
- [ ] Monitoring telemetry during tests
- [ ] Physical bumper protection (foam/padding) if available

---

## 🎯 Expected kG Value

For your heavy arm with gravity reversal:

**Current:** `kG = 0.088858` (probably too low)

**Expected after SysId:** `kG = 0.3 - 0.8` (much higher!)

**Why it will be higher:**
- Your arm is heavy
- It needs significant voltage to hold at horizontal (90°)
- Current kG can't provide enough voltage → doesn't move
- When you increase voltage manually → overshoots because kG isn't compensating

**After SysId with correct kG:**
- At 125° (back): kG adds voltage to fight gravity pulling down
- At 25° (vertical): kG adds ~0 voltage (gravity perpendicular)
- At 0° (hopper): kG adds voltage to RESIST gravity pulling toward bumper
- At -30° (if you go there later): kG adds EVEN MORE voltage to prevent slam

The **same kG value** works for ALL angles because WPILib's `ArmFeedforward` automatically calculates `kG × cos(angle)` at every position!

---

## 🔧 After Tuning - Using Negative Angles

Once kG is tuned in safe range (0° to 125°), you can OPTIONALLY test negative angles:

### **Gradual Expansion:**

1. **Test -5°** with soft limit:
   ```java
   .withSoftLimits(Degrees.of(-5), Degrees.of(125))
   ```
   - Deploy, test carefully
   - Should move smoothly toward bumper and STOP at -5°

2. **Test -10°** if -5° is smooth:
   ```java
   .withSoftLimits(Degrees.of(-10), Degrees.of(125))
   ```

3. **Gradually expand** to operational range

4. **Add kD if needed:**
   - If arm oscillates near bumper
   - Small kD helps dampen as gravity accelerates it

### **Physical Safety:**

- Add padding/bumpers at -45° stop
- Consider velocity limits when approaching negative angles
- Test with low kP first, then increase

---

## 💡 Key Insight

**You don't need to run SysId in the dangerous range!**

SysId measures arm behavior at 0° to 125°, calculates kG coefficient, and WPILib applies it correctly at ALL angles including -45°.

The math (`kG × cos(angle)`) works universally - you just need enough data points to find kG, which the safe range provides!
