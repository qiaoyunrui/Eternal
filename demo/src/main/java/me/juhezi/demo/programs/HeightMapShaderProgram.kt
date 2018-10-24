package me.juhezi.demo.programs

import android.content.Context
import android.opengl.GLES20
import me.juhezi.demo.R

class HeightMapShaderProgram(context: Context) :
        ShaderProgram(context, R.raw.height_map_vertex_shader,
                R.raw.height_map_fragment_shader) {

    private val uMatrixLocation: Int
    private val aPositionLocation: Int

    init {
        uMatrixLocation = GLES20.glGetUniformLocation(program, U_MATRIX)
        aPositionLocation = GLES20.glGetAttribLocation(program, A_POSITION)
    }

    fun setUniforms(matrix: FloatArray) {
        GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0)
    }

    fun getPositionAttributeLocation() = aPositionLocation

}