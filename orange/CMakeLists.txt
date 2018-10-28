cmake_minimum_required(VERSION 3.4.1)

# 定义常量
set(SRC_DIR src/main/cpp/lame)

# 指定关联的头文件目录
include_directories(src/main/cpp/lame)

# 查找在某个路径下的所有源文件
aux_source_directory(src/main/cpp/lame SRC_LIST)

add_subdirectory(src/main/cpp/common/)

# 设置 so 文件输出路径，要放在 add_library 之前，不然不会起作用
#set(CMAKE_LIBRARY_OUTPUT_DIRECTORY
#        ${PROJECT_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI})

add_library(
        orange
        SHARED
        src/main/cpp/mp3_encoder.cpp
        src/main/jni/orange.cpp
        ${SRC_LIST})

find_library(
        log-lib
        log)

target_link_libraries(
        orange
        log
        common)