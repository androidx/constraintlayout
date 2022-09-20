package androidx.demo.lua;

public class NativeLib {

    // Used to load the 'lua' library on application startup.
    static {
        System.loadLibrary("lua");
    }

    /**
     * A native method that is implemented by the 'lua' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI(String code,String answer);
}