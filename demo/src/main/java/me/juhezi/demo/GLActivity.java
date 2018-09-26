package me.juhezi.demo;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import org.jetbrains.annotations.Nullable;

import me.juhezi.demo.renderer.AirHockeyRenderer;
import me.juhezi.eternal.base.BaseActivity;

public class GLActivity extends BaseActivity {

    private static final String TAG = "GLActivity";

    private GLSurfaceView mGLSurfaceView;

    private boolean mRendererSet = false;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setToolBarVisibility(false);
        mGLSurfaceView = new GLSurfaceView(this);

        final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();

        Log.i(TAG, "onCreate: reqGlEsVersion: " + configurationInfo.reqGlEsVersion);

        final AirHockeyRenderer renderer = new AirHockeyRenderer(this);

        final boolean supportsES2 = configurationInfo.reqGlEsVersion >= 0x20000;
        if (supportsES2) {
            mGLSurfaceView.setEGLContextClientVersion(2);

            mGLSurfaceView.setRenderer(renderer);
            mRendererSet = true;
        } else {
            Toast.makeText(this, "不支持 OpenGL ES 2.0", Toast.LENGTH_SHORT).show();
            return;
        }
        setContentView(mGLSurfaceView);
        mGLSurfaceView.setOnTouchListener((v, event) -> {
            // 在着色器中需要使用归一化设备坐标，因此需要把触控时间坐标转换回归一化设备坐标，所以要把 y 轴反转，并把每个坐标按比例映射到范围 [-1，1] 内。
            final float normalizedX =
                    (event.getX() / (float) v.getWidth()) * 2 - 1;
            final float normalizedY =
                    -((event.getY() / (float) v.getHeight()) * 2 - 1);
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                mGLSurfaceView.queueEvent(() -> renderer.handleTouchPress(normalizedX, normalizedY));
            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                mGLSurfaceView.queueEvent(() -> renderer.handleTouchDrag(normalizedX, normalizedY));
            }
            return true;
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mRendererSet) {
            mGLSurfaceView.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mRendererSet) {
            mGLSurfaceView.onResume();
        }
    }
}
