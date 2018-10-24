package me.juhezi.demo

import android.opengl.GLES20.*
import me.juhezi.demo.Constants.BYTES_PER_SHORT
import java.nio.ByteBuffer
import java.nio.ByteOrder

class IndexBuffer(vertexData: ShortArray) {
    private val bufferId: Int

    init {
        val buffers = IntArray(1)
        // allocate a buffer
        // 创建一个新的缓冲区对象
        glGenBuffers(buffers.size, buffers, 0)
        if (buffers[0] == 0) {
            throw RuntimeException("Could not create a new vertex buffer object.")
        }
        bufferId = buffers[0]

        // bind to buffer
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, bufferId)

        // Transfer data to native memory
        val vertexArray = ByteBuffer
                .allocateDirect(vertexData.size * BYTES_PER_SHORT)
                .order(ByteOrder.nativeOrder())
                .asShortBuffer()
                .put(vertexData)

        vertexArray.position(0)
        // Transfer data from native memory to the GPU buffer
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, vertexArray.capacity() * BYTES_PER_SHORT,
                vertexArray, GL_STATIC_DRAW)
        // Unbind buffer
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)
    }

    fun getBufferId() = bufferId

}