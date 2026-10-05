# Spotify TV Leanback Hook for Amazon Echo Show 8 (LineageOS / Android TV)

[![License: MIT](https://img.shields.io/badge/License-MIT-green.svg)](https://opensource.org/licenses/MIT)
[![LSPosed](https://img.shields.io/badge/LSPosed-v1.9.2+-blue.svg)](https://github.com/LSPosed/LSPosed)
[![Android](https://img.shields.io/badge/Android-11%20(API%2030)-orange.svg)](https://developer.android.com)
[![Platform](https://img.shields.io/badge/Platform-Amazon%20Echo%20Show%208-red.svg)](https://github.com)

A lightweight, dedicated **LSPosed (Xposed) Module** engineered for the **Amazon Echo Show 8 (1st Gen / `crown`)** and other tablet / custom ROM Android devices running pure AOSP or LineageOS.

This module resolves the compatibility barrier preventing **Spotify Android TV (`com.spotify.tv.android`)** from running on non-TV ROMs without breaking tablet `SystemUI`.

---

## ?? The Problem

When running **Spotify Android TV** on pure Android / LineageOS:
1. **Activity Termination:** Spotify TV checks `PackageManager.hasSystemFeature("android.software.leanback")` and `UiModeManager.getCurrentModeType()`. If the device is not detected as an Android TV, Spotify logs `[SpotifyTVPresenterImpl] Spotify is not allowed to run. Terminating Activity` and immediately aborts.
2. **SystemUI Crash on Global XMLs:** Adding `android.software.leanback.xml` globally to `/system/etc/permissions/` causes LineageOS's Phone/Tablet `SystemUI` to crash in a bootloop (`NullPointerException` on `NotificationShadeWindowView StatusBar.getNotificationShadeWindowView()`).
3. **Hidden Media Session:** Spotify TV runs with `MediaSession.setActive(false)` and `PlaybackState(state=0)`, preventing desk clock and lockscreen widgets from displaying live playback info.

---

## ?? Key Features

* **Targeted Feature Spoofing:** Spoofs `android.software.leanback`, `android.hardware.type.television`, and `UI_MODE_TYPE_TELEVISION` (4) **exclusively** inside Spotify's process. Leaves `SystemUI` untouched and 100% stable.
* **MediaSession Bridge for DeskClock:** Intercepts `MediaSession.setActive(true)` and rewrites `PlaybackState` to `STATE_PLAYING` (3) so clock screens (such as **ClockDesk**) display real-time song titles, artist names, and dynamic full-screen album artwork.
* **Full-Height Layout CSS Injection:** Hooks WebView rendering to ensure Spotify TV fills 100% of the display canvas.
* **Universal Split-Screen Force Hook:** Overrides `ActivityInfo.isResizeable` and `supportsSplitScreenMultiWindow` to enable multi-window / split-screen on apps with `resizeableActivity="false"` (e.g. Spacedesk, Spotify TV).

---

## ?? Project Structure

```
SpotifyLeanbackHook/
+-- AndroidManifest.xml              # Module manifest and scope definitions
+-- build.bat                        # Automated 1-click Windows build script
+-- README.md                        # Project documentation
+-- release/
   +-- SpotifyLeanbackHook-v1.0.apk # Compiled, zipaligned & signed APK
   +-- SpotifyLeanbackHook-v1.0-release.zip # Complete release archive
+-- assets/
   +-- xposed_init                  # Xposed entry point
+-- res/
   +-- values/
       +-- arrays.xml               # LSPosed target scope array
+-- src/
    +-- de/robv/android/xposed/      # Xposed API stubs
    +-- dev/manz/leanbackhook/       # Core hook implementations
        +-- HookInit.java            # Main Xposed entry point
        +-- FeatureHook.java         # Leanback & TV feature spoofing
        +-- UiModeHook.java          # TV UI mode interceptor
        +-- MediaHook.java           # MediaSession & PlaybackState bridge
        +-- WebViewHook.java         # Fullscreen WebView CSS injection
        +-- ForceResizeHook.java     # Split-screen resize override
```

---

## ??? Installation & Setup

### Prerequisites
* Rooted Android device with **Magisk** (v26+ / v30+).
* **Zygisk** enabled in Magisk settings.
* **LSPosed Framework (Zygisk release)** installed and active.

### Quick Install via ADB
```bash
# 1. Install the module APK
adb install -r release/SpotifyLeanbackHook-v1.0.apk

# 2. Open LSPosed Manager
adb shell monkey -p org.lsposed.manager 1

# 3. In LSPosed Manager:
#    - Navigate to the "Modules" tab.
#    - Select "Spotify TV Leanback Hook".
#    - Toggle "Enable Module" ON.
#    - Ensure "com.spotify.tv.android" is checked in the scope list.

# 4. Launch Spotify TV
adb shell am start -n com.spotify.tv.android/.SpotifyTVActivity
```

---

## ?? Web Remote Controller Included

This repository includes a standalone Python web remote controller for the Echo Show 8:

```bash
# Launch the web remote controller
python echo_remote.py
```
* Access from PC: `http://localhost:8765`
* Access from Phone / LAN: `http://<YOUR-PC-IP>:8765`

### Web Remote Features:
* **Full D-Pad:** Up, Down, Left, Right, OK/Select.
* **Playback Controls:** Play/Pause, Next Track, Prev Track, Vol +, Vol -, Mute.
* **Live Screen Mirror:** Auto-refreshing 2-second screen capture preview.
* **1-Click App Switcher:** Switch between Spotify TV, DeskClock, Spacedesk, and Settings.
* **Keyboard Hotkeys:** Control the Echo Show using PC Arrow keys, Enter, Space, and Esc.

---

## ?? Building from Source

To compile the module from source on Windows:

1. Ensure JDK 17+ or JDK 21 is in your `PATH`.
2. Ensure Android Build Tools (e.g. `34.0.0`) is installed in `%LOCALAPPDATA%\Android\Sdk\build-tools\34.0.0`.
3. Run:
```cmd
build.bat
```
The script will pull required framework jars from your connected device, compile Java sources, dex with `d8`, package with `aapt`, and sign the output APK in `release/SpotifyLeanbackHook-v1.0.apk`.

---

## ?? License

Distributed under the **MIT License**. Free for personal and commercial use.
