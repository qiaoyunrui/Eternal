package me.juhezi.demo;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import org.jetbrains.annotations.Nullable;

import me.juhezi.demo.renderer.ParticlesRenderer;
import me.juhezi.eternal.base.BaseActivity;

public class GL2Activity extends BaseActivity {

    private static final String TAG = "GL2Activity";

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

        final ParticlesRenderer renderer = new ParticlesRenderer(this);
//        renderer.setBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.wait));

        final boolean supportsES2 = configurationInfo.reqGlEsVersion >= 0x20000;
        if (supportsES2) {
            mGLSurfaceView.setEGLContextClientVersion(2);
            mGLSurfaceView.setRenderer(renderer);
//            mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
            mRendererSet = true;
        } else {
            Toast.makeText(this, "不支持 OpenGL ES 2.0", Toast.LENGTH_SHORT).show();
            return;
        }
        setContentView(mGLSurfaceView);
        mGLSurfaceView.setOnTouchListener(new View.OnTouchListener() {

            float previousX, previousY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event != null) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        previousX = event.getX();
                        previousY = event.getY();
                    } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                        final float dx = event.getX() - previousX;
                        final float dy = event.getY() - previousY;

                        previousX = event.getX();
                        previousY = event.getY();

                        mGLSurfaceView.queueEvent(() -> renderer.handleTouchDrag(dx,dy));

                    }
                    return true;
                }
                return false;
            }
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
