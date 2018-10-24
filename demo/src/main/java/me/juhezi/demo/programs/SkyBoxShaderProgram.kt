package me.juhezi.demo.programs

import android.content.Context
import android.opengl.GLES20.*
import me.juhezi.demo.R


class SkyBoxShaderProgram(context: Context) :
        ShaderProgram(context, R.raw.sky_box_vertex_shader,
                R.raw.sky_box_fragment_shader) {

    private val uMatrixLocation: Int
    private val uTextureUnitLocation: Int
    private val aPositionLocation: Int

    init {
        uMatrixLocation = glGetUniformLocation(program, U_MATRIX)
        uTextureUnitLocation = glGetUniformLocation(program, U_TEXTURE_UNIT)

        aPositionLocation = glGetAttribLocation(program, A_POSITION)
    }

    fun setUniforms(matrix: FloatArray, textureId: Int) {
        glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0)

        glActiveTexture(GL_TEXTURE0)
        glBindTexture(GL_TEXTURE_CUBE_MAP, textureId)
        glUniform1i(uTextureUnitLocation, 0)
    }

    fun getPositionAttributeLocation() = aPositionLocation

}