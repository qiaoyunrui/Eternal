//
// Created by yunrui on 2018/10/10.
//

#ifndef GPUImageMacros_h
#define GPUImageMacros_h

#define PLATFORM_UNKNOWN 0
#define PLATFORM_ANDROID 1
#define FLATFORM_IOS 2

#define PLATFORM PLATFORM_UNKNOWN

#if defined(__ANDROID__) || defined(ANDROID)
#undef PLATFORM
#define PLATFORM PLATFORM_ANDROID
#elif defined(__APPLE__)
#undef PLATFORM
#define PLATFORM FLATFORM_IOS
#endif

#define NS_GI_BEGIN     namespace GPUImage {
#define NS_GI_END       }
#define USING_NS_GI     using namespace GPUImage

#if PLATFORM == PLATFORM_ANDROID
#include <GLES2/gl2.h>
#include <GLES2/gl2ext.h>
#elif PLATFORM == PLATFORM_IOS
#include <OpenGLES/ES2/gl.h>
#include <OpenGLES/ES2/glext.h>
#include <OpenGLES/ES3/gl.h>
#include <OpenGLES/ES3/glext.h>
#endif

#endif


