package me.juhezi.demo

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.WindowManager
import kotlinx.android.synthetic.main.activity_eternal_gpu_image.*
import me.juhezi.eternal.base.BaseActivity
import me.juhezi.eternal.global.logi
import me.juhezi.eternal.gpuimage.EternalGPUImage
import me.juhezi.eternal.gpuimage.EternalGPUImageFilter
import me.juhezi.eternal.gpuimage.buildSpecialFragmentShader
import me.juhezi.eternal.gpuimage.filter.FragmentShaderFilter
import me.juhezi.eternal.router.OriginalPicker
import me.juhezi.eternal.util.UriUtils

class EternalGPUImageActivity : BaseActivity() {

    private var gpuImage: EternalGPUImage? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_eternal_gpu_image)
        hideBottomUIMenu()
        showContent()
        toolBarVisibility = false
        btn_demo_pick_image.setOnClickListener {
            val intent = OriginalPicker.getIntent(OriginalPicker.Type.IMAGE)
            startActivityForResult(intent, 0x123)
        }
        gpuImage = EternalGPUImage(this)
        gpuImage!!.glSurfaceView = glsv_demo_show
        val filter = FragmentShaderFilter()
        gpuImage!!.setFilter(filter)
        filter.resetFragmentShader(buildSpecialFragmentShader {
            main("""
                vec2 st = gl_FragCoord.st / u_resolution;
                gl_FragColor = vec4(st.x, st.y, 0.0, 1.0);
            """.trimIndent())
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 0x123) {
                val uri = data.data
                if (uri != null) {
                    val path = UriUtils.getPathFromUri(this, uri)
                    logi("onActivityResult: 文件路径： $path")
                    doGpuImage(path)
                }
            }
        }
    }

    private fun doGpuImage(path: String) {
        gpuImage!!.setFilter(EternalGPUImageFilter())
        gpuImage!!.setImage(BitmapFactory.decodeFile(path))
    }

}