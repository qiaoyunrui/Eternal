package me.juhezi.demo;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import org.jetbrains.annotations.Nullable;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageSepiaFilter;
import me.juhezi.eternal.base.BaseActivity;
import me.juhezi.eternal.router.OriginalPicker;
import me.juhezi.eternal.util.UriUtils;

public class ImageActivity extends BaseActivity {

    private static final String TAG = "ImageActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        setToolBarVisibility(false);
        findViewById(R.id.btn_demo_pick_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = OriginalPicker.getIntent(OriginalPicker.Type.IMAGE);
                startActivityForResult(intent, 0x123);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 0x123) {
                Uri uri = data.getData();
                if (uri != null) {
                    String path = UriUtils.getPathFromUri(this, uri);
                    Log.i(TAG, "onActivityResult: 文件路径： " + path);
                    doGpuImage(uri);
                }
            }
        }
    }

    private void doGpuImage(Uri uri) {
        GPUImage gpuImage = new GPUImage(this);
        gpuImage.setGLSurfaceView(findViewById(R.id.glsv_demo_show));
        gpuImage.setImage(uri);
        gpuImage.setFilter(new GPUImageSepiaFilter());
    }

}
