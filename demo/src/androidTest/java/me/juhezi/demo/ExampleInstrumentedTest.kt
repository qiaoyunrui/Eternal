package me.juhezi.demo

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import me.juhezi.eternal.media.trackExtractor
import me.juhezi.eternal.media.trackMuxer
import me.juhezi.orange.FFmpegBridge
import me.juhezi.orange.LameBridge
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getTargetContext()

        assertEquals("me.juhezi.demo", appContext.packageName)
    }

    @Test
    fun mp3EncodeTest() {
        val file = File("/storage/emulated/0/output.mp3")
        file.delete()
        LameBridge.init("/storage/emulated/0/input1.pcm", 1, 16, 44100, "/storage/emulated/0/output.mp3")
        LameBridge.encode()
        LameBridge.destroy()
        assertTrue(File("/storage/emulated/0/output.mp3").exists())
    }

    // error 无法加载 so
    @Test
    fun remuxTest() {
        assertTrue(FFmpegBridge.remux("storage/emulated/0/demo.mp4", "storage/emulated/0/demo.flv") == 0 && File("storage/emulated/0/demo.flv").exists())
    }

    @Test
    fun trackExtractorTest() {
        trackExtractor("storage/emulated/0/demo.mp4", "storage/emulated/0/video.mp4", true)
        trackExtractor("storage/emulated/0/demo.mp4", "storage/emulated/0/audio.mp3", false)
        assertTrue(File("storage/emulated/0/video.mp4").exists() &&
                File("storage/emulated/0/audio.mp3").exists())
    }

    @Test
    fun trackMuxerTest() {
        trackMuxer("storage/emulated/0/video.mp4", "storage/emulated/0/audio.mp3",
                "storage/emulated/0/result.mp4")
        assertTrue(File("storage/emulated/0/result.mp4").exists())
    }

}
