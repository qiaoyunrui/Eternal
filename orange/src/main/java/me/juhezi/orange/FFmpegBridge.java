package me.juhezi.orange;

public class FFmpegBridge {

    static {
        System.loadLibrary("orange");
    }

    // ffmpeg 转封装
    public static native int remux(String inputFilePath, String outputFilePath);
}
