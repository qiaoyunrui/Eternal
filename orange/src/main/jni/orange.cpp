#include <jni.h>
#include "../cpp/media/mp3_encoder.hpp"

Mp3Encoder *encoder = NULL;

extern "C"
JNIEXPORT jint JNICALL
Java_me_juhezi_orange_LameBridge_init(JNIEnv *env, jclass type, jstring pcmPath_,
                                      jint audioChannels, jint bitRate, jint sampleRate,
                                      jstring mp3Path_) {

    int ret = -1;

    const char *pcmPath = env->GetStringUTFChars(pcmPath_, NULL);
    const char *mp3Path = env->GetStringUTFChars(mp3Path_, NULL);

    encoder = new Mp3Encoder();
    ret = encoder->Init(pcmPath, mp3Path, sampleRate, audioChannels, bitRate);

    env->ReleaseStringUTFChars(pcmPath_, pcmPath);
    env->ReleaseStringUTFChars(mp3Path_, mp3Path);
    return ret;
}

extern "C"
JNIEXPORT void JNICALL
Java_me_juhezi_orange_LameBridge_encode(JNIEnv *env, jclass type) {

    if (encoder) {
        encoder->Encode();
    }

}

extern "C"
JNIEXPORT void JNICALL
Java_me_juhezi_orange_LameBridge_destroy(JNIEnv *env, jclass type) {

    if (encoder) {
        encoder->Destory();
        delete encoder;
        encoder = NULL;
    }

}