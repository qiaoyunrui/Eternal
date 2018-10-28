package me.juhezi.demo;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.nio.ByteBuffer;

import me.juhezi.eternal.base.BaseActivity;
import me.juhezi.eternal.router.OriginalPicker;
import me.juhezi.eternal.service.AudioRecordService;
import me.juhezi.eternal.util.UriUtils;

public class MainActivity extends BaseActivity {

    private static final String TAG = "DemoMainActivity";

    private Button mPickAudioButton;
    private Button mGLButton;
    private Button mGpuImageButton;
    private Button mGLButton2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPickAudioButton = findViewById(R.id.btn_demo_pick_audio);
        mPickAudioButton.setOnClickListener(v -> {
            if (!checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) return;
            Intent intent = OriginalPicker.getIntent(OriginalPicker.Type.AUDIO);
            startActivityForResult(intent, 0x123);
        });
        mGLButton = findViewById(R.id.btn_demo_gl);
        mGLButton.setOnClickListener(v ->
                startActivity(new Intent(this, GLActivity.class)));
        mGpuImageButton = findViewById(R.id.btn_demo_gpu_image);
        mGpuImageButton.setOnClickListener(v -> {
            startActivity(new Intent(this, ImageActivity.class));
        });
        mGLButton2 = findViewById(R.id.btn_demo_gl_2);
        mGLButton2.setOnClickListener(v -> {
//            startActivity(new Intent(this, GL2Activity.class));
            if (!checkPermission(Manifest.permission.RECORD_AUDIO)) return;
            AudioRecordService.Companion.getInstance().initMeta();
            AudioRecordService.Companion.getInstance().start("/storage/emulated/0/input1.pcm");
            mGLButton2.postDelayed(() -> {
                AudioRecordService.Companion.getInstance().stop();
                Toast.makeText(MainActivity.this, "Done", Toast.LENGTH_SHORT).show();
            }, 10 * 1000);
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
                    initDecode(path);
                }
            }
        }
    }

    // TODO: 2018/8/2 扩展为一个公用组件
    private boolean checkPermission(String permission) {
        if (ContextCompat.checkSelfPermission(this,
                permission)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{permission},
                    0x123);
            return false;
        }
        return true;
    }

    // 初始化解码器
    private void initDecode(String sourcePath) {
        MediaCodec mediaCodec = null;
        try {
            MediaExtractor mediaExtractor = new MediaExtractor();
            mediaExtractor.setDataSource(sourcePath);
            for (int i = 0; i < mediaExtractor.getTrackCount(); i++) { // 遍历轨道
                MediaFormat format = mediaExtractor.getTrackFormat(i);
                String mime = format.getString(MediaFormat.KEY_MIME);
                Log.i(TAG, "initDecode: " + mime);
                if (mime.startsWith("audio")) {
                    mediaExtractor.selectTrack(i);
                    mediaCodec = MediaCodec.createDecoderByType(mime); // 创建 Decode 解码器
                    mediaCodec.configure(format, null, null, 0);
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (mediaCodec == null) {
            Log.e(TAG, "Create MediaCodec failed");
            return;
        }
        mediaCodec.start();    // 启动 MediaCodec，等待传入数据
        ByteBuffer[] decodeInputBuffers = mediaCodec.getInputBuffers();
        ByteBuffer[] decodeOutputBuffers = mediaCodec.getOutputBuffers();
        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
        Log.i(TAG, "initDecode: " + decodeInputBuffers.length);
    }

    /**
     * 初始化 AAC 编码器
     */
    private void initEncoder() {
        MediaCodec mediaCodec = null;
        try {
            MediaFormat format = MediaFormat.createAudioFormat(MediaFormat.MIMETYPE_AUDIO_AAC, 44100, 2);  // 类型、采样率、声道数
            format.setInteger(MediaFormat.KEY_BIT_RATE, 96000); //比特率
            format.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC);
            format.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, 100 * 1024);  // 作用于 inoutBuffer 的大小
            mediaCodec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_AUDIO_AAC);
            mediaCodec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (mediaCodec == null) {
            Log.e(TAG, "Create mediaEncode failed");
            return;
        }
        mediaCodec.start();

    }

}

// TODO: 2018/8/2 扩展成 Router 的一个方法
//intent.setType(“image/*”);//选择图片
//intent.setType(“audio/*”); //选择音频
//intent.setType(“video/*”); //选择视频 （mp4 3gp 是android支持的视频格式）
//intent.setType(“video/*;image/*”);//同时选择视频和图片