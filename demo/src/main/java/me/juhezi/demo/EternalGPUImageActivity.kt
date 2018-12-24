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
        filter.resetFragmentShader(buildRepeatFragmentShader())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 0x123) {
                val uri = data?.data
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

    private fun buildRepeatFragmentShader() = buildSpecialFragmentShader {
        function("""float circle(in vec2 _st, in float _radius) {
                        vec2 l = _st - vec2(0.5);   // 到圆心的距离
                        return 1.0 - smoothstep(_radius - (_radius * 0.01),
                            _radius + (_radius * 0.01),
                            dot(l, l) * 4.0);
                    }
        """.trimIndent())
        main("""vec2 st = gl_FragCoord.xy / u_resolution;
                vec3 color = vec3(0.0);

                st *= 4.0;  // 坐标系扩大为 3 倍
                st = fract(st); // 取小数部分

                color = vec3(st, 0.0);
//                color = vec3(circle(st, 0.5));
                gl_FragColor = vec4(color,1.0);
            """.trimIndent())
    }

}