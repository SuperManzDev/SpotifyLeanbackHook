@echo off
setlocal enabledelayedexpansion
echo [*] Building SpotifyLeanbackHook APK...

set SDK_BUILD_TOOLS=%LOCALAPPDATA%\Android\Sdk\build-tools\34.0.0
set FRAMEWORK_JAR=%TEMP%\framework.jar
set FRAMEWORK_RES=%TEMP%\framework-res.apk

if not exist "%FRAMEWORK_JAR%" (
    echo [!] Pulling framework.jar from device...
    adb pull /system/framework/framework.jar "%FRAMEWORK_JAR%"
)
if not exist "%FRAMEWORK_RES%" (
    echo [!] Pulling framework-res.apk from device...
    adb pull /system/framework/framework-res.apk "%FRAMEWORK_RES%"
)

if exist classes rd /s /q classes
mkdir classes
mkdir release 2>nul

echo [*] Compiling Java...
dir /s /b src\*.java > sources.txt
javac --release 8 -cp "%FRAMEWORK_JAR%" -d classes @sources.txt
del sources.txt

echo [*] Converting to DEX...
call "%SDK_BUILD_TOOLS%\d8.bat" --lib "%FRAMEWORK_JAR%" --output . --min-api 26 classes\dev\manz\leanbackhook\*.class

echo [*] Packaging APK...
"%SDK_BUILD_TOOLS%\aapt.exe" package -f -M AndroidManifest.xml -S res -I "%FRAMEWORK_RES%" -F unsigned.apk
"%SDK_BUILD_TOOLS%\aapt.exe" add unsigned.apk classes.dex
"%SDK_BUILD_TOOLS%\aapt.exe" add unsigned.apk assets/xposed_init

echo [*] Aligning and Signing...
"%SDK_BUILD_TOOLS%\zipalign.exe" -f -p 4 unsigned.apk aligned.apk
call "%SDK_BUILD_TOOLS%\apksigner.bat" sign --ks "%TEMP%\debug.keystore" --ks-pass pass:android --ks-key-alias androiddebugkey --key-pass pass:android --out release\SpotifyLeanbackHook-v1.0.apk aligned.apk

del unsigned.apk aligned.apk classes.dex 2>nul
rd /s /q classes 2>nul

echo [?] Build complete: release\SpotifyLeanbackHook-v1.0.apk
pause
