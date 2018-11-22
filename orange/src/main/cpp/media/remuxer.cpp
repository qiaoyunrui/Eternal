//
// Created by yunrui on 2018/11/22.
//

#include "remuxer.hpp"

int Remuxer::Start(const char *input_file_path, const char *output_file_path) {
    AVOutputFormat *ofmt = NULL;
    AVFormatContext *ifmt_ctx = NULL, *ofmt_ctx = NULL;
    AVPacket pkt;
    int ret, i;
    av_register_all();
    // 输入
    if ((ret = avformat_open_input(&ifmt_ctx, input_file_path, 0, 0)) < 0) {
        LOGE("Could not open input file.");
        End(ifmt_ctx,ofmt_ctx,ret);
        return -1;
    }
    av_dump_format(ifmt_ctx, 0, input_file_path, 0);
    // 输出
    avformat_alloc_output_context2(&ofmt_ctx, NULL, NULL, output_file_path);
    if (!ofmt_ctx) {
        LOGE("Could not create output context");
        ret = AVERROR_UNKNOWN;
        End(ifmt_ctx,ofmt_ctx,ret);
        return -1;
    }
    ofmt = ofmt_ctx->oformat;
    for (i = 0; i < ifmt_ctx->nb_streams; i++) {
        // 根据输入流创建输出流
        AVStream *in_stream = ifmt_ctx->streams[i];
        AVStream *out_stream = avformat_new_stream(ofmt_ctx, in_stream->codec->codec);
        if (!out_stream) {
            LOGE("Failed allocating output stream.\n");
            ret = AVERROR_UNKNOWN;
            End(ifmt_ctx,ofmt_ctx,ret);
            return -1;
        }
        // 复制 AVCodecContext 的设置
        ret = avcodec_copy_context(out_stream->codec, in_stream->codec);
        if (ret < 0) {
            LOGE("Failed to copy context from input to output stream codec context.\n");
            End(ifmt_ctx,ofmt_ctx,ret);
            return -1;
        }
        out_stream->codec->codec_tag = 0;
        if (ofmt_ctx->oformat->flags & AVFMT_GLOBALHEADER) {
            out_stream->codec->flags |= CODEC_FLAG_GLOBAL_HEADER;
        }
    }
    // 输出格式
    av_dump_format(ofmt_ctx, 0, output_file_path, 1);
    // 打开输出文件
    if (!(ofmt->flags & AVFMT_NOFILE)) {
        ret = avio_open(&ofmt_ctx->pb, output_file_path, AVIO_FLAG_WRITE);
        if (ret < 0) {
            LOGE("Could not open output file %s", output_file_path);
            End(ifmt_ctx,ofmt_ctx,ret);
            return -1;
        }
    }

    // 写文件头
    ret = avformat_write_header(ofmt_ctx, NULL);
    if (ret < 0) {
        LOGE("Error occurred when opening output file.\n");
        End(ifmt_ctx,ofmt_ctx,ret);
        return -1;
    }

    int frame_index = 0;
    while (1) {
        AVStream *in_stream, *out_stream;
        // 获取一个 AVPacket
        ret = av_read_frame(ifmt_ctx, &pkt);
        if (ret < 0) {
            break;
        }
        in_stream = ifmt_ctx->streams[pkt.stream_index];
        out_stream = ofmt_ctx->streams[pkt.stream_index];

        // 转换 PTS/DTS
        pkt.pts = av_rescale_q_rnd(pkt.pts, in_stream->time_base,
                                   out_stream->time_base,
                                   (AVRounding)(AV_ROUND_NEAR_INF | AV_ROUND_PASS_MINMAX));
        pkt.dts = av_rescale_q_rnd(pkt.dts, out_stream->time_base,
                                   out_stream->time_base,
                                   (AVRounding)(AV_ROUND_NEAR_INF | AV_ROUND_PASS_MINMAX));
        pkt.duration = av_rescale_q(pkt.duration, in_stream->time_base,
                                    out_stream->time_base);
        pkt.pos = -1;
        // 写入
        ret = av_interleaved_write_frame(ofmt_ctx, &pkt);
        if (ret < 0) {
            LOGE("Error muxing packet.\n");
            break;
        }
        LOGE("Write %8d frames to output file.\n", frame_index);
        av_free_packet(&pkt);
        frame_index++;
    }
    // 写文件尾
    av_write_trailer(ofmt_ctx);
    
    End(ifmt_ctx,ofmt_ctx,ret);

    return 0;
}

void Remuxer::End(AVFormatContext *ifmt_ctx, AVFormatContext *ofmt_ctx, int ret) {
    avformat_close_input(&ifmt_ctx);
    if (ofmt_ctx && !(ofmt_ctx->flags & AVFMT_NOFILE)) {
        avio_close(ofmt_ctx->pb);
    }
    avformat_free_context(ofmt_ctx);
    if (ret < 0 && ret != AVERROR_EOF) {
        LOGE("Error occurred.\n");
    }
}

