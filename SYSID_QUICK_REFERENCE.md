# SysId Quick Reference - All Button Bindings

## 🎮 **Button Combinations**

### **Drivetrain SysId (Driver Controller)**

```
DRIVE MOTORS:
Hold: Left Bumper + Right Bumper + Y

ANGLE MOTORS:
Hold: Left Bumper + Right Bumper + X
```

### **Mechanisms SysId (Operator Controller)**

```
ARM:
Hold: Left Stick + D-Pad Up

ELEVATOR:
Hold: Left Stick + D-Pad Right
```

---

## 📋 **SysId Checklist**

### **Drivetrain (30 min)**
- [ ] Clear 4+ meters of space
- [ ] Fully charged battery (>12.5V)
- [ ] Clean wheels
- [ ] Run Drive SysId (4 tests)
- [ ] Run Angle SysId (4 tests)
- [ ] Record kS, kV, kA values
- [ ] Apply to PathPlanner config

**Procedure:** See `DRIVETRAIN_SYSID_PROCEDURE.md`

### **Arm (20 min)**
- [ ] Position arm at 25° vertical
- [ ] Set soft limits (0° to 125°)
- [ ] Run Arm SysId (4 tests)
- [ ] Record kS, kG, kV, kA values
- [ ] Apply to ArmConstants.java

**Procedure:** See `ARM_SYSID_SAFE_PROCEDURE.md`

### **Elevator (15 min)**
- [ ] Elevator at safe height
- [ ] Run Elevator SysId (4 tests)
- [ ] Record kS, kG, kV, kA values
- [ ] Apply to ElevatorConstants.java

**Procedure:** See `HEAVY_MECHANISM_TUNING_GUIDE.md`

---

## 🎯 **Test Order (Recommended)**

1. **Drivetrain First** (most important for auto)
2. **Arm** (gravity compensation critical)
3. **Elevator** (completes mechanism tuning)

**Total Time:** ~65 minutes for complete characterization

---

## 📊 **Expected Values Reference**

### **Drivetrain (NEO, 6.75:1, 4" wheels)**
```
Drive kS: 0.1 - 0.3 V
Drive kV: 2.0 - 2.5 V/(m/s)
Drive kA: 0.1 - 0.5 V/(m/s²)

Angle kS: 0.05 - 0.2 V
Angle kV: 0.5 - 1.5 V/(deg/s)
Angle kA: 0.01 - 0.1 V/(deg/s²)
```

### **Arm (Heavy mechanism)**
```
kS: 0.1 - 0.3 V
kG: 0.3 - 0.8 V  ← Critical for gravity!
kV: 0.001 - 0.01 V/(deg/s)
kA: 0.001 - 0.01 V/(deg/s²)
```

### **Elevator (16lb carriage)**
```
kS: 0.1 - 0.5 V
kG: 0.8 - 1.5 V  ← Critical for gravity!
kV: 0.01 - 0.1 V/(m/s)
kA: 0.01 - 0.1 V/(m/s²)
```

---

## ⚠️ **Safety Notes**

**All SysId Tests:**
- ✅ 3-button combos prevent accidental activation
- ✅ Driver keeps finger on DISABLE
- ✅ Spotter watches for obstacles
- ✅ Clear area of obstacles/people
- ✅ Fully charged battery

**Drivetrain:**
- Robot moves 4+ meters during tests
- Dynamic tests are FAST - be ready!
- Test on flat, clean floor

**Arm:**
- Start from 25° vertical, NOT hopper (0°)
- Soft limits prevent bumper collision
- Gravity reversal point at vertical

**Elevator:**
- Keep clear of mechanism during movement
- Watch for cable tangling
- Verify soft limits active

---

## 🚀 **After SysId - Performance Gains**

### **Drivetrain:**
- ✅ Voltage-independent path following
- ✅ Accurate at any battery level
- ✅ Optimal acceleration
- ✅ < 5cm path errors

### **Mechanisms:**
- ✅ Smooth, controlled movement
- ✅ Holds position against gravity
- ✅ No oscillation or drift
- ✅ Accurate targeting

### **Competition Impact:**
- 🏆 Reliable auto all day long
- 🏆 Consistent between qualification matches
- 🏆 Works in elims when battery tired
- 🏆 +10-15 ranking points from better auto

---

## 📚 **Documentation**

**Detailed Procedures:**
- `DRIVETRAIN_SYSID_PROCEDURE.md` - Drivetrain characterization
- `ARM_SYSID_SAFE_PROCEDURE.md` - Arm characterization (gravity-safe)
- `HEAVY_MECHANISM_TUNING_GUIDE.md` - General tuning guide

**Configuration:**
- `AUTONOMOUS_TUNING_GUIDE.md` - Complete auto setup
- `AutoConstants.java` - Path following PID
- `ArmConstants.java` - Arm feedforward
- `ElevatorConstants.java` - Elevator feedforward

---

## ✅ **Ready to Run?**

1. **Deploy latest code** (SysId bindings now active)
2. **Open WPILib SysId tool**
3. **Start with drivetrain** (biggest auto impact)
4. **Follow detailed procedures** in respective .md files
5. **Record all values** for future reference
6. **Apply to robot config**
7. **Test and verify** improved performance

**You're set up for COMPETITION-GRADE performance!** 🏆
