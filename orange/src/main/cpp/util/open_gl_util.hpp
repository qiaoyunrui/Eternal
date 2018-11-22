//
// Created by yunrui on 2018/11/15.
//

#include <GLES2/gl2.h>

#ifndef ETERNAL_OPENGLUTIL_HPP
#define ETERNAL_OPENGLUTIL_HPP

class OpenGLUtil {
public:
    static int LoadProgram(const char *vertex_Source, const char *fragment_source);

private:
    static bool LoadShader(const char *source, int shader_type, GLuint *shader);
};

#endif //ETERNAL_OPENGLUTIL_HPP
