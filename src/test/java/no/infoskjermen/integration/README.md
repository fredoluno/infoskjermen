# Firestore Integration Tests

This directory contains integration tests for Firestore database connectivity and operations. These tests verify that the application can successfully connect to and interact with a Firestore database.

## Test Structure

- **`FirestoreIntegrationTest.java`** - Main integration test class with comprehensive database operation tests
- **`application-integration.properties.example`** - Example configuration for integration tests

## Test Coverage

The integration tests cover:

1. **Database Connectivity** - Verifying successful connection to Firestore
2. **Settings Retrieval** - Testing all settings types (Google, Netatmo, Entur, Yr, Display)
3. **Caching Behavior** - Ensuring document caching works correctly
4. **Error Handling** - Testing graceful fallback to default settings
5. **Write Operations** - Testing Netatmo refresh token updates
6. **Non-existent Data** - Testing behavior with missing documents

## Running the Tests

### Prerequisites

Choose one of these authentication methods:

#### Option 1: Firestore Emulator (Recommended for CI/CD)
```bash
# Install the emulator (choose based on your environment):
# For CI/CD (Ubuntu/Debian):
sudo apt-get install -y google-cloud-cli-firestore-emulator

# For local development (if gcloud CLI allows):
gcloud components install cloud-firestore-emulator

# Start the emulator
gcloud beta emulators firestore start --host-port=localhost:8080

# Set environment variable
export FIRESTORE_EMULATOR_HOST=localhost:8080
```

#### Option 2: Service Account Key
```bash
# Download service account key from Google Cloud Console
export GOOGLE_APPLICATION_CREDENTIALS="/path/to/service-account-key.json"
```

#### Option 3: Application Default Credentials
```bash
# Authenticate with gcloud
gcloud auth application-default login
```

### Running Tests

#### Using the Test Scripts (Recommended)

**Linux/Mac:**
```bash
# Run with existing authentication
./test-firestore-integration.sh

# Start emulator automatically and run tests
./test-firestore-integration.sh --emulator

# Show help
./test-firestore-integration.sh --help
```

**Windows PowerShell:**
```powershell
# Run with existing authentication
.\test-firestore-integration.ps1

# Start emulator automatically and run tests  
.\test-firestore-integration.ps1 -Emulator

# Show help
.\test-firestore-integration.ps1 -Help
```

#### Using Maven Directly

```bash
# Enable integration tests and run
export FIRESTORE_INTEGRATION_ENABLED=true
./mvnw test -Dtest="FirestoreIntegrationTest"
```

## Configuration

### Environment Variables

- **`FIRESTORE_INTEGRATION_ENABLED=true`** - Required to enable integration tests
- **`FIRESTORE_EMULATOR_HOST`** - Use Firestore emulator (e.g., `localhost:8080`)
- **`GOOGLE_APPLICATION_CREDENTIALS`** - Path to service account key JSON file

### Test Data Structure

The tests expect a Firestore collection structure like this:

```
settings/
├── general/
│   ├── google: { calendar: "primary", display_calendar: "primary" }
│   ├── netatmo: { client_id: "...", client_secret: "..." }
│   ├── entur: { stopplace_id: "NSR:StopPlace:58366" }
│   ├── yr: { latitude: "59.9139", longitude: "10.7522" }
│   └── display: { theme: "default" }
└── integration-test-user/
    └── (test data created/modified during tests)
```

## Test Behavior

### Conditional Execution

Tests are conditionally executed using `@EnabledIfEnvironmentVariable`:
- Tests only run when `FIRESTORE_INTEGRATION_ENABLED=true`
- Tests are skipped if no authentication is available
- Graceful fallback to default settings is always tested

### Test Isolation

- Each test clears the cache before execution
- Tests use isolated test user data where possible
- Write operations use timestamped test data to avoid conflicts

### Error Handling

- Tests verify that the application gracefully handles Firestore unavailability
- Default settings fallback behavior is thoroughly tested
- Authentication failures result in test skips, not failures

## Troubleshooting

### Common Issues

1. **Tests are skipped**
   - Verify `FIRESTORE_INTEGRATION_ENABLED=true` is set
   - Check that authentication is properly configured

2. **Connection timeouts**
   - Verify Firestore emulator is running and accessible
   - Check network connectivity for real Firestore

3. **Permission denied errors**
   - Verify service account has Firestore permissions
   - Check that project ID is correct

4. **Tests fail with "Document not found"**
   - Ensure test data exists in Firestore
   - Or rely on default settings fallback behavior

### Debug Logging

Enable debug logging for integration tests:

```properties
logging.level.no.infoskjermen.integration=DEBUG
logging.level.no.infoskjermen.Settings=DEBUG
```

## CI/CD Integration

For continuous integration, use the Firestore emulator:

```yaml
# Example GitHub Actions
- name: Start Firestore Emulator
  run: |
    sudo apt-get update
    sudo apt-get install -y google-cloud-cli-firestore-emulator
    gcloud beta emulators firestore start --host-port=localhost:8080 &
    
- name: Run Integration Tests
  env:
    FIRESTORE_EMULATOR_HOST: localhost:8080
    FIRESTORE_INTEGRATION_ENABLED: true
  run: ./mvnw test -Dtest="FirestoreIntegrationTest"
```

## Maintenance

- Update test data structure when Settings class changes
- Verify tests work with new Firestore SDK versions
- Keep authentication examples current with Google Cloud best practices