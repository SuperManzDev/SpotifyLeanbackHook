package dev.manz.leanbackhook;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import java.lang.reflect.Method;

public class HookInit implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (lpparam == null || lpparam.packageName == null) return;

        // Global split-screen force hook for unsupported apps
        try {
            Class<?> activityInfoClass = Class.forName("android.content.pm.ActivityInfo", false, lpparam.classLoader);
            ForceResizeHook rHook = new ForceResizeHook();
            for (Method m : activityInfoClass.getDeclaredMethods()) {
                if (m.getName().equals("supportsSplitScreenMultiWindow") || m.getName().equals("isResizeable")) {
                    XposedBridge.hookMethod(m, rHook);
                }
            }
        } catch (Throwable ignored) {}

        // Target Spotify TV specifically
        if (!lpparam.packageName.equals("com.spotify.tv.android")) return;

        XposedBridge.log("[SpotifyLeanbackHook] Attaching hooks to Spotify TV...");

        // 1. Hook ApplicationPackageManager.hasSystemFeature
        try {
            Class<?> pmsClass = Class.forName("android.app.ApplicationPackageManager", false, lpparam.classLoader);
            FeatureHook fHook = new FeatureHook();
            for (Method m : pmsClass.getDeclaredMethods()) {
                if (m.getName().equals("hasSystemFeature")) {
                    XposedBridge.hookMethod(m, fHook);
                }
            }
            XposedBridge.log("[SpotifyLeanbackHook] Hooked hasSystemFeature successfully");
        } catch (Throwable t) {
            XposedBridge.log("[SpotifyLeanbackHook] Failed to hook PackageManager: " + t);
        }

        // 2. Hook UiModeManager.getCurrentModeType
        try {
            Class<?> uiModeClass = Class.forName("android.app.UiModeManager", false, lpparam.classLoader);
            UiModeHook uHook = new UiModeHook();
            for (Method m : uiModeClass.getDeclaredMethods()) {
                if (m.getName().equals("getCurrentModeType")) {
                    XposedBridge.hookMethod(m, uHook);
                }
            }
            XposedBridge.log("[SpotifyLeanbackHook] Hooked UiModeManager successfully");
        } catch (Throwable t) {
            XposedBridge.log("[SpotifyLeanbackHook] Failed to hook UiModeManager: " + t);
        }

        // 3. Hook MediaSession (setActive & setPlaybackState)
        try {
            Class<?> msClass = Class.forName("android.media.session.MediaSession", false, lpparam.classLoader);
            MediaHook mHook = new MediaHook();
            for (Method m : msClass.getDeclaredMethods()) {
                if (m.getName().equals("setActive") || m.getName().equals("setPlaybackState")) {
                    XposedBridge.hookMethod(m, mHook);
                }
            }
            XposedBridge.log("[SpotifyLeanbackHook] Hooked MediaSession.setActive and setPlaybackState");
        } catch (Throwable t) {
            XposedBridge.log("[SpotifyLeanbackHook] Failed to hook MediaSession: " + t);
        }

        // 4. Hook WebView to inject full-height CSS into Spotify TV webview
        try {
            Class<?> webViewClass = Class.forName("android.webkit.WebView", false, lpparam.classLoader);
            WebViewHook wHook = new WebViewHook();
            for (Method m : webViewClass.getDeclaredMethods()) {
                if (m.getName().equals("loadUrl") || m.getName().equals("postVisualStateCallback")) {
                    XposedBridge.hookMethod(m, wHook);
                }
            }
            XposedBridge.log("[SpotifyLeanbackHook] Hooked WebView methods for full-screen layout");
        } catch (Throwable t) {
            XposedBridge.log("[SpotifyLeanbackHook] Failed to hook WebView: " + t);
        }
    }
}
