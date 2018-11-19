package me.juhezi.demo.renderer;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import me.juhezi.demo.R;
import me.juhezi.demo.objects.Geometry;
import me.juhezi.demo.objects.HeightMap;
import me.juhezi.demo.objects.ParticleShooter;
import me.juhezi.demo.objects.ParticleSystem;
import me.juhezi.demo.objects.SkyBox;
import me.juhezi.demo.programs.HeightMapShaderProgram;
import me.juhezi.demo.programs.ParticleShaderProgram;
import me.juhezi.demo.programs.SkyBoxShaderProgram;
import me.juhezi.eternal.gpuimage.helper.TextureHelper;

import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_CULL_FACE;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_TEST;
import static android.opengl.GLES20.GL_LEQUAL;
import static android.opengl.GLES20.GL_LESS;
import static android.opengl.GLES20.GL_ONE;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDepthFunc;
import static android.opengl.GLES20.glDepthMask;
import static android.opengl.GLES20.glDisable;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glViewport;
import static android.opengl.Matrix.invertM;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.multiplyMV;
import static android.opengl.Matrix.perspectiveM;
import static android.opengl.Matrix.rotateM;
import static android.opengl.Matrix.scaleM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.translateM;
import static android.opengl.Matrix.transposeM;

public class ParticlesRenderer implements GLSurfaceView.Renderer {

    private final Context context;

    private final float[] modelMatrix = new float[16];
    private final float[] viewMatrix = new float[16];
    private final float[] viewMatrixForSkyBox = new float[16];
    private final float[] projectionMatrix = new float[16];

    private final float[] tempMatrix = new float[16];
    private final float[] modelViewProjectionMatrix = new float[16];

    private final float[] modelViewMatrix = new float[16];
    private final float[] itModelViewMatrix = new float[16];

    private final float[] vectorToLight = {0.30f, 0.35f, -0.89f, 0f};
    private final float[] pointLightPositions = new float[]{
            -1f, 1f, 0f, 1f,
            0f, 1f, 0f, 1f,
            1f, 1f, 0f, 1f,
    };
    private final float[] pointLightColors = new float[]{
            1.00f, 0.20f, 0.02f,
            0.02f, 0.25f, 0.02f,
            0.02f, 0.20f, 1.00f,
    };

    private final float angleVarianceInDegrees = 5f;    // 发射角变化量
    private final float speedVariance = 1f;

    private ParticleShaderProgram particleShaderProgram;
    private ParticleSystem particleSystem;
    private ParticleShooter redParticleShooter;
    private ParticleShooter greenParticleShooter;
    private ParticleShooter blueParticleShooter;

    private SkyBoxShaderProgram skyBoxProgram;
    private SkyBox skyBox;
    private int skyBoxTexture;

    private HeightMapShaderProgram heightMapShaderProgram;
    private HeightMap heightMap;

    private float xRotation, yRotation;

    private long globalStartTime;

    private int particleTexture;

    public ParticlesRenderer(Context context) {
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);

        heightMapShaderProgram = new HeightMapShaderProgram(context);
        heightMap = new HeightMap(BitmapFactory.decodeResource(context.getResources(), R.drawable.heightmap));

        skyBoxProgram = new SkyBoxShaderProgram(context);
        skyBox = new SkyBox();
        skyBoxTexture = TextureHelper.INSTANCE.loadCubeMap(context,
                new int[]{
                        R.drawable.left,
                        R.drawable.right,
                        R.drawable.bottom,
                        R.drawable.top,
                        R.drawable.front,
                        R.drawable.back,
                });

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

        particleTexture = TextureHelper.INSTANCE.loadTexture(context, R.drawable.particle_texture);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        glViewport(0, 0, width, height);
        perspectiveM(projectionMatrix, 0,
                45,
                (float) width / (float) height,
                1f, 100f);
        updateViewMatrices();
        // 因为使用了天空盒，所以不想把平移矩阵应用到这个场景上，也不想把它应用到天空盒上
        // 所以，需要为天空盒和粒子使用一个不同的矩阵
//        setIdentityM(viewMatrix, 0);
//        translateM(viewMatrix, 0, 0f, -1.5f, -5f);
//        multiplyMM(viewProjectionMatrix, 0,
//                projectionMatrix, 0,
//                viewMatrix, 0);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // 在每个帧上也要清空深度缓冲区

        drawHeightMap();

        drawSkyBox();

        drawParticles();

    }

    private void drawSkyBox() {
        glDepthFunc(GL_LEQUAL);
        setIdentityM(modelMatrix, 0);
        updateMvpMatrixForSkyBox();
        skyBoxProgram.useProgram();
        skyBoxProgram.setUniforms(modelViewProjectionMatrix, skyBoxTexture);
        skyBox.bindData(skyBoxProgram);
        skyBox.draw();
        glDepthFunc(GL_LESS);
    }

    private void drawParticles() {
        glDepthMask(false);
        float currentTime = (System.nanoTime() - globalStartTime) / 1000000000f;

        redParticleShooter.addParticles(particleSystem, currentTime, 5);
        greenParticleShooter.addParticles(particleSystem, currentTime, 5);
        blueParticleShooter.addParticles(particleSystem, currentTime, 5);

        setIdentityM(modelMatrix, 0);
        updateMvpMatrix();

        glEnable(GL_BLEND); // 使用混合模式
        glBlendFunc(GL_ONE, GL_ONE);    // 设置混合模式为累加模式

        particleShaderProgram.useProgram();
        particleShaderProgram.setUniforms(modelViewProjectionMatrix, currentTime, particleTexture);
        particleSystem.bindData(particleShaderProgram);
        particleSystem.draw();

        glDisable(GL_BLEND);
        glDepthMask(true);
    }

    private void drawHeightMap() {
        setIdentityM(modelMatrix, 0);
        scaleM(modelMatrix, 0, 100f, 10f, 100f);
        updateMvpMatrix();
        heightMapShaderProgram.useProgram();
        // put the light position into eye space
        final float[] vectorToLightInEyeSpace = new float[4];
        final float[] pointPositionsInEyeSpace = new float[12];
        multiplyMV(vectorToLightInEyeSpace, 0, viewMatrix,
                0, vectorToLight, 0);
        multiplyMV(pointPositionsInEyeSpace, 0, viewMatrix,
                0, pointLightPositions, 0);
        multiplyMV(pointPositionsInEyeSpace, 4, viewMatrix,
                0, pointLightPositions, 4);
        multiplyMV(pointPositionsInEyeSpace, 8, viewMatrix,
                0, pointLightPositions, 8);

        heightMapShaderProgram.setUniforms(
                modelMatrix,
                itModelViewMatrix,
                modelViewProjectionMatrix,
                vectorToLightInEyeSpace,
                pointPositionsInEyeSpace,
                pointLightColors);
        heightMap.bindData(heightMapShaderProgram);
        heightMap.draw();
    }

    public void handleTouchDrag(float dx, float dy) {
        xRotation += dx / 16f;
        yRotation += dy / 16f;

        if (yRotation < -90) {
            yRotation = -90;
        } else if (yRotation > 90) {
            yRotation = 90;
        }
        updateViewMatrices();
    }

    public void updateViewMatrices() {
        setIdentityM(viewMatrix, 0);
        rotateM(viewMatrix, 0, -yRotation, 1f, 0f, 0f);
        rotateM(viewMatrix, 0, -xRotation, 0f, 1f, 0f);
        System.arraycopy(viewMatrix, 0, viewMatrixForSkyBox, 0, viewMatrix.length);

        translateM(viewMatrix, 0, 0f, -1.5f, -15f);
    }

    public void updateMvpMatrix() {
        multiplyMM(modelViewMatrix, 0, viewMatrix, 0, modelMatrix, 0);
        // 逆矩阵
        invertM(tempMatrix, 0, modelViewMatrix, 0);
        // 转置矩阵
        transposeM(itModelViewMatrix, 0, tempMatrix, 0);
        multiplyMM(
                modelViewProjectionMatrix, 0,
                projectionMatrix, 0,
                modelViewMatrix, 0);
    }

    public void updateMvpMatrixForSkyBox() {
        multiplyMM(tempMatrix, 0, viewMatrixForSkyBox, 0, modelMatrix, 0);
        multiplyMM(modelViewProjectionMatrix, 0, projectionMatrix, 0, tempMatrix, 0);
    }

}
