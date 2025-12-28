# Coding Standards for Infoskjermen Project

## Overview
This document establishes coding standards for all agents and developers working on the Infoskjermen project, a Spring Boot application providing dashboard services with Google Calendar, weather, and public transport integration.

## Architecture Overview
- **Technology Stack**: Spring Boot 3.4.1, Java 21, Maven
- **Deployment Target**: Google App Engine
- **Package Structure**: `no.infoskjermen.*`
- **Layer Architecture**: Controller → Service → Data/Utils

## Package Structure Standards

### Required Package Organization
```
no.infoskjermen/
├── InfoskjermenApplication.java    (Main application + REST controller)
├── ServletInitializer.java         (App Engine configuration)
├── Settings.java                    (Configuration management)
├── data/                           (Data models and DTOs)
├── tjenester/                      (Service layer - Norwegian for "services")
└── utils/                          (Utility classes)
```

### Package Naming Rules
- **Root package**: Always `no.infoskjermen`
- **Service layer**: Use `tjenester` (Norwegian convention in this codebase)
- **Data models**: Place in `data` package
- **Utilities**: Place in `utils` package
- **Test packages**: Mirror main package structure under `src/test/java`

## Code Style Standards

### Java Coding Conventions

#### 1. Class Structure
```java
@Service  // Spring annotations first
public class ExampleService extends BaseService implements RequiredInterface {
    
    // Constants (static final)
    private static final String CONSTANT_VALUE = "value";
    
    // Logger (if needed)
    private static final Logger log = LoggerFactory.getLogger(ExampleService.class);
    
    // Dependencies (injected via constructor)
    private final OtherService otherService;
    
    // Constructor
    public ExampleService(OtherService otherService) {
        this.otherService = otherService;
    }
    
    // Public methods
    // Private methods
}
```

#### 2. Dependency Injection
- **Prefer constructor injection** over `@Autowired` field injection
- Use `final` fields for injected dependencies
- Document constructor parameters

#### 3. Logging Standards
```java
// Use SLF4J with class-specific logger
private static final Logger log = LoggerFactory.getLogger(ClassName.class);

// Log levels:
log.debug("Detailed information: {}", parameter);  // Development info
log.info("Important application events");          // Production info  
log.warn("Potential issues: {}", issue);          // Warnings
log.error("Error occurred: {}", error, exception); // Errors with stack trace
```

#### 4. Exception Handling
```java
// Always include context in exceptions
throw new ServiceException("Failed to process calendar events for user: " + userName, e);

// Use try-with-resources for resource management
try (InputStream stream = getInputStream()) {
    // Process stream
} catch (IOException e) {
    log.error("Failed to process stream", e);
    throw new ServiceException("Stream processing failed", e);
}
```

#### 5. Method Naming
- Use Norwegian names when appropriate (following existing convention): `getEventer()`, `printDato()`
- Use English for new technical terms: `processApiResponse()`, `validateInput()`
- Be descriptive: `populateCalendarEvent()` not `populate()`

### Spring Boot Specific Standards

#### 1. Service Classes
```java
@Service
public class WeatherService implements PopulateInterface {
    
    private static final Logger log = LoggerFactory.getLogger(WeatherService.class);
    private final Settings settings;
    
    public WeatherService(Settings settings) {
        this.settings = settings;
        this.log = LoggerFactory.getLogger(WeatherService.class);
    }
    
    // Implement interface methods
    // Add service-specific methods
}
```

#### 2. Configuration Management
- Use `Settings` class for configuration access
- Follow existing pattern: `settings.getGoogleSettings(navn)`
- Document configuration keys used

#### 3. Caching
- Use `@EnableCaching` at application level (already configured)
- Add `@Cacheable` for expensive operations
- Use meaningful cache names

### Data Model Standards

#### 1. Data Classes
```java
public class CalendarEvent implements Comparable<CalendarEvent> {
    
    // Public fields are acceptable for simple DTOs (following existing pattern)
    public LocalDateTime eventStart;
    public LocalDateTime eventEnd;
    public String title;
    
    // Always implement compareTo properly
    @Override
    public int compareTo(CalendarEvent other) {
        // Null safety
        if (eventStart == null) return 1;
        if (other.eventStart == null) return -1;
        
        // Primary comparison
        int compare = eventStart.compareTo(other.eventStart);
        if (compare == 0) {
            compare = eventEnd.compareTo(other.eventEnd);
        }
        if (compare == 0) {
            return -1; // Maintain consistent ordering
        }
        return compare;
    }
}
```

#### 2. Date/Time Handling
- **Always use `java.time`** classes (`LocalDateTime`, `ZonedDateTime`)
- Use `DateTimeUtils` for common formatting operations
- Norwegian locale for user-facing dates: `new Locale("no", "NO")`

### Testing Standards

#### 1. Test Class Structure
```java
@SpringBootTest
public class ServiceNameTest {
    
    @Autowired
    private ServiceName serviceUnderTest;
    
    @BeforeEach
    public void setUp() {
        // Test setup
    }
    
    @Test
    public void shouldDoSomethingWhenCondition() {
        // Given
        // When  
        // Then
        assertThat(result).isNotNull();
    }
}
```

#### 2. Test Naming
- Use descriptive names: `shouldReturnEventsWhenUserHasCalendarAccess()`
- Follow Given-When-Then structure in test body
- Use AssertJ for assertions: `assertThat().isNotEmpty()`

#### 3. Integration Tests
- Use `@SpringBootTest` for service integration tests
- Mock external APIs appropriately
- Test error conditions and edge cases

## API and External Integration Standards

### 1. Google API Integration
```java
@Service
public class GoogleBasedService extends GoogleService {
    
    public GoogleBasedService(Settings settings) throws Exception {
        super(settings);
        log = LoggerFactory.getLogger(this.getClass());
    }
    
    // Follow existing pattern for API calls
    protected SomeResult callGoogleApi(String refreshToken, String parameter) throws Exception {
        // Handle authentication
        // Make API call
        // Process response
        // Handle errors appropriately
    }
}
```

### 2. Error Handling for External APIs
- Always handle API rate limits gracefully
- Log API errors with sufficient context
- Implement appropriate retry mechanisms
- Return meaningful error messages to users

## Documentation Standards

### 1. Class-Level Documentation
```java
/**
 * Service for managing calendar events from Google Calendar API.
 * Handles authentication, event retrieval, and data transformation
 * for the Infoskjermen dashboard display.
 */
@Service
public class CalendarService extends GoogleService {
```

### 2. Method Documentation
- Document public methods with clear purpose
- Explain parameters and return values
- Note any exceptions thrown
- Include usage examples for complex methods

### 3. Configuration Documentation
- Document all configuration keys used
- Explain expected value formats
- Provide examples in comments

## Performance and Security Standards

### 1. Caching Strategy
- Cache expensive API calls
- Use appropriate cache timeouts
- Clear cache when data changes

### 2. Security Practices
- Never log sensitive data (tokens, passwords)
- Use secure credential storage
- Validate all external inputs
- Handle authentication failures gracefully

### 3. Resource Management
- Use try-with-resources for external connections
- Close streams and connections properly
- Monitor memory usage for large datasets

## Maven and Build Standards

### 1. Dependency Management
- Keep dependencies up to date (see UPGRADE-TODO.md)
- Use Spring Boot's dependency management
- Document any version overrides

### 2. Build Configuration
- Follow existing Maven structure
- Use appropriate profiles for different environments
- Keep build artifacts clean

## Git Workflow and Branching Strategy

### Branch Types and Naming Conventions

#### Main Branches
- **`master`**: Production-ready code only
- **`develop`**: Integration branch for ongoing development (if using Git Flow)

#### Feature Branches
- **Naming**: `feature/description` or `feature/issue-number-description`
- **Examples**: 
  - `feature/upgrade-spring-boot-3.4.1`
  - `feature/calendar-service-improvements`
  - `feature/dependency-updates-phase1`

#### Bugfix Branches
- **Naming**: `bugfix/description` or `bugfix/issue-number-description`
- **Examples**:
  - `bugfix/calendar-null-pointer`
  - `bugfix/weather-service-timeout`

#### Hotfix Branches (for production issues)
- **Naming**: `hotfix/description`
- **Examples**: `hotfix/critical-auth-issue`

### Required Workflow

#### 1. Before Starting Any Work
```bash
# Always start from latest master
git checkout master
git pull origin master

# Create feature branch
git checkout -b feature/your-feature-name
```

#### 2. During Development
```bash
# Regular commits with clear messages
git add .
git commit -m "Add calendar event validation logic"

# Push branch to remote
git push -u origin feature/your-feature-name
```

#### 3. Before Merging to Master
```bash
# Ensure your branch is up to date with master
git checkout master
git pull origin master
git checkout feature/your-feature-name
git merge master  # or rebase: git rebase master

# Run all tests
./mvnw clean test

# Ensure application compiles and runs
./mvnw clean compile
./mvnw spring-boot:run
```

#### 4. Merge Requirements
- [ ] All tests pass (`./mvnw test`)
- [ ] Application compiles without errors
- [ ] Application runs successfully
- [ ] Code follows established standards
- [ ] Documentation updated if needed
- [ ] No merge conflicts with master

#### 5. Merging Process
```bash
# Final check
git checkout master
git pull origin master
git merge feature/your-feature-name

# Push to master
git push origin master

# Clean up feature branch
git branch -d feature/your-feature-name
git push origin --delete feature/your-feature-name
```

### Commit Message Standards

#### Format
```
Type: Brief description (50 chars max)

Optional detailed explanation if needed.
Wrap lines at 72 characters.

- List specific changes
- Reference issue numbers if applicable
```

#### Commit Types
- **feat**: New feature
- **fix**: Bug fix
- **docs**: Documentation changes
- **style**: Code style changes (formatting, etc.)
- **refactor**: Code refactoring
- **test**: Adding or updating tests
- **chore**: Build process or auxiliary tool changes
- **upgrade**: Dependency upgrades

#### Examples
```
feat: Add weather service caching functionality

Implement Redis-based caching for weather API responses
to reduce external API calls and improve response times.

- Add cache configuration
- Update WeatherService with @Cacheable
- Add cache eviction strategy
```

```
upgrade: Update Spring Boot to 3.4.1

- Upgrade spring-boot-starter-parent to 3.4.1
- Update configuration for new version
- Fix deprecated method usage
- All tests passing
```

### Branch Protection Rules

#### Master Branch Protection
- Direct pushes to master are **FORBIDDEN**
- All changes must go through feature branches
- Require status checks to pass
- Require up-to-date branches before merging

#### Emergency Hotfixes
For critical production issues only:
1. Create hotfix branch from master
2. Make minimal necessary changes
3. Test thoroughly
4. Merge to master immediately
5. Document the emergency change

### Development Workflow for Different Scenarios

#### Single Developer Working Alone
```bash
# Still use branches for organization
git checkout -b feature/my-changes
# Make changes, commit, test
git checkout master
git merge feature/my-changes
git push origin master
```

#### Team Development
```bash
# Always coordinate with team
git checkout -b feature/my-feature
# Make changes, push branch
git push -u origin feature/my-feature
# Create pull request or coordinate merge
```

#### Dependency Upgrades (Following UPGRADE-TODO.md)
```bash
# For each phase in UPGRADE-TODO.md
git checkout -b upgrade/phase1-easy-updates
# Complete all Phase 1 upgrades
# Test thoroughly
git checkout master
git merge upgrade/phase1-easy-updates

# Next phase
git checkout -b upgrade/phase2-google-apis
# Continue...
```

## Code Review Guidelines

### Required Checks
- [ ] Follows package structure conventions
- [ ] Uses proper dependency injection
- [ ] Includes appropriate logging
- [ ] Has corresponding tests
- [ ] Handles errors appropriately
- [ ] Documents configuration usage
- [ ] Follows naming conventions
- [ ] No security vulnerabilities
- [ ] **Branch workflow followed correctly**
- [ ] **All tests pass in feature branch**
- [ ] **No merge conflicts with master**

### Performance Considerations
- [ ] Caches expensive operations
- [ ] Uses efficient data structures
- [ ] Manages resources properly
- [ ] Handles large datasets appropriately

## Common Patterns to Follow

### 1. Service Implementation Pattern
```java
@Service
public class NewService extends GoogleService implements PopulateInterface {
    
    public NewService(Settings settings) throws Exception {
        super(settings);
        log = LoggerFactory.getLogger(NewService.class);
    }
    
    @Override
    public String populate(String template, String user) throws Exception {
        // Implementation
    }
}
```

### 2. Data Processing Pattern
```java
public TreeSet<DataType> processData(String identifier) throws Exception {
    log.debug("Processing data for: {}", identifier);
    
    HashMap personalSettings = settings.getPersonalSettings(identifier);
    TreeSet<DataType> results = new TreeSet<>();
    
    try {
        // Process data
        logResults(results);
        return results;
    } catch (Exception e) {
        log.error("Failed to process data for: {}", identifier, e);
        throw new ServiceException("Data processing failed", e);
    }
}
```

## Tools and IDE Configuration

### Recommended Settings
- **Indentation**: 4 spaces (no tabs)
- **Line Length**: 120 characters
- **Import Organization**: Group by package, static imports last
- **Code Formatting**: Follow existing patterns in codebase

### Required Plugins
- Spring Boot support
- Maven integration
- Git integration

---

*This document should be updated as the project evolves and new patterns emerge. All agents should familiarize themselves with these standards before making changes to the codebase.*