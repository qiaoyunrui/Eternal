package me.juhezi.orange;

public class GPUImage {

    static {
        System.loadLibrary("orange");
    }

    public static native void onSurfaceCreated();

    public static native void onSurfaceChanged(int width, int height);

    public static native void onDrawFrame();

}
