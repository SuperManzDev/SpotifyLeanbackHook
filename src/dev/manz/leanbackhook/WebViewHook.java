package dev.manz.leanbackhook;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import java.lang.reflect.Method;

public class WebViewHook extends XC_MethodHook {
    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
        if (param != null && param.thisObject != null) {
            Object webView = param.thisObject;
            try {
                Class<?> cbClass = Class.forName("android.webkit.ValueCallback");
                Method evalMethod = webView.getClass().getMethod("evaluateJavascript", String.class, cbClass);
                String js = "(function() {" +
                            "  var style = document.getElementById('manz_fullscreen_fix');" +
                            "  if (!style) {" +
                            "    style = document.createElement('style');" +
                            "    style.id = 'manz_fullscreen_fix';" +
                            "    style.innerHTML = 'html, body, #root, #app, .app, div[data-testid], main, .media-container { height: 100vh !important; min-height: 100vh !important; max-height: 100vh !important; }';" +
                            "    document.head.appendChild(style);" +
                            "  }" +
                            "})();";
                evalMethod.invoke(webView, js, null);
                XposedBridge.log("[SpotifyLeanbackHook] Injected fullscreen layout CSS into WebView");
            } catch (Throwable ignored) {}
        }
    }
}
