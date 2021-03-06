package me.juhezi.demo.objects;

import android.graphics.Color;
import android.opengl.GLES20;

import me.juhezi.demo.VertexArray;
import me.juhezi.demo.programs.ParticleShaderProgram;

import static me.juhezi.demo.Constants.BYTES_PER_FLOAT;

public class ParticleSystem {

    private static final int POSITION_COMPONENT_COUNT = 3;
    private static final int COLOR_COMPONENT_COUNT = 3;
    private static final int VECTOR_COMPONENT_COUNT = 3;
    private static final int PARTICLE_START_TIME_COMPONENT = 1;

    private static final int TOTAL_COMPONENT_COUNT =
            POSITION_COMPONENT_COUNT +
                    COLOR_COMPONENT_COUNT +
                    VECTOR_COMPONENT_COUNT +
                    PARTICLE_START_TIME_COMPONENT;

    private static final int STRIDE = TOTAL_COMPONENT_COUNT * BYTES_PER_FLOAT;

    private final float[] particles;
    private final VertexArray vertexArray;
    private final int maxParticleCount;

    private int currentParticleCount;
    private int nextParticle;

    public ParticleSystem(int maxParticleCount) {
        particles = new float[maxParticleCount * TOTAL_COMPONENT_COUNT];
        vertexArray = new VertexArray(particles);
        this.maxParticleCount = maxParticleCount;
    }

    public void addParticle(Geometry.Point position,
                            int color,
                            Geometry.Vector direction,
                            float particleStartTime) {
        final int particleOffset = nextParticle * TOTAL_COMPONENT_COUNT;

        int currentOffset = particleOffset;
        nextParticle++;

        // 一个新粒子每次被添加进来时，就给 nextParticle 增加 1，当到了数组的结尾处，就从 0 开始以便回收最旧的粒子。
        if (currentParticleCount < maxParticleCount) {
            currentParticleCount++;
        }

        if (nextParticle == maxParticleCount) {
            nextParticle = 0;
        }
        particles[currentOffset++] = position.x;
        particles[currentOffset++] = position.y;
        particles[currentOffset++] = position.z;

        particles[currentOffset++] = Color.red(color) / 255f;
        particles[currentOffset++] = Color.green(color) / 255f;
        particles[currentOffset++] = Color.blue(color) / 255f;

        particles[currentOffset++] = direction.x;
        particles[currentOffset++] = direction.y;
        particles[currentOffset++] = direction.z;

        particles[currentOffset++] = particleStartTime;

        // 复制到本地缓存区
        vertexArray.updateBuffer(particles, particleOffset, TOTAL_COMPONENT_COUNT);
    }

    public void bindData(ParticleShaderProgram program) {
        int dataOffset = 0;
        vertexArray.setVertexAttribPointer(dataOffset,
                program.getPositionAttributeLocation(),
                POSITION_COMPONENT_COUNT,
                STRIDE);
        dataOffset += POSITION_COMPONENT_COUNT;


        vertexArray.setVertexAttribPointer(dataOffset,
                program.getColorAttributeLocation(),
                COLOR_COMPONENT_COUNT,
                STRIDE);
        dataOffset += COLOR_COMPONENT_COUNT;


        vertexArray.setVertexAttribPointer(dataOffset,
                program.getDirectionVectorAttributeLocation(),
                VECTOR_COMPONENT_COUNT,
                STRIDE);
        dataOffset += VECTOR_COMPONENT_COUNT;


        vertexArray.setVertexAttribPointer(dataOffset,
                program.getParticleStartTimeAttributeLocation(),
                PARTICLE_START_TIME_COMPONENT,
                STRIDE);
    }

    public void draw() {
        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, currentParticleCount);
    }

}
