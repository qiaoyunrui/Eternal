package me.juhezi.eternal.gpuimage

import android.opengl.GLES20
import android.opengl.GLES20.*
import android.text.TextUtils
import me.juhezi.eternal.extension.i
import me.juhezi.eternal.gpuimage.helper.ShaderHelper
import java.nio.FloatBuffer

open class EternalGPUImageFilter(protected var vertexShader: String = NO_FILTER_VERTEX_SHADER,
                                 protected var fragmentShader: String = NO_FILTER_FRAGMENT_SHADER) {

    companion object {
        // 没有滤镜的顶点着色器
        val NO_FILTER_VERTEX_SHADER = """
            attribute vec4 position;
            attribute vec4 inputTextureCoordinate;
            varying vec2 textureCoordinate;
            void main() {
                gl_Position = position;
                textureCoordinate = inputTextureCoordinate.xy;
            }
        """.trimIndent()
        val NO_FILTER_FRAGMENT_SHADER = """
            varying highp vec2 textureCoordinate;
            uniform sampler2D inputImageTexture;
            void main() {
                gl_FragColor = texture2D(inputImageTexture, textureCoordinate);
            }
        """.trimIndent()
    }

    protected var program = 0
    protected var aPositionLocation = 0
    protected var aInputTextureCoordinateLOcation = 0

    protected var uInputImageTextureLocation = 0

    private var isInitialized = false

    fun init() {
        onInit()
        isInitialized = true
        onInitialized()
    }

    fun destroy() {
        isInitialized = false
        glDeleteProgram(program)
        onDestroy()
    }

    open fun onInit() {
        i("vertexShader: $vertexShader\nfragmentShader: $fragmentShader")
        program =
                if (TextUtils.isEmpty(vertexShader)) {
                    ShaderHelper.buildProgram(fragmentShader)
                } else {
                    ShaderHelper.buildProgram(vertexShader, fragmentShader)
                }
        aPositionLocation = GLES20.glGetAttribLocation(program,
                "position")
        aInputTextureCoordinateLOcation = GLES20.glGetAttribLocation(program,
                "inputTextureCoordinate")

        uInputImageTextureLocation = GLES20.glGetUniformLocation(program,
                "inputImageTexture")
    }

    open fun onInitialized() {}

    open fun onDestroy() {}

    open fun onDraw(textureId: Int, cubeBuffer: FloatBuffer,
                    textureBuffer: FloatBuffer) {
        glUseProgram(program)
        if (!isInitialized) {
            return
        }
        cubeBuffer.position(0)
        glVertexAttribPointer(aPositionLocation, 2,
                GL_FLOAT, false,
                0, cubeBuffer)
        glEnableVertexAttribArray(aPositionLocation)

        textureBuffer.position(0)
        glVertexAttribPointer(aInputTextureCoordinateLOcation, 2,
                GL_FLOAT, false,
                0, textureBuffer)
        glEnableVertexAttribArray(aInputTextureCoordinateLOcation)

        if (textureId != NO_TEXTURE) {
            glActiveTexture(GL_TEXTURE0)
            glBindTexture(GL_TEXTURE_2D, textureId)
            glUniform1i(uInputImageTextureLocation, 0)
        }
        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4)
        glDisableVertexAttribArray(aPositionLocation)
        glDisableVertexAttribArray(aInputTextureCoordinateLOcation)
        glBindTexture(GL_TEXTURE_2D, 0)
    }

}
