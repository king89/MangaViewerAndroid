echo "Build"
./gradlew app/assembleDebug
echo "Install"
adb install -r "./app/build/outputs/apk/app-debug.apk"