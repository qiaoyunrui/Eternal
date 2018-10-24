package me.juhezi.demo

import android.opengl.GLES20.*
import me.juhezi.demo.Constants.BYTES_PER_FLOAT
import java.nio.ByteBuffer
import java.nio.ByteOrder

class VertexBuffer(vertexData: FloatArray) {

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
        glBindBuffer(GL_ARRAY_BUFFER, buffers[0])

        // Transfer data to native memory
//        val vertexArray = ByteBuffer
//                .allocate(vertexData.size * BYTES_PER_FLOAT)
//                .order(ByteOrder.nativeOrder())
//                .asFloatBuffer()
//                .put(vertexData)

        val vertexArray = ByteBuffer
                .allocateDirect(vertexData.size * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertexData)

        vertexArray.position(0)
        // Transfer data from native memory to the GPU buffer
        glBufferData(GL_ARRAY_BUFFER, vertexArray.capacity() * BYTES_PER_FLOAT,
                vertexArray, GL_STATIC_DRAW)
        // Unbind buffer
        glBindBuffer(GL_ARRAY_BUFFER, 0)
    }

    fun setVertexAttribPointer(dataOffset: Int, attributeLocation: Int,
                               componentCount: Int, stride: Int) {
        glBindBuffer(GL_ARRAY_BUFFER, bufferId)
        glVertexAttribPointer(attributeLocation, componentCount,
                GL_FLOAT, false, stride, dataOffset)
        glEnableVertexAttribArray(attributeLocation)
        glBindBuffer(GL_ARRAY_BUFFER, 0)
    }

}