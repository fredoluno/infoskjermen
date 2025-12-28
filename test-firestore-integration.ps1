# Firestore Integration Test Runner (PowerShell)
# This script helps set up and run Firestore integration tests on Windows

param(
    [switch]$Emulator,
    [switch]$Help
)

Write-Host "🔥 Firestore Integration Test Runner" -ForegroundColor Cyan
Write-Host "====================================" -ForegroundColor Cyan

function Check-Environment {
    Write-Host "🔍 Checking environment setup..." -ForegroundColor Yellow
    
    if ($env:FIRESTORE_EMULATOR_HOST) {
        Write-Host "✅ Firestore emulator detected at: $env:FIRESTORE_EMULATOR_HOST" -ForegroundColor Green
        return $true
    }
    
    if ($env:GOOGLE_APPLICATION_CREDENTIALS -and (Test-Path $env:GOOGLE_APPLICATION_CREDENTIALS)) {
        Write-Host "✅ Service account credentials found: $env:GOOGLE_APPLICATION_CREDENTIALS" -ForegroundColor Green
        return $true
    }
    
    # Check for gcloud auth
    try {
        $authAccount = gcloud auth list --filter=status:ACTIVE --format="value(account)" 2>$null
        if ($authAccount) {
            $accessToken = gcloud auth application-default print-access-token 2>$null
            if ($accessToken) {
                Write-Host "✅ Application default credentials are configured" -ForegroundColor Green
                return $true
            }
        }
    }
    catch {
        # gcloud not available or not authenticated
    }
    
    Write-Host "❌ No valid Firestore authentication found!" -ForegroundColor Red
    Write-Host ""
    Write-Host "Choose one of these options:" -ForegroundColor Yellow
    Write-Host "1. Start Firestore emulator: gcloud beta emulators firestore start --host-port=localhost:8080"
    Write-Host "   Then set: `$env:FIRESTORE_EMULATOR_HOST='localhost:8080'"
    Write-Host ""
    Write-Host "2. Use service account: `$env:GOOGLE_APPLICATION_CREDENTIALS='C:\path\to\key.json'"
    Write-Host ""
    Write-Host "3. Use application default: gcloud auth application-default login"
    Write-Host ""
    return $false
}

function Start-Emulator {
    if ($Emulator) {
        Write-Host "🚀 Starting Firestore emulator..." -ForegroundColor Yellow
        
        # Check if emulator is already running
        try {
            $response = Invoke-WebRequest -Uri "http://localhost:8080" -TimeoutSec 2 -ErrorAction SilentlyContinue
            Write-Host "⚠️  Emulator appears to already be running on port 8080" -ForegroundColor Yellow
        }
        catch {
            Write-Host "Starting emulator in background..."
            $emulatorJob = Start-Job -ScriptBlock {
                gcloud beta emulators firestore start --host-port=localhost:8080
            }
            
            # Wait for emulator to start
            Write-Host "Waiting for emulator to be ready..."
            $ready = $false
            for ($i = 1; $i -le 30; $i++) {
                try {
                    $response = Invoke-WebRequest -Uri "http://localhost:8080" -TimeoutSec 2 -ErrorAction SilentlyContinue
                    Write-Host "✅ Emulator is ready!" -ForegroundColor Green
                    $ready = $true
                    break
                }
                catch {
                    Start-Sleep 1
                }
            }
            
            if (-not $ready) {
                Write-Host "❌ Emulator failed to start within 30 seconds" -ForegroundColor Red
                return $false
            }
        }
        
        $env:FIRESTORE_EMULATOR_HOST = "localhost:8080"
        Write-Host "Set FIRESTORE_EMULATOR_HOST=localhost:8080"
    }
    return $true
}

function Run-Tests {
    Write-Host "🧪 Running Firestore integration tests..." -ForegroundColor Yellow
    
    # Enable integration tests
    $env:FIRESTORE_INTEGRATION_ENABLED = "true"
    
    # Run only the integration tests
    $result = & .\mvnw.cmd test "-Dtest=FirestoreIntegrationTest" "-Dspring.profiles.active=test"
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✅ All Firestore integration tests passed!" -ForegroundColor Green
        return $true
    } else {
        Write-Host "❌ Some integration tests failed!" -ForegroundColor Red
        return $false
    }
}

function Show-Help {
    Write-Host "Usage: .\test-firestore-integration.ps1 [-Emulator] [-Help]"
    Write-Host ""
    Write-Host "Options:"
    Write-Host "  -Emulator     Start Firestore emulator before running tests"
    Write-Host "  -Help         Show this help message"
    Write-Host ""
    Write-Host "Environment variables:"
    Write-Host "  FIRESTORE_EMULATOR_HOST         Use Firestore emulator"
    Write-Host "  GOOGLE_APPLICATION_CREDENTIALS  Use service account key"
    Write-Host "  FIRESTORE_INTEGRATION_ENABLED   Enable integration tests"
    Write-Host ""
    Write-Host "Examples:"
    Write-Host "  .\test-firestore-integration.ps1              # Run with existing auth"
    Write-Host "  .\test-firestore-integration.ps1 -Emulator   # Start emulator and run tests"
}

# Main execution
if ($Help) {
    Show-Help
    exit 0
}

$emulatorStarted = Start-Emulator

if ($emulatorStarted -and (Check-Environment)) {
    $testResult = Run-Tests
    if (-not $testResult) {
        exit 1
    }
} else {
    Write-Host ""
    Write-Host "Run '.\test-firestore-integration.ps1 -Help' for setup instructions" -ForegroundColor Yellow
    exit 1
}

Write-Host "🎉 Integration tests completed successfully!" -ForegroundColor Green