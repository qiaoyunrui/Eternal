package me.juhezi.demo

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaExtractor
import android.media.MediaFormat
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.widget.Button
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import me.juhezi.eternal.base.BaseActivity
import me.juhezi.eternal.extension.i
import me.juhezi.eternal.media.getMediaFormat
import me.juhezi.eternal.router.OriginalPicker
import me.juhezi.eternal.service.AudioRecordService
import me.juhezi.eternal.util.UriUtils
import java.io.IOException

class MainActivity : BaseActivity() {

    private var mPickAudioButton: Button? = null
    private var mGLButton: Button? = null
    private var mGpuImageButton: Button? = null
    private var mGLButton2: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mPickAudioButton = findViewById(R.id.btn_demo_pick_audio)
        mPickAudioButton!!.setOnClickListener {
            if (!checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) return@setOnClickListener
            val intent = OriginalPicker.getIntent(OriginalPicker.Type.AUDIO)
            startActivityForResult(intent, 0x123)
        }
        mGLButton = findViewById(R.id.btn_demo_gl)
        mGLButton!!.setOnClickListener { startActivity(Intent(this, GLActivity::class.java)) }
        mGpuImageButton = findViewById(R.id.btn_demo_gpu_image)
        mGpuImageButton!!.setOnClickListener { startActivity(Intent(this, ImageActivity::class.java)) }
        mGLButton2 = findViewById(R.id.btn_demo_gl_2)
        mGLButton2!!.setOnClickListener {
            startActivity(Intent(this, GL2Activity::class.java))
        }
        btn_record.setOnClickListener {
            val path = "/storage/emulated/0/input1.pcm"
            if (!checkPermission(Manifest.permission.RECORD_AUDIO)) return@setOnClickListener
            AudioRecordService.instance.initMeta()
            AudioRecordService.instance.start(path)
            mGLButton2!!.postDelayed({
                AudioRecordService.instance.stop()
                Toast.makeText(this@MainActivity, "Done, path is $path", Toast.LENGTH_SHORT).show()
            }, (10 * 1000).toLong())
        }
        btn_test.setOnClickListener {
            getMediaFormat("/storage/emulated/0/demo1.mp4")
                    .forEach { i(it) }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 0x123) {
                val uri = data.data
                if (uri != null) {
                    val path = UriUtils.getPathFromUri(this, uri)
                    Log.i(TAG, "onActivityResult: 文件路径： " + path!!)
                    initDecode(path)
                }
            }
        }
    }

    // TODO: 2018/8/2 扩展为一个公用组件
    private fun checkPermission(permission: String): Boolean {
        if (ContextCompat.checkSelfPermission(this,
                        permission) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    arrayOf(permission),
                    0x123)
            return false
        }
        return true
    }

    // 初始化解码器
    private fun initDecode(sourcePath: String?) {
        var mediaCodec: MediaCodec? = null
        try {
            val mediaExtractor = MediaExtractor()
            mediaExtractor.setDataSource(sourcePath!!)
            for (i in 0 until mediaExtractor.trackCount) { // 遍历轨道
                val format = mediaExtractor.getTrackFormat(i)
                val mime = format.getString(MediaFormat.KEY_MIME)
                Log.i(TAG, "initDecode: $mime")
                if (mime.startsWith("audio")) {
                    mediaExtractor.selectTrack(i)
                    mediaCodec = MediaCodec.createDecoderByType(mime) // 创建 Decode 解码器
                    mediaCodec!!.configure(format, null, null, 0)
                    break
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        if (mediaCodec == null) {
            Log.e(TAG, "Create MediaCodec failed")
            return
        }
        mediaCodec.start()    // 启动 MediaCodec，等待传入数据
        val decodeInputBuffers = mediaCodec.inputBuffers
        val decodeOutputBuffers = mediaCodec.outputBuffers
        val bufferInfo = MediaCodec.BufferInfo()
        Log.i(TAG, "initDecode: " + decodeInputBuffers.size)
    }

    /**
     * 初始化 AAC 编码器
     */
    private fun initEncoder() {
        var mediaCodec: MediaCodec? = null
        try {
            val format = MediaFormat.createAudioFormat(MediaFormat.MIMETYPE_AUDIO_AAC, 44100, 2)  // 类型、采样率、声道数
            format.setInteger(MediaFormat.KEY_BIT_RATE, 96000) //比特率
            format.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC)
            format.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, 100 * 1024)  // 作用于 inoutBuffer 的大小
            mediaCodec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_AUDIO_AAC)
            mediaCodec!!.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        if (mediaCodec == null) {
            Log.e(TAG, "Create mediaEncode failed")
            return
        }
        mediaCodec.start()

    }

    companion object {

        private val TAG = "DemoMainActivity"
    }

}

// TODO: 2018/8/2 扩展成 Router 的一个方法
//intent.setType(“image/*”);//选择图片
//intent.setType(“audio/*”); //选择音频
//intent.setType(“video/*”); //选择视频 （mp4 3gp 是android支持的视频格式）
//intent.setType(“video/*;image/*”);//同时选择视频和图片