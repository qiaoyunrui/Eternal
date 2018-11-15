//
// Created by yunrui on 2018/11/15.
//

#include "OpenGLUtil.hpp"

const int MAX_DEBUG_INFO_LEN = 4096;

int OpenGLUtil::LoadProgram(const char *vertex_source, const char *fragment_source) {

    GLuint program_id = static_cast<GLuint>(-1);

    GLuint vertex_shader = 0;
    GLuint fragment_shader = 0;
    if (!LoadShader(vertex_source,GL_VERTEX_SHADER,&vertex_shader)) {
        return program_id;
    }
    if (!LoadShader(fragment_source,GL_FRAGMENT_SHADER,&fragment_shader)) {
        return program_id;
    }
    GLint prog_link_status = 0;
    program_id = glCreateProgram();
    glAttachShader(program_id,
                   vertex_shader);
    glAttachShader(program_id,
                   fragment_shader);
    glLinkProgram(program_id);
    glDetachShader(program_id,vertex_shader);
    glDetachShader(program_id,fragment_shader);
    glDeleteShader(vertex_shader);
    glDeleteShader(fragment_shader);
    return program_id;
}

bool OpenGLUtil::LoadShader(const char *source, int shader_type, GLuint *shader) {
    int compiled = GL_FALSE;
    *shader = glCreateShader(static_cast<GLenum>(shader_type));
    glShaderSource(static_cast<GLuint>(*shader), 1, &source, NULL);
    glCompileShader(static_cast<GLuint>(*shader));
    glGetShaderiv(static_cast<GLuint>(*shader), GL_COMPILE_STATUS, &compiled);
    return compiled == GL_TRUE;
}
