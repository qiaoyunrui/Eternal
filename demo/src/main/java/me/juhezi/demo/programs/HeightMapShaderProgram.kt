package me.juhezi.demo.programs

import android.content.Context
import android.opengl.GLES20
import me.juhezi.demo.R
import me.juhezi.demo.objects.Geometry

class HeightMapShaderProgram(context: Context) :
        ShaderProgram(context, R.raw.height_map_vertex_shader,
                R.raw.height_map_fragment_shader) {

    private val uMatrixLocation: Int
    private val uVectorToLightLocation: Int

    private val aPositionLocation: Int
    private val aNormalLocation: Int

    init {
        uMatrixLocation = GLES20.glGetUniformLocation(program, U_MATRIX)
        uVectorToLightLocation = GLES20.glGetUniformLocation(program, U_VECTOR_TO_LIGHT)

        aPositionLocation = GLES20.glGetAttribLocation(program, A_POSITION)
        aNormalLocation = GLES20.glGetAttribLocation(program, A_NORMAL)
    }

    fun setUniforms(matrix: FloatArray, vectorToLight: Geometry.Vector) {
        GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0)
        GLES20.glUniform3f(uVectorToLightLocation, vectorToLight.x, vectorToLight.y, vectorToLight.z)
    }

    fun getPositionAttributeLocation() = aPositionLocation

    fun getNormalAttributeLocation() = aNormalLocation

}