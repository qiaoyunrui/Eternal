package me.juhezi.demo.none;

import android.content.Context;
import android.opengl.GLES20;
import android.support.annotation.RawRes;

import me.juhezi.demo.Constants;
import me.juhezi.demo.VertexArray;
import me.juhezi.eternal.extension.ContextExtensionKt;
import me.juhezi.eternal.util.ShaderHelper;

import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TRIANGLE_STRIP;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUseProgram;

/**
 * 粗制滥造的
 */
public class GPUImageFilter {
    private static final String TAG = "GPUImageFilter";

    private static final String A_POSITION = "aPosition";
    private static final String A_COORDINATE = "aCoordinate";

    private static final String U_MATRIX = "uMatrix";
    private static final String U_TEXTURE = "uTexture";

    private static final int POSITION_COMPONENT_COUNT = 2;
    private static final int TEXTURE_COORDINATES_COMPONENT_COUNT = 2;
    private static final int STRIDE = (POSITION_COMPONENT_COUNT + TEXTURE_COORDINATES_COMPONENT_COUNT) * Constants.BYTES_PER_FLOAT;

    private final int program;

    private final int aPosition;
    private final int aCoordinate;

    private final int uModelMatrix;
    private final int uTexture;

    // 包含了顶点坐标和纹理坐标
    private static final float[] VERTEX_DATA = {
            -1.0f, 1.0f, 0.0f, 0.0f,
            -1.0f, -1.0f, 0.0f, 1.0f,
            1.0f, 1.0f, 1.0f, 0.0f,
            1.0f, -1.0f, 1.0f, 1.0f,
    };

    private final VertexArray vertexArray;


    public GPUImageFilter(Context context,
                          @RawRes int vertexShaderResourceId, @RawRes int fragmentShaderResourceId) {
        program = ShaderHelper.INSTANCE.buildProgram(
                ContextExtensionKt.readContentFromRaw(context, vertexShaderResourceId),
                ContextExtensionKt.readContentFromRaw(context, fragmentShaderResourceId));

        aPosition = GLES20.glGetAttribLocation(program, A_POSITION);
        aCoordinate = GLES20.glGetAttribLocation(program, A_COORDINATE);

        uModelMatrix = GLES20.glGetUniformLocation(program, U_MATRIX);
        uTexture = GLES20.glGetUniformLocation(program, U_TEXTURE);

        vertexArray = new VertexArray(VERTEX_DATA);
    }

    public void useProgram() {
        glUseProgram(program);
    }

    public void setMatrix(float[] matrix) {
        GLES20.glUniformMatrix4fv(uModelMatrix, 1, false, matrix, 0);
    }

    public void setTextureId(int textureId) {
        if (textureId == 0) return;
        glBindTexture(GL_TEXTURE_2D, textureId);
        glUniform1i(uTexture, 0);
    }

    public void bindData() {
        vertexArray.setVertexAttribPointer(
                0,
                aPosition,
                POSITION_COMPONENT_COUNT,
                STRIDE);

        vertexArray.setVertexAttribPointer(
                POSITION_COMPONENT_COUNT,
                aCoordinate,
                TEXTURE_COORDINATES_COMPONENT_COUNT,
                STRIDE);
    }

    public void draw() {
        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
    }
}
