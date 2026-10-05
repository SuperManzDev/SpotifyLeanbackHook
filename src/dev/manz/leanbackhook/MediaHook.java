package dev.manz.leanbackhook;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class MediaHook extends XC_MethodHook {
    @Override
    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
        if (param == null || param.args == null || param.args.length == 0) return;

        // Force MediaSession.setActive(true)
        if (param.args[0] instanceof Boolean) {
            param.args[0] = Boolean.TRUE;
            XposedBridge.log("[SpotifyLeanbackHook] Forced MediaSession.setActive(true)");
            return;
        }

        // Force PlaybackState to STATE_PLAYING (3) so ClockDesk & LockScreen widgets receive active music
        Object arg0 = param.args[0];
        if (arg0 != null && arg0.getClass().getName().contains("PlaybackState")) {
            try {
                long pos = 0;
                try {
                    Method getPos = arg0.getClass().getMethod("getPosition");
                    pos = ((Long) getPos.invoke(arg0)).longValue();
                } catch (Throwable ignored) {}

                Class<?> builderClass = Class.forName("android.media.session.PlaybackState$Builder", false, arg0.getClass().getClassLoader());
                Constructor<?> ctor = builderClass.getConstructor();
                Object builder = ctor.newInstance();

                Method setState = builderClass.getMethod("setState", int.class, long.class, float.class, long.class);
                Class<?> sysClock = Class.forName("android.os.SystemClock");
                Method elapsedRealtime = sysClock.getMethod("elapsedRealtime");
                long now = ((Long) elapsedRealtime.invoke(null)).longValue();

                // State 3 = STATE_PLAYING
                setState.invoke(builder, 3, pos, 1.0f, now);

                Method setActions = builderClass.getMethod("setActions", long.class);
                setActions.invoke(builder, 518L); // Play, Pause, Next, Prev

                Method build = builderClass.getMethod("build");
                param.args[0] = build.invoke(builder);
                XposedBridge.log("[SpotifyLeanbackHook] Rewrote PlaybackState to STATE_PLAYING for ClockDesk");
            } catch (Throwable t) {
                XposedBridge.log("[SpotifyLeanbackHook] PlaybackState hook reflection error: " + t);
            }
        }
    }
}
