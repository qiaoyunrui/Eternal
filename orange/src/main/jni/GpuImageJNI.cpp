//
// Created by yunrui on 2018/11/15.
//
#include <jni.h>
#include <GLES2/gl2.h>



extern "C"
JNIEXPORT void JNICALL
Java_me_juhezi_orange_GPUImage_onSurfaceCreated(JNIEnv *env, jclass type) {

}

extern "C"
JNIEXPORT void JNICALL
Java_me_juhezi_orange_GPUImage_onSurfaceChanged(JNIEnv *env, jclass type, jint width, jint height) {
    glViewport(0, 0, width, height);
}

extern "C"
JNIEXPORT void JNICALL
Java_me_juhezi_orange_GPUImage_onDrawFrame(JNIEnv *env, jclass type) {
    glClear(GL_COLOR_BUFFER_BIT);
}

