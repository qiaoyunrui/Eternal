//
// Created by yunrui on 2018/10/27.
//

#ifndef ETERNAL_MP3_ENCODER_HPP
#define ETERNAL_MP3_ENCODER_HPP

#include <cstdio>
#include "../lame/lame.h"

class Mp3Encoder {
private:
    FILE *pcmFile;
    FILE *mp3File;
    lame_t lameClient;

public:
    Mp3Encoder();

    ~Mp3Encoder();

    int Init(const char *pcmFilePath, const char *mp3FilePath,
             int sampleRate, int channels, int bitRate);

    void Encode();

    void Destory();

};

#endif //ETERNAL_MP3_ENCODER_HPP
