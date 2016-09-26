@echo off
echo "Build"
call gradlew assembleDebug
echo "Install"
adb install -r ".\app\build\outputs\apk\app-debug.apk"