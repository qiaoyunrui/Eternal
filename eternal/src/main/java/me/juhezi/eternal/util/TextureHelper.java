package me.juhezi.eternal.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;
import android.support.annotation.DrawableRes;
import android.util.Log;

import me.juhezi.eternal.global.FunctionsKt;

import static android.opengl.GLES20.GL_LINEAR;
import static android.opengl.GLES20.GL_LINEAR_MIPMAP_LINEAR;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glDeleteTextures;
import static android.opengl.GLES20.glGenTextures;
import static android.opengl.GLES20.glGenerateMipmap;
import static android.opengl.GLES20.glTexParameteri;


public class TextureHelper {

    private static final String TAG = "TextureHelper";

    public static int loadTexture(Context context, @DrawableRes int resourceId) {
        if (context == null) return 0;
        final int[] textureObjectIds = new int[1];
        glGenTextures(1, textureObjectIds, 0);   // 创建一个纹理对象

        if (textureObjectIds[0] == 0) {
            if (FunctionsKt.isDebug()) {
                Log.w(TAG, "loadTexture: Could not generate a new OpenGL texture object.");
            }
            return 0;
        }
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);
        if (bitmap == null) {
            if (FunctionsKt.isDebug()) {
                Log.w(TAG, "loadTexture: Resource ID: " + resourceId + " could not be decoded.");
            }
            glDeleteTextures(1, textureObjectIds, 0);
            return 0;
        }
        glBindTexture(GL_TEXTURE_2D, textureObjectIds[0]);
        // 进行纹理过滤
        // 缩小的情况下，使用三线性过滤
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        // 放大的情况下，使用双线性过滤
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        // 加载位图数据到 OpenGL 中
        // 告诉 OpenGL 读入 bitmap 定义的位图数据，并把它复制到"当前绑定"的纹理对象
        GLUtils.texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);
        bitmap.recycle();
        // 生成 MIP 贴图
        glGenerateMipmap(GL_TEXTURE_2D);
        // 与纹理解除绑定
        glBindTexture(GL_TEXTURE_2D, 0);
        return textureObjectIds[0];
    }

}
