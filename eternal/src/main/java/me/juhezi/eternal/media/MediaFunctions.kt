package me.juhezi.eternal.media

import android.media.MediaExtractor
import android.media.MediaFormat
import me.juhezi.eternal.global.logi
import me.juhezi.eternal.global.range
import java.io.IOException

fun getMediaFormat(path: String): List<String> {
    logi("path is $path")
    val extractor = MediaExtractor()
    try {
        extractor.setDataSource(path)
    } catch (e: IOException) {
        e.printStackTrace()
    }
    val tracks = extractor.trackCount
    val result = range(end = tracks).map {
        extractor.getTrackFormat(it).getString(MediaFormat.KEY_MIME)
    }
    extractor.release()
    return result
}