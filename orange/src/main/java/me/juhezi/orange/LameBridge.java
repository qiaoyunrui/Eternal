package me.juhezi.orange;

public class LameBridge {

    static {
        System.loadLibrary("orange");
    }

    public static native int init(String pcmPath,
                                  int audioChannels,
                                  int bitRate,
                                  int sampleRate,
                                  String mp3Path);

    public static native void encode();

    public static native void destroy();

}
