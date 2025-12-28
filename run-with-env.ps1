# run-with-env.ps1
# Reads .env file and sets environment variables before running the Spring Boot application

# Check if .env file exists
if (Test-Path ".env") {
    Write-Host "Loading environment variables from .env file..." -ForegroundColor Green
    
    # Read .env file line by line
    Get-Content ".env" | ForEach-Object {
        if ($_ -match '^([^#=]+)=(.*)$') {
            $name = $matches[1].Trim()
            $value = $matches[2].Trim()
            # Remove quotes if present
            $value = $value -replace '^["'']|["'']$', ''
            
            Write-Host "Setting $name = $value" -ForegroundColor Cyan
            Set-Item -Path "env:$name" -Value $value
        }
    }
    
    Write-Host "Environment variables loaded. Starting application..." -ForegroundColor Green
} else {
    Write-Host ".env file not found. Starting application with current environment..." -ForegroundColor Yellow
}

# Run the Spring Boot application
& ./mvnw spring-boot:run