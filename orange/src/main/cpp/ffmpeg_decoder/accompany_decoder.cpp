//
// Created by yunrui on 2018/11/2.
//

#include "accompany_decoder.hpp"

AccompanyDecoder::AccompanyDecoder() {
    this->seek_seconds = 0.0f;
    this->seek_req = false;
    this->seek_resp = false;
    accompanyFilePath = NULL;
}

AccompanyDecoder::~AccompanyDecoder() {
    if (NULL != accompanyFilePath) {
        delete[] accompanyFilePath;
        accompanyFilePath = NULL;
    }
}

int AccompanyDecoder::getMusicMeta(const char *fileString, int *metaData) {
    init(fileString);
    int sampleRate = avCodecContext->sample_rate;
    LOGI("sampleRate is %d", sampleRate);
    int bitRate = static_cast<int>(avCodecContext->bit_rate);
    LOGI("bitRate is %d", bitRate);
    destroy();
    metaData[0] = sampleRate;
    metaData[1] = bitRate;
    return 0;
}

void AccompanyDecoder::init(const char *fileString, int packetBufferSizeParam) {
    init(fileString);
    packetBufferSize = packetBufferSizeParam;
}

int AccompanyDecoder::init(const char *fileString) {
    LOGI("AccompanyDecoder::init");
    audioBuffer = NULL;
    position = -1.0f;
    audioBufferCursor = 0;
    audioBufferSize = 0;
    swrContext = NULL;
    swrBuffer = NULL;
    swrBufferSize = 0;
    seek_success_read_frame_success = true;
    isNeedFirstFrameCorrectFlag = true;
    firstFrameCorrectionInSecs = 0.0f;

    avcodec_register_all();
    av_register_all();
    avFormatContext = avformat_alloc_context();
    // 打开输出文件
    LOGI("open accompany file %s....", fileString);
    if (NULL == accompanyFilePath) {
        int length = strlen(fileString);
        accompanyFilePath = new char[length + 1];
        // 由于最后一个是 '\0' 所以 memset 的长度要设置为 length + 1
        memset(accompanyFilePath, 0, length + 1);
        memcpy(accompanyFilePath, fileString, length + 1);
    }

    int result = avformat_open_input(&avFormatContext, fileString, NULL, NULL);
    if (result != 0) {
        LOGI("can't open file %s result is %d", fileString, result);
        return -1;
    } else {
        LOGI("open file %s success and result is %d", fileString, result);
    }
    avFormatContext->max_analyze_duration = 50000;
    // 检查在文件中的信息
    result = avformat_find_stream_info(avFormatContext, NULL);
    if (result < 0) {
        LOGI("fail av_format_find_stream_info result is %d", result);
        return result;
    } else {
        LOGI("success av_format_find_stream_info result is %d", result);
    }
    stream_index = av_find_best_stream(avFormatContext, AVMEDIA_TYPE_AUDIO, -1, -1, NULL, 0);
    LOGI("stream_index is %d", stream_index);
    // 没有音频
    if (stream_index == -1) {
        LOGI("no audio stream");
        return -1;
    }
    // 音频流
    AVStream *audioStream = avFormatContext->streams[stream_index];
    if (audioStream->time_base.den && audioStream->time_base.num)
        timeBase = av_q2d(audioStream->time_base);
    else if (audioStream->codec->time_base.den &&
             audioStream->codec->time_base.num)
        timeBase = av_q2d(audioStream->codec->time_base);
    // 获得音频流的解码器 context
    avCodecContext = audioStream->codec;
    // 根据解码器 Context 找打解码器
    LOGI("avCodecContext->codec_id is %d AV_CODEC_ID_AAC is %d", avCodecContext->codec_id,
         AV_CODEC_ID_AAC);
    AVCodec *avCodec = avcodec_find_decoder(avCodecContext->codec_id);
    if (avCodec == NULL) {
        LOGI("Unsupported codec");
        return -1;
    }
    // 打开解码器
    result = avcodec_open2(avCodecContext, avCodec, NULL);
    if (result < 0) {
        LOGI("fail av_format_find_stream_info result is %d", result);
        return -1;
    } else {
        LOGI("success av_format_find_stream_info result is %d", result);
    }
    // 判断是否需要 resampler
    if (!audioCodecIsSupported()) {
        LOGI("because of audio Codec is Not Supported so we will init swresampler...");
        /**
         * 初始化resampler
         * @param s               Swr context, can be NULL
         * @param out_ch_layout   output channel layout (AV_CH_LAYOUT_*)
         * @param out_sample_fmt  output sample format (AV_SAMPLE_FMT_*).
         * @param out_sample_rate output sample rate (frequency in Hz)
         * @param in_ch_layout    input channel layout (AV_CH_LAYOUT_*)
         * @param in_sample_fmt   input sample format (AV_SAMPLE_FMT_*).
         * @param in_sample_rate  input sample rate (frequency in Hz)
         * @param log_offset      logging level offset
         * @param log_ctx         parent logging context, can be NULL
         */
        swrContext = swr_alloc_set_opts(NULL,
                                        av_get_default_channel_layout(OUT_PUT_CHANNELS),
                                        AV_SAMPLE_FMT_S16,
                                        avCodecContext->sample_rate,
                                        av_get_default_channel_layout(avCodecContext->channels),
                                        avCodecContext->sample_fmt,
                                        avCodecContext->sample_rate, 0, NULL);
        if (!swrContext || swr_init(swrContext)) {
            if (swrContext)
                swr_free(&swrContext);
            avcodec_close(avCodecContext);
            LOGI("init resampler failed...");
            return -1;
        }
    }
    LOGI("channels is %d sampleRate is %d", avCodecContext->channels, avCodecContext->sample_rate);
    pAudioFrame = av_frame_alloc();
    return 1;
}

bool AccompanyDecoder::audioCodecIsSupported() {
    return avCodecContext->sample_fmt == AV_SAMPLE_FMT_S16;
}

AudioPacket *AccompanyDecoder::decodePacket() {
    short *samples = new short[packetBufferSize];
    int stereoSampleSize = readSamples(samples, packetBufferSize);
    AudioPacket *samplePacket = new AudioPacket();
    if (stereoSampleSize > 0) {
        samplePacket->buffer = samples;
        samplePacket->size = stereoSampleSize;
        samplePacket->position = position;
    } else {
        samplePacket->size = -1;
    }
    return samplePacket;
}

int AccompanyDecoder::readSamples(short *samples, int size) {
    if (seek_req) {
        audioBufferCursor = audioBufferSize;
    }
    int sampleSize = size;
    while (size > 0) {
        if (audioBufferCursor < audioBufferSize) {
            int audioBufferDataSize = audioBufferSize - audioBufferCursor;
            int copySize = MIN(size, audioBufferDataSize);
            memcpy(samples + (sampleSize - size), audioBuffer + audioBufferCursor, copySize * 2);
            size -= copySize;
            audioBufferCursor += copySize;
        } else {
            if (readFrame() < 0) {
                break;
            }
        }
    }
    int fillSize = sampleSize - size;
    if (fillSize == 0) {
        return -1;
    }
    return fillSize;
}

void AccompanyDecoder::seek_frame() {
    LOGI("\n seek frame firstFrameCorrectionInSecs is %.6f, seek_seconds=%f, position=%f \n",
         firstFrameCorrectionInSecs, seek_seconds, position);
    float targetPosition = seek_seconds;
    float currentPosition = position;
    float frameDuration = duration;
    if (targetPosition < currentPosition) {
        this->destroy();
        this->init(accompanyFilePath);
        currentPosition = 0.0;
    }
    int readFrameCode = -1;
    while (true) {
        av_init_packet(&packet);
        readFrameCode = av_read_frame(avFormatContext, &packet);
        if (readFrameCode >= 0) {
            currentPosition += frameDuration;
            if (currentPosition >= targetPosition) {
                break;
            }
        }
        av_free_packet(&packet);
    }
    seek_resp = true;
    seek_req = false;
    seek_success_read_frame_success = false;
}

int AccompanyDecoder::readFrame() {
    if (seek_req) {
        this->seek_frame();
    }
    int ret = 1;
    av_init_packet(&packet);
    int gotFrame = 0;
    int readFrameCode = 0;
    while (true) {
        readFrameCode = av_read_frame(avFormatContext, &packet);
        if (readFrameCode >= 0) {
            if (packet.stream_index == stream_index) {
                int len = avcodec_decode_audio4(avCodecContext,
                                                pAudioFrame,
                                                &gotFrame,
                                                &packet);
                if (len < 0) {
                    LOGI("decode audio error, skip packet");
                }
                if (gotFrame) {
                    int numChannels = OUT_PUT_CHANNELS;
                    int numFrames = 0;
                    void *audioData;
                    if (swrContext) {
                        const int ratio = 2;
                        const int bufSize = av_samples_get_buffer_size(
                                NULL,
                                numChannels,
                                pAudioFrame->nb_samples * ratio,
                                AV_SAMPLE_FMT_S16,
                                1);
                        if (!swrBuffer || swrBufferSize < bufSize) {
                            swrBufferSize = bufSize;
                            swrBuffer = realloc(swrBuffer, swrBufferSize);
                        }
                        byte *outbuf[2] = {(byte *) swrBuffer, NULL};
                        numFrames = swr_convert(swrContext, outbuf,
                                                pAudioFrame->nb_samples * ratio,
                                                (const uint8_t **) pAudioFrame->data,
                                                pAudioFrame->nb_samples);
                        if (numFrames < 0) {
                            LOGI("fail resample audio");
                            ret = -1;
                            break;
                        }
                        audioData = swrBuffer;
                    } else {
                        if (avCodecContext->sample_fmt != AV_SAMPLE_FMT_S16) {
                            LOGI("bucheck, audio format is invalid");
                            ret = -1;
                            break;
                        }
                        audioData = pAudioFrame->data[0];
                        numFrames = pAudioFrame->nb_samples;
                    }
                    if (isNeedFirstFrameCorrectFlag && position >= 0) {
                        float expectedPosition = position + duration;
                        float actualPosition =
                                av_frame_get_best_effort_timestamp(pAudioFrame) * timeBase;
                        firstFrameCorrectionInSecs = actualPosition - expectedPosition;
                        isNeedFirstFrameCorrectFlag = false;
                    }
                    duration = av_frame_get_pkt_duration(pAudioFrame) * timeBase;
                    position = av_frame_get_best_effort_timestamp(pAudioFrame) * timeBase -
                               firstFrameCorrectionInSecs;
                    if (!seek_success_read_frame_success) {
                        LOGI("position is %.6f", position);
                        actualSeekPosition = position;
                        seek_success_read_frame_success = true;
                    }
                    audioBufferSize = numFrames * numChannels;
//					LOGI(" \n duration is %.6f position is %.6f audioBufferSize is %d\n", duration, position, audioBufferSize);
                    audioBuffer = (short *) audioData;
                    audioBufferCursor = 0;
                    break;
                }
            }
        } else {
            ret = -1;
            break;
        }
    }
    av_free_packet(&packet);
    return ret;
}

void AccompanyDecoder::destroy() {
    LOGI("start destroy!!!");
    if (NULL != swrBuffer) {
        free(swrBuffer);
        swrBuffer = NULL;
        swrBufferSize = 0;
    }
    if (NULL != swrContext) {
        swr_free(&swrContext);
        swrContext = NULL;
    }
    if (NULL != pAudioFrame) {
        av_free(pAudioFrame);
        pAudioFrame = NULL;
    }
    if (NULL != avCodecContext) {
        avcodec_close(avCodecContext);
        avCodecContext = NULL;
    }
    if (NULL != avFormatContext) {
        LOGI("leave LiveReceiver::destory");
        avformat_close_input(&avFormatContext);
        avFormatContext = NULL;
    }
//	LOGI("end destroy!!!");
}