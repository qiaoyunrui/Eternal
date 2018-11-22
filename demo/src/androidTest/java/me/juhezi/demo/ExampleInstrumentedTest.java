package me.juhezi.demo;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

import me.juhezi.orange.FFmpegBridge;
import me.juhezi.orange.LameBridge;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    private static final String TAG = "ExampleInstrumentedTest";

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("me.juhezi.demo", appContext.getPackageName());
    }

    @Test
    public void mp3EncodeTest() {
        File file = new File("/storage/emulated/0/output.mp3");
        file.delete();
        LameBridge.init("/storage/emulated/0/input1.pcm", 1, 16, 44100, "/storage/emulated/0/output.mp3");
        LameBridge.encode();
        LameBridge.destroy();
        assertTrue(new File("/storage/emulated/0/output.mp3").exists());
    }

    // error 无法加载 so
    @Test
    public void remuxTest() {
        assertTrue(FFmpegBridge.remux("storage/emulated/0/demo.mp4", "storage/emulated/0/demo.flv") == 0
                && new File("storage/emulated/0/demo.flv").exists());
    }

}
