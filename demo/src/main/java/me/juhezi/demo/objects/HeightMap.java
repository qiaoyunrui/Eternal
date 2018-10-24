package me.juhezi.demo.objects;

import android.graphics.Bitmap;
import android.graphics.Color;

import me.juhezi.demo.IndexBuffer;
import me.juhezi.demo.VertexBuffer;
import me.juhezi.demo.programs.HeightMapShaderProgram;

import static android.opengl.GLES20.GL_ELEMENT_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.GL_UNSIGNED_SHORT;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glDrawElements;

public class HeightMap {

    private static final int POSITION_COMPONENT_COUNT = 3;

    private final int width;
    private final int height;
    private final int numElements;
    private final VertexBuffer vertexBuffer;
    private final IndexBuffer indexBuffer;

    public HeightMap(Bitmap bitmap) {
        width = bitmap.getWidth();
        height = bitmap.getHeight();

        if (width * height > 65536) {
            throw new RuntimeException("HeightMap is too large for the index buffer.");
        }

        numElements = calculateNumElements();
        vertexBuffer = new VertexBuffer(loadBitmapData(bitmap));
        indexBuffer = new IndexBuffer(createIndexData());
    }

    private float[] loadBitmapData(Bitmap bitmap) {
        final int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        bitmap.recycle();

        final float[] heightMapVertices =
                new float[width * height * POSITION_COMPONENT_COUNT];
        int offset = 0;

        // 一行一行读取位图的原因在于其在内存中的布局方式就是这样，当 CPU 按顺序缓存和移动数据时，它们更有效率
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                final float xPosition = ((float) col / (float) (width - 1)) - 0.5f;
                // 假定这个图像是灰度图，因此，读入其对应像素的红色分量，并把它除以 255，得到高度
                final float yPosition = (float)Color.red(pixels[(row * height) + col]) / (float)255;
                final float zPosition = ((float) row / (float) (height - 1)) - 0.5f;

                heightMapVertices[offset++] = xPosition;
                heightMapVertices[offset++] = yPosition;
                heightMapVertices[offset++] = zPosition;
            }
        }
        return heightMapVertices;
    }

    /**
     * 生成索引数据
     *
     * @return
     */
    private short[] createIndexData() {
        final short[] indexData = new short[numElements];
        int offset = 0;
        for (int row = 0; row < height - 1; row++) {
            for (int col = 0; col < width - 1; col++) {
                short topLeftIndexNum = (short) (row * width + col);
                short topRightIndexNum = (short) (row * width + col + 1);
                short bottomLeftIndexNum = (short) ((row + 1) * width + col);
                short bottomRightIndexNum = (short) ((row + 1) * width + col + 1);

                indexData[offset++] = topLeftIndexNum;
                indexData[offset++] = bottomLeftIndexNum;
                indexData[offset++] = topRightIndexNum;

                indexData[offset++] = topRightIndexNum;
                indexData[offset++] = bottomLeftIndexNum;
                indexData[offset++] = bottomRightIndexNum;
            }
        }
        return indexData;
    }

    private int calculateNumElements() {
        return (width - 1) * (height - 1) * 2 * 3;
    }

    public void bindData(HeightMapShaderProgram program) {
        vertexBuffer.setVertexAttribPointer(0,
                program.getPositionAttributeLocation(),
                POSITION_COMPONENT_COUNT, 0);
    }

    public void draw() {
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBuffer.getBufferId());
        glDrawElements(GL_TRIANGLES, numElements, GL_UNSIGNED_SHORT, 0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    }

}
