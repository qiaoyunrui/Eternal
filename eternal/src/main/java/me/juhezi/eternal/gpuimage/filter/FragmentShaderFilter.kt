package me.juhezi.eternal.gpuimage.filter

import me.juhezi.eternal.gpuimage.EternalGPUImageFilter

class FragmentShaderFilter :
        EternalGPUImageFilter(EternalGPUImageFilter.NO_FILTER_VERTEX_SHADER, COLOR_FRAGMENT_SHADER) {
    companion object {
        val COLOR_FRAGMENT_SHADER = """
            precision mediump float;

            void main() {
                gl_FragColor = vec4(1.0, 0.0, 0.0, 1.0);
            }
        """.trimIndent()
    }
}