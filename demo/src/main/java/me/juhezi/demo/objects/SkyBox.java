package me.juhezi.demo.objects;

import android.opengl.GLES20;

import java.nio.ByteBuffer;

import me.juhezi.demo.VertexArray;
import me.juhezi.demo.programs.SkyBoxShaderProgram;

public class SkyBox {

    private static final int POSITION_COMPONENT = 3;

    private final VertexArray vertexArray;
    private final ByteBuffer indexArray;

    public SkyBox() {
        // 保存顶点数据
        vertexArray = new VertexArray(new float[]{
                -1, 1, 1,
                1, 1, 1,
                -1, -1, 1,
                1, -1, 1,
                -1, 1, -1,
                1, 1, -1,
                -1, -1, -1,
                1, -1, -1,});
        // 索引数组，使用索引偏移值指向每个顶点
        indexArray = ByteBuffer.allocate(6 * 6)
                .put(new byte[]{
                        // Front
                        1, 3, 0,
                        0, 3, 2,
                        // Back
                        4, 6, 5,
                        5, 6, 7,
                        // Left
                        0, 2, 4,
                        4, 2, 6,
                        // Right
                        5, 7, 1,
                        1, 7, 3,
                        // Top
                        5, 1, 4,
                        4, 1, 0,
                        // Bottom
                        6, 2, 7,
                        7, 2, 3,
                });
        indexArray.position(0);
    }

    public void bindData(SkyBoxShaderProgram skyBoxProgram) {
        vertexArray.setVertexAttribPointer(0,
                skyBoxProgram.getPositionAttributeLocation(),
                POSITION_COMPONENT, 0);
    }

    public void draw() {
        // 解析成无符号字节数
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, 36, GLES20.GL_UNSIGNED_BYTE, indexArray);
    }

}
