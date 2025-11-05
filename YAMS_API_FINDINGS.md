# YAMS API Findings - Actual vs Expected

**Status:** ✅ **YAMS Library Found!** But API differs from example code.

---

## ✅ What's Working

The YAMS library **is fully installed** and classes are found:
- ✅ `yams.mechanisms.positional.Arm`
- ✅ `yams.mechanisms.positional.Elevator`  
- ✅ `yams.mechanisms.config.ArmConfig`
- ✅ `yams.mechanisms.config.ElevatorConfig`
- ✅ `yams.motorcontrollers.SmartMotorController`
- ✅ `yams.motorcontrollers.local.SparkWrapper`
- ✅ `yams.gearing.GearBox`
- ✅ `yams.gearing.MechanismGearing`

---

## ⚠️ API Differences

The actual YAMS API differs from the example code you provided. Here are the mismatches:

### **1. Motor Control Methods**
```java
// ❌ Doesn't exist:
motor.set(double)
motor.getPosition()
motor.getVelocity()

// ✅ Likely exists (need to verify):
motor.setPercent(double)
// Position/velocity likely accessed differently
```

### **2. Voltage Methods**
```java
// ❌ Wrong type:
motor.setVoltage(double volts)

// ✅ Requires Voltage type:
motor.setVoltage(Volts.of(volts))
```

### **3. isNear() Return Type**
```java
// ❌ Returns Trigger, not boolean:
boolean atSetpoint = arm.isNear(angle, tolerance);

// ✅ Actual return:
Trigger trigger = arm.isNear(angle, tolerance);
// Need to call .getAsBoolean() or use differently
```

### **4. SysId Signature**
```java
// ❌ Doesn't exist:
arm.sysId()

// ✅ Requires parameters:
arm.sysId(Voltage rampRate, Velocity<VoltageUnit> stepVoltage, Time timeout)
```

### **5. GearBox Methods**
```java
// ❌ Doesn't exist:
GearBox.fromConversionFactor(double)

// ✅ Likely exists:
GearBox.fromReductionStages(...)
// Or constructor with different parameters
```

### **6. Config Methods Missing**
```java
// ❌ Don't exist:
SmartMotorControllerConfig.withOffset(double)
SmartMotorControllerConfig.withFollower(motor, inverted)
ElevatorConfig.withCarriageMass(Mass)
```

---

## 🔍 Current Build Errors

**Total:** 21 compilation errors

**Categories:**
1. Missing methods on `SmartMotorController` (6 errors)
2. Wrong types for voltage (3 errors)
3. Return type mismatches (2 errors)
4. Missing config methods (4 errors)
5. SysId signature mismatch (2 errors)
6. Other API differences (4 errors)

---

## 🎯 Recommended Actions

### **Option 1: Adapt to Actual API** ⭐ Recommended
Look at the actual YAMS source code you have locally and adapt to the real API:

```bash
# Find actual method signatures
grep -r "class SmartMotorController" src/main/java/yams/
grep -r "class Arm" src/main/java/yams/
grep -r "class Elevator" src/main/java/yams/
```

Then update our code to match the actual API.

### **Option 2: Use YAMS Source Documentation**
Since you have the source code locally (`src/main/java/yams/`), we can:
1. Read the actual class files
2. See what methods are available
3. Adapt our implementation accordingly

### **Option 3: Minimal YAMS Usage**
Use only the parts that work:
- `SmartMotorController` wrapper (partial)
- Configuration builders (what works)
- Skip mechanism classes initially

---

## 📂 YAMS Source Location

I can see from the build that YAMS source is available at:
```
/Users/arshan/Desktop/Reefscape-2025/src/main/java/yams/
```

This means we can directly read the source code to find the correct API!

---

## 🛠️ Next Steps

1. **Read actual YAMS source files** to find correct method signatures
2. **Update our code** to match real API
3. **Test incrementally** to ensure each fix works
4. **Document final API** for future reference

---

**Would you like me to:**
1. Read the YAMS source files to find the correct API?
2. Adapt the code based on what methods actually exist?
3. Focus on getting SmartMotorController working first, then mechanisms?

Let me know and I'll proceed! 🚀
