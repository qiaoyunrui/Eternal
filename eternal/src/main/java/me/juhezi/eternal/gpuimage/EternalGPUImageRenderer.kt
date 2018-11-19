package me.juhezi.eternal.gpuimage

import android.opengl.GLES20
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import me.juhezi.eternal.gpuimage.helper.TextureRotationHelper
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class EternalGPUImageRenderer(val filter: EternalGPUImageFilter)
    : GLSurfaceView.Renderer {

    companion object {
        val CUBE =
                floatArrayOf(
                        -1.0f, -1.0f,
                        1.0f, -1.0f,
                        -1.0f, 1.0f,
                        1.0f, 1.0f)
    }

    private val cubeBuffer: FloatBuffer
    private val textureBuffer: FloatBuffer
    private var textureId: Int = NO_TEXTURE

    init {
        cubeBuffer = ByteBuffer.allocate(CUBE.size * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
        cubeBuffer.put(CUBE).position(0)

        textureBuffer = ByteBuffer.allocate(
                TextureRotationHelper.TEXTURE_NO_ROTATION.size * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        glViewport(0, 0, width, height)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        glClearColor(0f, 0f, 0f, 1f)
        glDisable(GL_DEPTH_TEST)
        filter.init()
    }

    override fun onDrawFrame(gl: GL10?) {
        glClear(GLES20.GL_COLOR_BUFFER_BIT or
                GLES20.GL_DEPTH_BUFFER_BIT)
        filter.onDraw(textureId, cubeBuffer, textureBuffer)
    }

}