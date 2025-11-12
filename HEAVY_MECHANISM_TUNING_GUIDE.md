# Heavy Mechanism PID/Feedforward Tuning Guide

## 🔴 Problem Diagnosis

**Symptoms you're experiencing:**
- ❌ Not enough voltage → Mechanism doesn't move (falls due to gravity)
- ❌ Too much voltage → Overshoots target (no compensation for weight)

**Root Cause:** **kG (gravity compensation) is too low** for your heavy mechanisms!

### Current Problematic Values

**ARM (ArmConstants.java line 71):**
```java
public static final double kG = 0.088858;  // ❌ TOO LOW for heavy arm
public static final double kP = 0.002058;  // ❌ TOO LOW causes sluggish response
```

**ELEVATOR (ElevatorConstants.java line 50):**
```java
public static final double kG = 0.36;      // ❌ TOO LOW for 16lb carriage
public static final double kP = 2.0;       // Might be okay, but verify with SysId
```

---

## ✅ SOLUTION: Proper Tuning with SysId

### What is SysId?

**System Identification** automatically calculates the correct feedforward values by:
1. Moving your mechanism at different speeds
2. Measuring voltage vs movement
3. Calculating kS, kG, kV, kA values that compensate for weight/friction

**You already have SysId implemented!** Just need to run it.

---

## 📋 Step-by-Step Tuning Process

### **Phase 1: Setup SysId Bindings**

Add this to `OperatorControls.java`:

```java
/**
 * Configures SysId buttons for mechanism tuning.
 * SAFETY: Requires holding LEFT STICK to prevent accidental activation.
 */
private void configureSysIdButtons() {
    // ARM SysId - Hold Left Stick + D-Pad Up
    controller.leftStick().and(controller.povUp()).whileTrue(
        armSubsystem.sysId()
    );
    
    // ELEVATOR SysId - Hold Left Stick + D-Pad Down
    controller.leftStick().and(controller.povDown()).whileTrue(
        elevatorSubsystem.sysId()
    );
}
```

Then add to `configureBindings()`:
```java
public void configureBindings() {
    configureDefaultCommands();
    configureCoralScoringButtons();
    configureAlgaeScoringButtons();
    configureGrabberButtons();
    configureSysIdButtons();  // ← Add this
}
```

### **Phase 2: Run SysId (Follow WPILib Docs)**

1. **Open WPILib SysId Tool**
   - Launch from WPILib menu or standalone

2. **Select "Arm" or "Elevator" mechanism**

3. **Deploy robot code**

4. **Connect to robot NetworkTables**

5. **Run Tests** (one at a time):
   - **Quasistatic Forward** - Hold Left Stick + D-Pad Up/Down
     - Mechanism moves SLOWLY at constant voltage
     - Measures friction (kS) and gravity (kG)
   
   - **Quasistatic Reverse** 
     - Same but opposite direction
   
   - **Dynamic Forward**
     - Mechanism accelerates rapidly
     - Measures velocity (kV) and acceleration (kA)
   
   - **Dynamic Reverse**
     - Same but opposite direction

6. **Analyze Data**
   - SysId will calculate kS, kG, kV, kA values
   - **kG will be much higher than your current values!**

7. **Update Constants**
   - Copy new values to `ArmConstants.java` or `ElevatorConstants.java`
   - Redeploy

### **Phase 3: Tune PID (After Feedforward is Good)**

Once feedforward compensates for gravity, tune PID:

**Start with kP only:**
1. Set kI = 0, kD = 0
2. Increase kP until mechanism oscillates
3. Reduce kP by 50%

**Add kD if needed:**
1. If still oscillating, add small kD (start with kP/10)
2. Increase until oscillation stops

**Add kI only if steady-state error:**
1. Only if mechanism doesn't quite reach target
2. Start very small (kP/1000)

---

## 📊 Expected Values for Heavy Mechanisms

### **ARM with Heavy Load:**
```java
// Typical values for heavy arms (your actual values will vary)
kS = 0.1 - 0.3    // Friction
kG = 0.3 - 0.8    // ← THIS WILL BE MUCH HIGHER
kV = 0.001 - 0.01 // Velocity 
kA = 0.001 - 0.01 // Acceleration
kP = 0.01 - 0.1   // ← Will need to increase this too
```

### **ELEVATOR with 16lb Carriage:**
```java
// Typical values for heavy elevators
kS = 0.1 - 0.5    // Friction
kG = 0.8 - 1.5    // ← THIS WILL BE MUCH HIGHER
kV = 0.01 - 0.1   // Velocity
kA = 0.01 - 0.1   // Acceleration  
kP = 2.0 - 10.0   // Might be okay as-is
```

---

## ⚠️ Safety Notes

### **Before Running SysId:**

1. **Clear the area** - mechanism will move at full range
2. **Start with LOW voltages** - I've already reduced them for you:
   - Arm: 2V quasistatic, 1.5V/s ramp
   - Elevator: 4V quasistatic, 2.0V/s ramp
3. **Be ready to disable robot** if something goes wrong
4. **Check soft limits** are configured (they are in your code)

### **If SysId Voltage Too Low:**

If mechanism barely moves during SysId:
1. Increase `SYSID_STEP_VOLTAGE` by 1V
2. Increase `SYSID_RAMP_RATE_VALUE` by 0.5
3. Redeploy and try again

### **If SysId Voltage Too High:**

If mechanism moves dangerously fast:
1. Decrease voltages
2. Press disable immediately if unsafe

---

## 🔧 Troubleshooting

### **"Mechanism doesn't move at all"**
- ✅ kG too low - run SysId to get correct value
- ✅ Check motor direction (inverted?)
- ✅ Check current limits (might be too low)
- ✅ Check brake mode is enabled

### **"Mechanism oscillates/shakes"**
- ✅ kP too high - reduce it
- ✅ Add kD to dampen oscillation
- ✅ Check mechanism for mechanical friction/binding

### **"Mechanism overshoots target"**
- ✅ kG too low (doesn't slow down when approaching)
- ✅ kP might be too high
- ✅ Add kD for damping

### **"Mechanism drifts from target"**
- ✅ kG too low (can't hold against gravity)
- ✅ Might need small kI (but fix kG first!)

### **"Different behavior up vs down"**
- ✅ Normal for gravity-affected mechanisms
- ✅ Run SysId in BOTH directions
- ✅ YAMS uses average of both directions

---

## 📝 Tuning Checklist

- [ ] Add SysId button bindings to `OperatorControls.java`
- [ ] Deploy code with reduced SysId voltages
- [ ] Clear area around robot
- [ ] Open WPILib SysId tool
- [ ] Connect to robot
- [ ] Run Quasistatic Forward test on ARM
- [ ] Run Quasistatic Reverse test on ARM
- [ ] Run Dynamic Forward test on ARM
- [ ] Run Dynamic Reverse test on ARM
- [ ] Analyze ARM data, copy kS, kG, kV, kA to `ArmConstants.java`
- [ ] Repeat for ELEVATOR
- [ ] Analyze ELEVATOR data, copy values to `ElevatorConstants.java`
- [ ] Redeploy with new feedforward values
- [ ] Test mechanism movement - should be much better!
- [ ] Tune kP if still oscillating or sluggish
- [ ] Add kD if oscillation persists
- [ ] Only add kI if steady-state error exists
- [ ] Test at different positions/speeds
- [ ] Verify no oscillation, overshooting, or drift

---

## 📚 Additional Resources

**WPILib SysId Documentation:**
https://docs.wpilib.org/en/stable/docs/software/advanced-controls/system-identification/introduction.html

**YAMS SysId Examples:**
- Arm: https://yagsl.gitbook.io/yams/documentation/how-to/how-to-run-sysid-on-a-arm
- Elevator: https://yagsl.gitbook.io/yams/documentation/how-to/how-to-run-sysid-on-a-elevator

**Control Theory Background:**
https://docs.wpilib.org/en/stable/docs/software/advanced-controls/introduction/introduction-to-feedforward.html

---

## 🎯 Expected Outcome

After proper tuning:
✅ Mechanism holds position against gravity (no drift)
✅ Smooth movement without oscillation
✅ Reaches target accurately
✅ Same performance up and down
✅ No overshooting
✅ Repeatable behavior

**The key is kG (gravity compensation)!** Your current values are too low, which is why you need high voltage to move, but then it overshoots because there's no compensation.
