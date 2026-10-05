package dev.manz.leanbackhook;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;

public class ForceResizeHook extends XC_MethodHook {
    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
        // Force ActivityInfo.resizeMode = RESIZE_MODE_RESIZEABLE (2)
        try {
            if (param != null && param.thisObject != null) {
                java.lang.reflect.Field f = param.thisObject.getClass().getField("resizeMode");
                f.setInt(param.thisObject, 2);
            }
        } catch (Throwable ignored) {}
    }
}
