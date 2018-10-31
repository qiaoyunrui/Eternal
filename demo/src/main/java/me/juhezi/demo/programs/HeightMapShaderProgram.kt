package me.juhezi.demo.programs

import android.content.Context
import android.opengl.GLES20
import me.juhezi.demo.R

class HeightMapShaderProgram(context: Context) :
        ShaderProgram(context, R.raw.height_map_vertex_shader,
                R.raw.height_map_fragment_shader) {

    private val uMVMatrixLocation: Int
    private val uITMVMatrixLocation: Int
    private val uMVPMatrixLocation: Int
    private val uPointLightPositionsLocation: Int
    private val uPointLightColorsLocation: Int
    private val uVectorToLightLocation: Int

    private val aPositionLocation: Int
    private val aNormalLocation: Int

    init {
        uVectorToLightLocation = GLES20.glGetUniformLocation(program, U_VECTOR_TO_LIGHT)
        uMVMatrixLocation = GLES20.glGetUniformLocation(program, U_MV_MATRIX)
        uITMVMatrixLocation = GLES20.glGetUniformLocation(program, U_IT_MV_MATRIX)
        uMVPMatrixLocation = GLES20.glGetUniformLocation(program, U_MVP_MATRIX)
        uPointLightPositionsLocation = GLES20.glGetUniformLocation(program,
                U_POINT_LIGHT_POSITION)
        uPointLightColorsLocation = GLES20.glGetUniformLocation(program,
                U_POINT_LIGHT_COLORS)


        aPositionLocation = GLES20.glGetAttribLocation(program, A_POSITION)
        aNormalLocation = GLES20.glGetAttribLocation(program, A_NORMAL)
    }

    fun setUniforms(mvMatrix: FloatArray,
                    itMvMatrix: FloatArray,
                    mvpMatrix: FloatArray,
                    vectorToDirectionalLight: FloatArray,
                    pointLightPosition: FloatArray,
                    pointLightColors: FloatArray) {
        GLES20.glUniformMatrix4fv(uMVMatrixLocation, 1, false, mvMatrix, 0)
        GLES20.glUniformMatrix4fv(uITMVMatrixLocation, 1, false, itMvMatrix, 0)
        GLES20.glUniformMatrix4fv(uMVPMatrixLocation, 1, false, mvpMatrix, 0)

        GLES20.glUniform3fv(uVectorToLightLocation, 1, vectorToDirectionalLight, 0)

        GLES20.glUniform4fv(uPointLightPositionsLocation,
                3, pointLightPosition, 0)
        GLES20.glUniform3fv(uPointLightColorsLocation,
                3, pointLightColors, 0)
    }

    fun getPositionAttributeLocation() = aPositionLocation

    fun getNormalAttributeLocation() = aNormalLocation

}