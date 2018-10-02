package me.juhezi.demo;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.jetbrains.annotations.Nullable;

import me.juhezi.eternal.base.BaseActivity;

public class GL2Activity extends BaseActivity {

    private static final String TAG = "GL2Activity";

    private GLSurfaceView mGLSurfaceView;
    private boolean mRendererSet = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setToolBarVisibility(false);
        mGLSurfaceView = new GLSurfaceView(this);

        final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();

        Log.i(TAG, "onCreate: reqGlEsVersion: " + configurationInfo.reqGlEsVersion);

//        final AirHockeyRenderer renderer = new AirHockeyRenderer(this);

        final boolean supportsES2 = configurationInfo.reqGlEsVersion >= 0x20000;
        if (supportsES2) {
            mGLSurfaceView.setEGLContextClientVersion(2);

//            mGLSurfaceView.setRenderer(renderer);
            mRendererSet = true;
        } else {
            Toast.makeText(this, "不支持 OpenGL ES 2.0", Toast.LENGTH_SHORT).show();
            return;
        }
        setContentView(mGLSurfaceView);
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
