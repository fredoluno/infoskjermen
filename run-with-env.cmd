@echo off
REM run-with-env.cmd
REM Reads .env file and sets environment variables before running the Spring Boot application

echo Loading environment variables from .env file...

REM Check if .env file exists
if not exist .env (
    echo .env file not found. Starting application with current environment...
    goto :runapp
)

REM Read .env file and set environment variables
for /f "usebackq delims== tokens=1,*" %%A in (.env) do (
    REM Skip lines starting with # (comments)
    echo %%A | findstr /r "^#" >nul
    if errorlevel 1 (
        REM Remove quotes from value if present
        set "value=%%B"
        set "value=%value:"=%"
        echo Setting %%A = !value!
        set "%%A=!value!"
    )
)

echo Environment variables loaded. Starting application...

:runapp
REM Run the Spring Boot application
call mvnw.cmd spring-boot:run