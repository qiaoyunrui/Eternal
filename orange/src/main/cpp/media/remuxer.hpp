//
// 封装转换器
// Created by yunrui on 2018/11/22.
//

#ifndef ETERNAL_REMUXER_HPP
#define ETERNAL_REMUXER_HPP

#include "../common/common_tools.h"

extern "C"
{
#include "libavformat/avformat.h"
};

class Remuxer {
public:
    int Start(const char *input_file_path, const char *output_file_path);

private:
    void End(AVFormatContext *ifmt_ctx, AVFormatContext *ofmt_ctx,int ret);
};

#endif //ETERNAL_REMUXER_HPP
