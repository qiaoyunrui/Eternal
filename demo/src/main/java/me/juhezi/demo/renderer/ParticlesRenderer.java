package me.juhezi.demo.renderer;

import android.content.Context;
import android.graphics.Color;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import me.juhezi.demo.R;
import me.juhezi.demo.objects.Geometry;
import me.juhezi.demo.objects.ParticleShooter;
import me.juhezi.demo.objects.ParticleSystem;
import me.juhezi.demo.programs.ParticleShaderProgram;
import me.juhezi.eternal.util.TextureHelper;

import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_ONE;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glViewport;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.perspectiveM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.translateM;

public class ParticlesRenderer implements GLSurfaceView.Renderer {

    private final Context context;
    private final float[] projectionMatrix = new float[16];
    private final float[] viewMatrix = new float[16];
    private final float[] viewProjectionMatrix = new float[16];

    private final float angleVarianceInDegrees = 5f;    // 发射角变化量
    private final float speedVariance = 1f;

    private ParticleShaderProgram particleShaderProgram;
    private ParticleSystem particleSystem;
    private ParticleShooter redParticleShooter;
    private ParticleShooter greenParticleShooter;
    private ParticleShooter blueParticleShooter;

    private long globalStartTime;

    private int texture;

    public ParticlesRenderer(Context context) {
        this.context = context;
    }


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glEnable(GL_BLEND); // 使用混合模式
        glBlendFunc(GL_ONE, GL_ONE);    // 设置混合模式为累加模式

        particleShaderProgram = new ParticleShaderProgram(context);
        particleSystem = new ParticleSystem(10000);
        globalStartTime = System.currentTimeMillis();

        final Geometry.Vector particleDirection =
                new Geometry.Vector(0f, 0.5f, 0f);

        // 创建 3 个粒子喷泉。每个喷泉由一个粒子发射器表示，每个发射器都将按照 particleDirection 定义的方向或者沿 y 垂直向上发射它的粒子。
        redParticleShooter = new ParticleShooter(
                new Geometry.Point(-1f, 0f, 0f),
                particleDirection,
                Color.rgb(255, 50, 5),
                angleVarianceInDegrees,
                speedVariance);

        greenParticleShooter = new ParticleShooter(
                new Geometry.Point(0f, 0f, 0f),
                particleDirection,
                Color.rgb(25, 255, 25),
                angleVarianceInDegrees,
                speedVariance);

        blueParticleShooter = new ParticleShooter(
                new Geometry.Point(1f, 0f, 0f),
                particleDirection,
                Color.rgb(5, 50, 255),
                angleVarianceInDegrees,
                speedVariance);

        texture = TextureHelper.loadTexture(context, R.drawable.particle_texture);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        glViewport(0, 0, width, height);
        perspectiveM(projectionMatrix, 0,
                45,
                (float) width / (float) height,
                1f, 10f);
        setIdentityM(viewMatrix, 0);
        translateM(viewMatrix, 0, 0f, -1.5f, -5f);
        multiplyMM(viewProjectionMatrix, 0,
                projectionMatrix, 0,
                viewMatrix, 0);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        glClear(GL_COLOR_BUFFER_BIT);

        float currentTime = (System.nanoTime() - globalStartTime) / 1000000000f;

        redParticleShooter.addParticles(particleSystem, currentTime, 5);
        greenParticleShooter.addParticles(particleSystem, currentTime, 5);
        blueParticleShooter.addParticles(particleSystem, currentTime, 5);

        particleShaderProgram.useProgram();
        particleShaderProgram.setUniforms(viewProjectionMatrix, currentTime, texture);
        particleSystem.bindData(particleShaderProgram);
        particleSystem.draw();

    }
}
