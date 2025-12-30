#!/bin/bash
# RPi Startup Script

# 1. Pull latest code (if using git on RPi)
# git pull

# 2. Check desired mode (color vs grayscale)
# Logic: Could check a local config file or ENV var.
MODE="${DISPLAY_MODE:-color}"

if [ "$MODE" == "color" ]; then
    # Launch Chromium in Kiosk Mode
    # --kiosk: Fullscreen, no bars
    # --noerrdialogs: Suppress error dialogs
    # --disable-infobars: Remove "Chrome is being controlled..." (if applicable)
    ID="${USER_ID:-65a75e2e}"
    export DISPLAY=:0
    chromium-browser --kiosk --noerrdialogs --disable-infobars "http://localhost:3000/?mode=color&id=$ID" --check-for-update-interval=31536000
    
elif [ "$MODE" == "grayscale" ]; then
    # Run Node script to handle E-ink update
    # Assume node is installed
    cd /home/pi/infoskjermen/rpi-client
    node display-loader.js
fi
