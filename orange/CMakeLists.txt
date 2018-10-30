cmake_minimum_required(VERSION 3.4.1)

# 定义常量
set(SRC_DIR src/main/cpp/lame)

# 指定关联的头文件目录
include_directories(src/main/cpp/lame)
include_directories(libs/include)

# 查找在某个路径下的所有源文件
aux_source_directory(src/main/cpp/lame SRC_LIST)

# 设置 so 文件输出路径，要放在 add_library 之前，不然不会起作用
#set(CMAKE_LIBRARY_OUTPUT_DIRECTORY
#        ${PROJECT_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI})

add_library( avutil
             SHARED
             IMPORTED )
set_target_properties( avutil
                       PROPERTIES IMPORTED_LOCATION
                       ${CMAKE_SOURCE_DIR}/src/main/jniLibs/armeabi/libavutil.so )

add_library( swresample
             SHARED
             IMPORTED )
set_target_properties( swresample
                       PROPERTIES IMPORTED_LOCATION
                       ${CMAKE_SOURCE_DIR}/src/main/jniLibs/armeabi/libswresample.so )

add_library( avcodec
             SHARED
             IMPORTED )
set_target_properties( avcodec
                       PROPERTIES IMPORTED_LOCATION
                       ${CMAKE_SOURCE_DIR}/src/main/jniLibs/armeabi/libavcodec.so )

add_library( avfilter
             SHARED
             IMPORTED)
set_target_properties( avfilter
                       PROPERTIES IMPORTED_LOCATION
                       ${CMAKE_SOURCE_DIR}/src/main/jniLibs/armeabi/libavfilter.so )

add_library( swscale
             SHARED
             IMPORTED)
set_target_properties( swscale
                       PROPERTIES IMPORTED_LOCATION
                       ${CMAKE_SOURCE_DIR}/src/main/jniLibs/armeabi/libswscale.so )

add_library( avdevice
             SHARED
             IMPORTED)
set_target_properties( avdevice
                       PROPERTIES IMPORTED_LOCATION
                       ${CMAKE_SOURCE_DIR}/src/main/jniLibs/armeabi/libavdevice.so )

add_library( avformat
             SHARED
             IMPORTED)
set_target_properties( avformat
                       PROPERTIES IMPORTED_LOCATION
                       ${CMAKE_SOURCE_DIR}/src/main/jniLibs/armeabi/libavformat.so )

add_library( postproc
             SHARED
             IMPORTED)
set_target_properties( postproc
                       PROPERTIES IMPORTED_LOCATION
                       ${CMAKE_SOURCE_DIR}/src/main/jniLibs/armeabi/libpostproc.so )

add_library(
        orange
        SHARED
        src/main/cpp/mp3_encoder.cpp
        src/main/cpp/ffmpeg_test.c
        src/main/jni/orange.cpp
        ${SRC_LIST})

find_library(
        log-lib
        log)

target_link_libraries(
        orange
        log
        avutil
        swresample
        avcodec
        avfilter
        swscale
        avdevice
        avformat
        postproc)