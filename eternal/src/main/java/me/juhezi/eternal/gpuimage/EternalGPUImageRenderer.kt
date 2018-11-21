package me.juhezi.eternal.gpuimage

import android.graphics.Bitmap
import android.graphics.Canvas
import android.opengl.GLES20
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import me.juhezi.eternal.gpuimage.helper.TextureHelper
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.util.*
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class EternalGPUImageRenderer(var currentFilter: EternalGPUImageFilter)
    : GLSurfaceView.Renderer {

    companion object {
        // 默认顶点坐标
        val CUBE = floatArrayOf(
                -1.0f, -1.0f,
                1.0f, -1.0f,
                -1.0f, 1.0f,
                1.0f, 1.0f)

        // 默认纹理坐标
        val ST = floatArrayOf(
                0.0f, 1.0f,
                1.0f, 1.0f,
                0.0f, 0.0f,
                1.0f, 0.0f)
    }

    private val cubeBuffer: FloatBuffer
    private val textureBuffer: FloatBuffer
    private var textureId: Int = NO_TEXTURE
    private val runOnDrawQueue: Queue<Runnable>

    var drawClosure: (() -> Unit)? = null

    private var outputWidth: Int = 0
    private var outputHeight: Int = 0
    private var imageWidth: Int = 0
    private var imageHeight: Int = 0
    private var addedPadding: Int = 0

    init {
        runOnDrawQueue = LinkedList()
        cubeBuffer = ByteBuffer.allocateDirect(
                CUBE.size * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
        cubeBuffer.put(CUBE).position(0)

        textureBuffer = ByteBuffer.allocateDirect(
                ST.size * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
        textureBuffer.put(ST).position(0)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        outputWidth = width
        outputHeight = height
        glViewport(0, 0, width, height)
        currentFilter.setOutputSize(outputWidth to outputHeight)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        glClearColor(0f, 0f, 0f, 1f)
        glDisable(GL_DEPTH_TEST)
        currentFilter.init()
    }

    override fun onDrawFrame(gl: GL10?) {
        glClear(GLES20.GL_COLOR_BUFFER_BIT or
                GLES20.GL_DEPTH_BUFFER_BIT)
        runAll(runOnDrawQueue)
        drawClosure?.invoke()
        currentFilter.onDraw(textureId, cubeBuffer, textureBuffer)
    }

    fun setImageBitmap(bitmap: Bitmap, recycle: Boolean = true) {
        runOnDraw(Runnable {
            var resizeBitmap: Bitmap? = null
            if (bitmap.width % 2 == 1) {
                resizeBitmap = Bitmap.createBitmap(bitmap.width + 1,
                        bitmap.height,
                        Bitmap.Config.ARGB_8888)
                val canvas = Canvas(resizeBitmap)
                canvas.drawARGB(0x00, 0x00, 0x00, 0x00)
                canvas.drawBitmap(bitmap, 0f, 0f, null)
                addedPadding = 1
            } else {
                addedPadding = 0
            }
            textureId = TextureHelper.loadTexture(resizeBitmap ?: bitmap, recycle)
            resizeBitmap?.recycle()
            imageWidth = bitmap.width
            imageHeight = bitmap.height
        })
    }

    fun runOnDraw(runnable: Runnable) {
        synchronized(runOnDrawQueue) {
            runOnDrawQueue.add(runnable)
        }
    }

    private fun runAll(queue: Queue<Runnable>) {
        synchronized(queue) {
            while (!queue.isEmpty()) {
                queue.poll().run()
            }
        }
    }


    fun deleteImage() {
        runOnDraw(Runnable {
            GLES20.glDeleteTextures(1, intArrayOf(textureId), 0)
            textureId = NO_TEXTURE
        })
    }

    fun setFilter(filter: EternalGPUImageFilter) {
        runOnDraw(Runnable {
            val oldFilter = this.currentFilter
            currentFilter = filter
            oldFilter.destroy()
            currentFilter.init()
            currentFilter.setOutputSize(outputWidth to outputHeight)
        })
    }

}