package me.juhezi.demo;

import android.opengl.GLES20;

import java.util.ArrayList;
import java.util.List;

import me.juhezi.demo.objects.Geometry;

public class ObjectBuilder {

    private static final int FLOATS_PER_VERTEX = 3;     // 一个顶点需要多少浮点数
    private final float[] vertexData;   // 保存这些顶点
    private int offset = 0;

    private final List<DrawCommand> drawList = new ArrayList<>();

    private ObjectBuilder(int sizeInVertices) {
        vertexData = new float[sizeInVertices * FLOATS_PER_VERTEX];
    }

    // 计算圆柱体顶部顶点数量的方法
    private static int sizeOfCircleInVertices(int numPoints) {
        return 1 + (numPoints + 1);
    }

    private static int sizeOfCylinderInVertices(int numPoints) {
        return (numPoints + 1) * 2;
    }

    private void appendCircle(Geometry.Circle circle, int numPoints) {

        // 顶点偏移值 和 长度
        final int startVertex = offset / FLOATS_PER_VERTEX;
        final int numVertices = sizeOfCircleInVertices(numPoints);

        vertexData[offset++] = circle.center.x;
        vertexData[offset++] = circle.center.y;
        vertexData[offset++] = circle.center.z;

        for (int i = 0; i <= numPoints; i++) {
            float angleInRadians =
                    ((float) i / (float) numPoints) * ((float) Math.PI * 2f);
            vertexData[offset++] = (float) (circle.center.x
                    + circle.radius * Math.cos(angleInRadians));
            vertexData[offset++] = circle.center.y;
            vertexData[offset++] = (float) (circle.center.z
                    + circle.radius * Math.sin(angleInRadians));
        }
        drawList.add(() -> GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, startVertex, numVertices));
    }

    private void appendOpenCylinder(Geometry.Cylinder cylinder, int numPoints) {
        final int startVertex = offset / FLOATS_PER_VERTEX;
        final int numVertices = sizeOfCylinderInVertices(numPoints);
        final float yStart = cylinder.center.y - (cylinder.height / 2f);
        final float yEnd = cylinder.center.y + (cylinder.height / 2f);

        for (int i = 0; i <= numPoints; i++) {
            float angleInRadians =
                    ((float) i / (float) numPoints)
                            * ((float) Math.PI * 2f);
            float xPosition =
                    (float) (cylinder.center.x
                            + cylinder.radius * Math.cos(angleInRadians));
            float zPosition =
                    (float) (cylinder.center.z
                            + cylinder.radius * Math.sin(angleInRadians));

            vertexData[offset++] = xPosition;
            vertexData[offset++] = yStart;
            vertexData[offset++] = zPosition;

            vertexData[offset++] = xPosition;
            vertexData[offset++] = yEnd;
            vertexData[offset++] = zPosition;

            drawList.add(() -> GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, startVertex, numVertices));
        }

    }

    private GeneratedData build() {
        return new GeneratedData(vertexData, drawList);
    }

    public static GeneratedData createPuck(Geometry.Cylinder puck, int numPoints) {
        int size = sizeOfCircleInVertices(numPoints) + sizeOfCylinderInVertices(numPoints);
        ObjectBuilder builder = new ObjectBuilder(size);
        Geometry.Circle puckTop = new Geometry.Circle(
                puck.center.translateY(puck.height / 2f),
                puck.radius);
        builder.appendCircle(puckTop, numPoints);
        builder.appendOpenCylinder(puck, numPoints);

        return builder.build();
    }

    public static GeneratedData createMallet(Geometry.Point center, float radius,
                                      float height, int numPoints) {
        int size = sizeOfCircleInVertices(numPoints) * 2 +
                sizeOfCylinderInVertices(numPoints) * 2;
        ObjectBuilder builder = new ObjectBuilder(size);
        float baseHeight = height * 0.25f;
        Geometry.Circle baseCircle = new Geometry.Circle(
                center.translateY(-baseHeight),
                radius);
        Geometry.Cylinder baseCylinder = new Geometry.Cylinder(
                baseCircle.center.translateY(-baseHeight / 2f),
                radius, baseHeight);

        builder.appendCircle(baseCircle, numPoints);
        builder.appendOpenCylinder(baseCylinder, numPoints);

        float handleHeight = height * 0.75f;
        float handleRadius = radius / 3f;

        Geometry.Circle handleCircle = new Geometry.Circle(
                center.translateY(height * 0.5f),
                handleRadius);
        Geometry.Cylinder handleCylinder = new Geometry.Cylinder(
                handleCircle.center.translateY(-handleHeight / 2f),
                handleRadius,
                handleHeight);

        builder.appendCircle(handleCircle, numPoints);
        builder.appendOpenCylinder(handleCylinder, numPoints);
        return builder.build();
    }


    public static class GeneratedData {
        public final float[] vertexData;
        public final List<DrawCommand> drawCommands;

        public GeneratedData(float[] vertexData, List<DrawCommand> drawCommands) {
            this.vertexData = vertexData;
            this.drawCommands = drawCommands;
        }
    }

    public interface DrawCommand {
        void draw();
    }

}
