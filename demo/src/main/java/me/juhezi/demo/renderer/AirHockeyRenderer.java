package me.juhezi.demo.renderer;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import me.juhezi.demo.R;
import me.juhezi.demo.objects.Mallet;
import me.juhezi.demo.objects.Table;
import me.juhezi.demo.programs.ColorShaderProgram;
import me.juhezi.demo.programs.TextureShaderProgram;
import me.juhezi.eternal.util.TextureHelper;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glViewport;


public class AirHockeyRenderer implements GLSurfaceView.Renderer {

    private static final String TAG = "AirHockeyRenderer";

    private final float[] modelMatrix = new float[16];
    private final float[] projectionMatrix = new float[16];

    private Context context;

    private Table table;
    private Mallet mallet;

    private TextureShaderProgram textureProgram;
    private ColorShaderProgram colorProgram;

    private int texture;

    public AirHockeyRenderer(Context context) {
        this.context = context;
    }

    /**
     * 可能会被调用多次
     *
     * @param gl
     * @param config
     */
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        table = new Table();
        mallet = new Mallet();

        textureProgram = new TextureShaderProgram(context);
        colorProgram = new ColorShaderProgram(context);

        texture = TextureHelper.loadTexture(context, R.drawable.air_hockey_surface);
    }

    /**
     * 每次 Surface 尺寸变化时，以及横竖屏来回切换的时候，就会调用这个方法
     *
     * @param gl
     * @param width
     * @param height
     */
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        glViewport(0, 0, width, height);
        Matrix.perspectiveM(projectionMatrix, 0, 45, (float) width / (float) height, 1f, 10f);
        Matrix.setIdentityM(modelMatrix, 0);    // 将模型矩阵设置为单位矩阵
        Matrix.translateM(modelMatrix, 0, 0f, 0f, -2.5f); // 再沿 z 轴平移 -2.5
        Matrix.rotateM(modelMatrix, 0, -60f, 1f, 0f, 0f);
        final float[] temp = new float[16];
        Matrix.multiplyMM(temp, 0, projectionMatrix, 0, modelMatrix, 0);
        System.arraycopy(temp, 0, projectionMatrix, 0, temp.length);
    }

    /**
     * 当绘制一帧时，这个方法会被调用
     * 这个方法中，一定要绘制一些东西，即使是清空屏幕
     * 因为这个方法返回后，渲染缓冲区会被交换并显示在屏幕上，如果什么都没画，可能会看到闪烁效果
     *
     * @param gl
     */
    @Override
    public void onDrawFrame(GL10 gl) {
        glClear(GL_COLOR_BUFFER_BIT);

        textureProgram.useProgram();
        textureProgram.setUniforms(projectionMatrix, texture);
        table.bindData(textureProgram);
        table.draw();

        colorProgram.useProgram();
        colorProgram.setUniforms(projectionMatrix);
        mallet.bindData(colorProgram);
        mallet.draw();
    }
}
