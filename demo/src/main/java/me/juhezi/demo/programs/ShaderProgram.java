package me.juhezi.demo.programs;

import android.content.Context;
import android.support.annotation.RawRes;

import me.juhezi.eternal.extension.ContextExtensionKt;
import me.juhezi.eternal.util.ShaderHelper;

import static android.opengl.GLES20.glUseProgram;

public class ShaderProgram {

    // Uniform constants
    protected static final String U_MATRIX = "u_Matrix";
    protected static final String U_TEXTURE_UNIT = "u_TextureUnit";
    protected static final String U_COLOR = "u_Color";

    // Atrribute constants
    protected static final String A_POSITION = "a_Position";
    protected static final String A_COLOR = "a_Color";
    protected static final String A_TEXTURE_COORDINATES = "a_TextureCoordinates";

    //------New-------

    protected static final String U_TIME = "u_Time";

    protected static final String A_DIRECTION_VECTOR = "a_DirectionVector";
    protected static final String A_PARTICLE_START_TIME = "a_ParticleStartTime";

    // Shader program
    protected final int program;

    protected ShaderProgram(Context context,
                            @RawRes int vertexShaderResourceId, @RawRes int fragmentShaderResourceId) {
        if (context == null) {
            program = 0;
            return;
        }
        program = ShaderHelper.INSTANCE.buildProgram(
                ContextExtensionKt.readContentFromRaw(context, vertexShaderResourceId),
                ContextExtensionKt.readContentFromRaw(context, fragmentShaderResourceId));
    }

    public void useProgram() {
        glUseProgram(program);
    }
}
