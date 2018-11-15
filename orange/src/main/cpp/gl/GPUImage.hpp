//
// Created by yunrui on 2018/11/15.
//

#ifndef ETERNAL_GPUIMAGE_HPP
#define ETERNAL_GPUIMAGE_HPP

#include <GLES2/gl2.h>
#include <string>


class GPUImage {
private:
    GLfloat vertices[];
    const std::string vertexShaderString;
    const std::string fragmentShaderString;
public:
    GPUImage();

    void draw();
};

#endif //ETERNAL_GPUIMAGE_HPP
