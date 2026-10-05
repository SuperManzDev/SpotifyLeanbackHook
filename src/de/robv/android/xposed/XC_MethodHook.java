package de.robv.android.xposed;
public abstract class XC_MethodHook {
    public static class MethodHookParam {
        public Object thisObject;
        public Object[] args;
        public Object result;
        public void setResult(Object result) { this.result = result; }
        public Object getResult() { return result; }
    }
    public static class Unhook {}
    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {}
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {}
}
