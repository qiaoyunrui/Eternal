package me.juhezi.demo.renderer;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import me.juhezi.demo.R;
import me.juhezi.demo.none.GPUImageFilter;
import me.juhezi.eternal.util.TextureHelper;

public class DemoRenderer implements GLSurfaceView.Renderer {

    private Context context;
    private Bitmap mBitmap;
    private GPUImageFilter imageFilter;
    private float[] mProjectionMatrix = new float[16];
    private float[] mViewMatrix = new float[16];
    private float[] mMVPMatrix = new float[16];
    private int textureId = 0;

    public void setBitmap(Bitmap bitmap) {
        this.mBitmap = bitmap;
    }

    public DemoRenderer(Context context) {
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        imageFilter = new GPUImageFilter(context,
                R.raw.default_vertex_shader,
                R.raw.default_fragment_shader);
        textureId = TextureHelper.INSTANCE.loadTexture(mBitmap);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        if (mBitmap == null) return;
        int w = mBitmap.getWidth();
        int h = mBitmap.getHeight();

        float sWH = w / (float) h;
        float sWidthHeight = width / (float) height;
        if (width > height) {
            if (sWH > sWidthHeight) {
                Matrix.orthoM(mProjectionMatrix, 0, -sWidthHeight * sWH, sWidthHeight * sWH, -1, 1, 3, 7);
            } else {
                Matrix.orthoM(mProjectionMatrix, 0, -sWidthHeight / sWH, sWidthHeight / sWH, -1, 1, 3, 7);
            }
        } else {
            if (sWH > sWidthHeight) {
                Matrix.orthoM(mProjectionMatrix, 0, -1, 1, -1 / sWidthHeight * sWH, 1 / sWidthHeight * sWH, 3, 7);
            } else {
                Matrix.orthoM(mProjectionMatrix, 0, -1, 1, -sWH / sWidthHeight, sWH / sWidthHeight, 3, 7);
            }
        }
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, 7.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        imageFilter.useProgram();
        imageFilter.setMatrix(mMVPMatrix);
        imageFilter.bindData();
        imageFilter.setTextureId(textureId);
        imageFilter.draw();
    }
}
