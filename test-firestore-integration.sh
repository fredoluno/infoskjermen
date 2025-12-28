#!/bin/bash

# Firestore Integration Test Runner
# This script helps set up and run Firestore integration tests

set -e

echo "🔥 Firestore Integration Test Runner"
echo "===================================="

# Check for environment setup
check_environment() {
    echo "🔍 Checking environment setup..."
    
    if [ -n "$FIRESTORE_EMULATOR_HOST" ]; then
        echo "✅ Firestore emulator detected at: $FIRESTORE_EMULATOR_HOST"
        return 0
    fi
    
    if [ -n "$GOOGLE_APPLICATION_CREDENTIALS" ] && [ -f "$GOOGLE_APPLICATION_CREDENTIALS" ]; then
        echo "✅ Service account credentials found: $GOOGLE_APPLICATION_CREDENTIALS"
        return 0
    fi
    
    # Check for gcloud auth
    if gcloud auth list --filter=status:ACTIVE --format="value(account)" >/dev/null 2>&1; then
        if gcloud auth application-default print-access-token >/dev/null 2>&1; then
            echo "✅ Application default credentials are configured"
            return 0
        fi
    fi
    
    echo "❌ No valid Firestore authentication found!"
    echo ""
    echo "Choose one of these options:"
    echo "1. Start Firestore emulator: gcloud beta emulators firestore start --host-port=localhost:8080"
    echo "   Then set: export FIRESTORE_EMULATOR_HOST=localhost:8080"
    echo ""
    echo "2. Use service account: export GOOGLE_APPLICATION_CREDENTIALS=/path/to/key.json"
    echo ""
    echo "3. Use application default: gcloud auth application-default login"
    echo ""
    return 1
}

# Start Firestore emulator if requested
start_emulator() {
    if [ "$1" = "--emulator" ]; then
        echo "🚀 Starting Firestore emulator..."
        
        # Check if emulator is already running
        if curl -s http://localhost:8080 >/dev/null 2>&1; then
            echo "⚠️  Emulator appears to already be running on port 8080"
        else
            echo "Starting emulator in background..."
            gcloud beta emulators firestore start --host-port=localhost:8080 &
            EMULATOR_PID=$!
            
            # Wait for emulator to start
            echo "Waiting for emulator to be ready..."
            for i in {1..30}; do
                if curl -s http://localhost:8080 >/dev/null 2>&1; then
                    echo "✅ Emulator is ready!"
                    break
                fi
                sleep 1
            done
        fi
        
        export FIRESTORE_EMULATOR_HOST=localhost:8080
        echo "Set FIRESTORE_EMULATOR_HOST=localhost:8080"
    fi
}

# Run the integration tests
run_tests() {
    echo "🧪 Running Firestore integration tests..."
    
    # Enable integration tests
    export FIRESTORE_INTEGRATION_ENABLED=true
    
    # Run only the integration tests
    ./mvnw test -Dtest="FirestoreIntegrationTest" -Dspring.profiles.active=test
    
    local exit_code=$?
    
    if [ $exit_code -eq 0 ]; then
        echo "✅ All Firestore integration tests passed!"
    else
        echo "❌ Some integration tests failed!"
    fi
    
    return $exit_code
}

# Cleanup function
cleanup() {
    if [ -n "$EMULATOR_PID" ]; then
        echo "🧹 Stopping Firestore emulator..."
        kill $EMULATOR_PID 2>/dev/null || true
    fi
}

# Set trap for cleanup
trap cleanup EXIT

# Main execution
case "${1:-}" in
    --emulator)
        start_emulator --emulator
        if check_environment; then
            run_tests
        fi
        ;;
    --help|-h)
        echo "Usage: $0 [--emulator] [--help]"
        echo ""
        echo "Options:"
        echo "  --emulator    Start Firestore emulator before running tests"
        echo "  --help        Show this help message"
        echo ""
        echo "Environment variables:"
        echo "  FIRESTORE_EMULATOR_HOST         Use Firestore emulator"
        echo "  GOOGLE_APPLICATION_CREDENTIALS  Use service account key"
        echo "  FIRESTORE_INTEGRATION_ENABLED   Enable integration tests"
        ;;
    *)
        if check_environment; then
            run_tests
        else
            echo ""
            echo "Run '$0 --help' for setup instructions"
            exit 1
        fi
        ;;
esac