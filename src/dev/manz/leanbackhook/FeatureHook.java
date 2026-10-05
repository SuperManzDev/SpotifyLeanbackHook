package dev.manz.leanbackhook;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;

public class FeatureHook extends XC_MethodHook {
    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
        if (param != null && param.args != null && param.args.length > 0) {
            Object arg0 = param.args[0];
            if (arg0 instanceof String) {
                String name = (String) arg0;
                if ("android.software.leanback".equals(name) ||
                    "android.software.leanback_only".equals(name) ||
                    "android.hardware.type.television".equals(name) ||
                    "android.software.live_tv".equals(name)) {
                    param.setResult(Boolean.TRUE);
                    XposedBridge.log("[SpotifyLeanbackHook] hasSystemFeature(" + name + ") -> TRUE");
                }
            }
        }
    }
}
