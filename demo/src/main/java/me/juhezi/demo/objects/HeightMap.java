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
import static me.juhezi.demo.Constants.BYTES_PER_FLOAT;

// 顶点缓冲区存储位置和法线
public class HeightMap {

    private static final int POSITION_COMPONENT_COUNT = 3;

    private static final int NORMAL_COMPONENT_COUNT = 3;
    private static final int TOTAL_COMPONENT_COUNT =
            POSITION_COMPONENT_COUNT + NORMAL_COMPONENT_COUNT;

    private static final int STRIDE =
            (POSITION_COMPONENT_COUNT + NORMAL_COMPONENT_COUNT) * BYTES_PER_FLOAT;

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
                new float[width * height * TOTAL_COMPONENT_COUNT];  // 存储位置和法线
        int offset = 0;

        // 一行一行读取位图的原因在于其在内存中的布局方式就是这样，当 CPU 按顺序缓存和移动数据时，它们更有效率
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                final Geometry.Point point = getPoint(pixels, row, col);

                heightMapVertices[offset++] = point.x;
                heightMapVertices[offset++] = point.y;
                heightMapVertices[offset++] = point.z;

                final Geometry.Point top = getPoint(pixels, row - 1, col);
                final Geometry.Point left = getPoint(pixels, row, col - 1);
                final Geometry.Point right = getPoint(pixels, row, col + 1);
                final Geometry.Point bottom = getPoint(pixels, row + 1, col);

                final Geometry.Vector rightToLeft = Geometry.vectorBetween(right, left);
                final Geometry.Vector topToBottom = Geometry.vectorBetween(top, bottom);

                // 计算法线
                final Geometry.Vector normal = rightToLeft.crossProduct(topToBottom).normalize();

                heightMapVertices[offset++] = normal.x;
                heightMapVertices[offset++] = normal.y;
                heightMapVertices[offset++] = normal.z;
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
                POSITION_COMPONENT_COUNT, STRIDE);

        vertexBuffer.setVertexAttribPointer(
                POSITION_COMPONENT_COUNT * BYTES_PER_FLOAT,
                program.getNormalAttributeLocation(),
                NORMAL_COMPONENT_COUNT, STRIDE);
    }

    public void draw() {
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBuffer.getBufferId());
        glDrawElements(GL_TRIANGLES, numElements, GL_UNSIGNED_SHORT, 0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    private Geometry.Point getPoint(int[] pixels, int row, int col) {
        float x = ((float) col / (float) (width - 1)) - 0.5f;
        float z = ((float) row / (float) (height - 1)) - 0.5f;

        row = clamp(row, 0, width - 1);
        col = clamp(col, 0, height - 1);

        // 假定这个图像是灰度图，因此，读入其对应像素的红色分量，并把它除以 255，得到高度
        float y = (float) Color.red(pixels[(row * height) + col]) / (float) 255;
        return new Geometry.Point(x, y, z);
    }

    private int clamp(int val, int min, int max) {
        return Math.max(min, Math.min(max, val));
    }

}
