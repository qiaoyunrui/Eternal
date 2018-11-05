//
// Created by yunrui on 2018/11/2.
//

#ifndef ETERNAL_ACCOMPANY_DECODER_HPP
#define ETERNAL_ACCOMPANY_DECODER_HPP

#include <stdio.h>
#include <stdlib.h>
#include <time.h>

#ifndef UINT64_C
#define UINT64_C(value)__CONCAT(value,ULL)
#endif

#ifndef INT64_MIN
#define INT64_MIN  (-9223372036854775807LL - 1)
#endif

#ifndef INT64_MAX
#define INT64_MAX	9223372036854775807LL
#endif

typedef struct AudioPacket {
    static const int AUDIO_PACKET_ACTION_PLAY = 0;
    static const int AUDIO_PACKET_ACTION_PAUSE = 100;
    static const int AUDIO_PACKET_ACTION_SEEK = 101;

    short *buffer;
    int size;
    float position;
    int action;

    float extra_param1;
    float extra_param2;

    AudioPacket() {
        buffer = NULL;
        size = 0;
        position = -1;
        action = 0;
        extra_param1 = 0;
        extra_param2 = 0;
    }

    ~AudioPacket() {
        if (NULL != buffer) {
            delete[] buffer;
            buffer = NULL;
        }
    }

} AudioPacket;

#include "../common/CommonTools.h"

extern "C" {
#include "libavcodec/avcodec.h"
#include "libavformat/avformat.h"
#include "libavutil/avutil.h"
#include "libavutil/samplefmt.h"
#include "libavutil/common.h"
#include "libavutil/channel_layout.h"
#include "libavutil/opt.h"
#include "libavutil/imgutils.h"
#include "libavutil/mathematics.h"
#include "libswscale/swscale.h"
#include "libswresample/swresample.h"
};

#define OUT_PUT_CHANNELS 2

class AccompanyDecoder {
private:
    // 如果使用了快进或者快退命令，则先设置一下参数
    bool seek_req;
    bool seek_resp;
    float seek_seconds;

    float actualSeekPosition;

    AVFormatContext *avFormatContext;
    AVCodecContext *avCodecContext;
    int stream_index;
    float timeBase;
    AVFrame *pAudioFrame;
    AVPacket packet;

    char *accompanyFilePath;
    bool seek_success_read_frame_success;
    int packetBufferSize;

    // 每次解码出来的 AudioBuffer 以及这个 AudioBuffer 的时间戳以及当前这个类对于这个 AudioBuffer 的操作情况
    short *audioBuffer;
    float position;
    int audioBufferCursor;
    int audioBufferSize;
    float duration;
    bool isNeedFirstFrameCorrectFlag;
    float firstFrameCorrectionInSecs;

    SwrContext *swrContext;
    void *swrBuffer;
    int swrBufferSize;

    int init(const char *fileString);

    int readSamples(short *samples, int size);

    int readFrame();

    bool audioCodecIsSupported();

public:
    AccompanyDecoder();

    virtual ~AccompanyDecoder();

    // 获取采样率以及比特率
    virtual int getMusicMeta(const char *fileString, int *metaData);

    // 初始化这个 decoder，即打开指定的 mp3 文件
    virtual void init(const char *fileString, int packetBufferSizeParam);

    virtual AudioPacket *decodePacket();

    // 销毁这个 decoder
    virtual void destroy();

    void setSeekReq(bool seekReqParam) {
        seek_req = seekReqParam;
        if (seek_req) {
            seek_resp = false;
        }
    };

    bool hasSeekReq() {
        return seek_req;
    };

    bool hasSeekResp() {
        return seek_resp;
    };


    void setPosition(float seconds) {
        actualSeekPosition = -1;
        this->seek_seconds = seconds;
        this->seek_req = true;
        this->seek_resp = false;
    };

    float getActualSeekPosition() {
        float ret = actualSeekPosition;
        if (ret != -1) {
            actualSeekPosition = -1;
        }
        return ret;
    }

    virtual void seek_frame();
};

#endif //ETERNAL_ACCOMPANY_DECODER_HPP