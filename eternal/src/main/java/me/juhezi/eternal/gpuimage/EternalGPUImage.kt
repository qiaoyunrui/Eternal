package me.juhezi.eternal.gpuimage

import android.content.Context
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.opengl.GLSurfaceView

class EternalGPUImage(val context: Context) {

    private var filter: EternalGPUImageFilter = EternalGPUImageFilter()
    private var renderer: EternalGPUImageRenderer
    private var currentBitmap: Bitmap? = null

    var glSurfaceView: GLSurfaceView? = null
        set(value) {
            field = value
            field?.setEGLContextClientVersion(2)
            field?.setEGLConfigChooser(8, 8,
                    8, 8,
                    16, 0)
            field?.holder?.setFormat(PixelFormat.RGBA_8888)
            field?.setRenderer(renderer)
            field?.renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
            field?.requestRender()
        }

    init {
        renderer = EternalGPUImageRenderer(filter)
    }

    fun requestRender() {
        glSurfaceView?.requestRender()
    }

    fun setImage(bitmap: Bitmap?): EternalGPUImage {
        currentBitmap = bitmap
        if (bitmap != null) {
            renderer.setImageBitmap(bitmap)
        }
        return this
    }

}