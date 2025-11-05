# Contributing Guidelines

Thank you for contributing to our FRC robot code! This document provides guidelines for contributing to the project.

## Code Standards

### Naming Conventions
- **Classes**: PascalCase (`ArmSubsystem`, `DriveCommand`)
- **Methods**: camelCase (`moveToPosition`, `getHeight`)
- **Constants**: UPPER_SNAKE_CASE (`MAX_SPEED`, `CORAL_L4_HEIGHT`)
- **Variables**: camelCase (`targetAngle`, `elevatorHeight`)
- **Packages**: lowercase (`constants`, `subsystems`)

### File Organization
- One public class per file
- File name matches class name
- Group related classes in packages
- Keep files under 400 lines when possible

### Code Style
- **Indentation**: 4 spaces (no tabs)
- **Line length**: 120 characters max
- **Braces**: Opening brace on same line
- **Imports**: Organize and remove unused imports

### Documentation
```java
/**
 * Moves the arm to a specific angle.
 * 
 * @param targetAngle The desired arm angle in degrees
 * @param elevatorHeight Current elevator height for collision checking
 * @return Command that moves the arm to position
 */
public Command moveToAngle(Rotation2d targetAngle, Distance elevatorHeight) {
    // Implementation
}
```

## Git Workflow

### Branch Naming
- `feature/description` - New features
- `fix/description` - Bug fixes
- `refactor/description` - Code refactoring
- `docs/description` - Documentation updates

Examples:
- `feature/auto-coral-pickup`
- `fix/elevator-soft-limits`
- `refactor/split-constants`

### Commit Messages
Follow conventional commits:
```
<type>: <description>

[optional body]

[optional footer]
```

Types:
- `feat`: New feature
- `fix`: Bug fix
- `refactor`: Code refactoring
- `docs`: Documentation
- `test`: Adding tests
- `chore`: Maintenance tasks

Examples:
```
feat: add L4 scoring position for coral

fix: correct elevator soft limit calculations

refactor: split Constants.java into modular files
- Created constants/ package
- Moved subsystem constants to separate files
- Updated all imports

docs: update control mappings for new button layout
```

### Pull Request Process

1. **Create a branch** from `main`
2. **Make your changes** following code standards
3. **Test thoroughly**:
   - Build without errors
   - Test in simulation if possible
   - Test on practice robot if available
4. **Commit with clear messages**
5. **Create Pull Request** with:
   - Clear title
   - Description of changes
   - Testing performed
   - Any breaking changes noted
6. **Request review** from team members
7. **Address feedback** promptly
8. **Merge** after approval

### Before Committing

- [ ] Code builds without errors
- [ ] No unused imports
- [ ] Comments added for complex logic
- [ ] Constants are properly named and documented
- [ ] Follows team coding standards
- [ ] Tested in simulation or on robot

## Adding New Features

### New Subsystem
1. Create class extending `SubsystemBase` in `subsystems/[name]/`
2. Add constants to `constants/subsystems/[Name]Constants.java`
3. Add motor configs to `config/subsystems/[Name]Config.java`
4. Register in `RobotContainer.java`
5. Add to FaultManager if using CAN devices
6. Document in `docs/SUBSYSTEMS.md`

### New Command
1. Create in appropriate package:
   - `commands/teleop/[subsystem]/` for teleop
   - `commands/auto/` for autonomous
   - `commands/groups/` for command groups
2. Extend appropriate base class (`Command`, `CommandGroup`, etc.)
3. Document purpose and usage
4. Add to button bindings or autonomous builder

### New Constant
1. Add to appropriate file in `constants/`
2. Use descriptive name
3. Add unit in comment if applicable
4. Group with related constants

## Testing

### Simulation Testing
```bash
./gradlew simulateJava
```
Test basic functionality:
- Commands execute correctly
- Subsystems initialize
- No exceptions thrown

### Practice Robot Testing
1. Deploy to practice robot
2. Test in disabled mode first
3. Verify all subsystems respond
4. Test individual commands
5. Test full sequences

### Competition Robot Testing
- Only deploy tested code
- Have a known-good backup
- Test in practice matches
- Monitor diagnostics

## Code Review Checklist

### For Authors
- [ ] Code follows style guidelines
- [ ] All files have appropriate headers
- [ ] No commented-out code (unless explained)
- [ ] Constants are in correct location
- [ ] Imports are organized
- [ ] JavaDoc on public methods
- [ ] Tested and working

### For Reviewers
- [ ] Code is understandable
- [ ] Logic is sound
- [ ] No obvious bugs
- [ ] Follows team standards
- [ ] Documentation is clear
- [ ] No unnecessary complexity

## Common Patterns

### Creating a Command
```java
public class ExampleCommand extends Command {
    private final ExampleSubsystem subsystem;
    
    public ExampleCommand(ExampleSubsystem subsystem) {
        this.subsystem = subsystem;
        addRequirements(subsystem);
    }
    
    @Override
    public void execute() {
        // Command logic
    }
    
    @Override
    public boolean isFinished() {
        return false; // or condition
    }
}
```

### Creating a Subsystem
```java
public class ExampleSubsystem extends SubsystemBase {
    private final SparkMax motor;
    
    public ExampleSubsystem() {
        motor = new SparkMax(ExampleConstants.MOTOR_ID, MotorType.kBrushless);
        motor.configure(ExampleConfig.MOTOR_CONFIG, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
    }
    
    @Override
    public void periodic() {
        // Runs every 20ms
    }
}
```

## Getting Help

- **Stuck on a problem?** Ask a mentor or experienced team member
- **Not sure about design?** Open an issue for discussion
- **Found a bug?** Create an issue with steps to reproduce
- **Want to propose a feature?** Open an issue first to discuss

## Resources

- [WPILib Documentation](https://docs.wpilib.org/)
- [YAGSL Swerve Docs](https://github.com/BroncBotz3481/YAGSL)
- [PathPlanner Docs](https://pathplanner.dev/)
- [FRC Programming Done Right](https://frc-pdr.readthedocs.io/)

## Questions?

Contact team programming leads or mentors for clarification on any guidelines.
