package dev.manz.leanbackhook;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;

public class UiModeHook extends XC_MethodHook {
    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
        // UI_MODE_TYPE_TELEVISION = 4
        param.setResult(Integer.valueOf(4));
        XposedBridge.log("[SpotifyLeanbackHook] getCurrentModeType -> UI_MODE_TYPE_TELEVISION (4)");
    }
}
