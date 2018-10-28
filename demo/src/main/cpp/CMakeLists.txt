cmake_minimum_required(VERSION 3.4.1)

add_definitions("-DGLM_FORCE_SIZE_T_LENGTH -DGLM_FORCE_RADIANS")

add_subdirectory(glm)

add_library(wbgpuimage SHARED
        android/GPUImageJNI.cpp
        gpuimage/GPUImageFilter.cpp
        android/GPUImageMacros.h)

target_include_directories(wbgpuimage PRIVATE ${CMAKE_CURRENT_SOURCE_DIR}
        wbgpuimage PRIVATE $<BUILD_INTERFACE:${CMAKE_CURRENT_SOURCE_DIR}/gpuimage>
        wbgpuimage PRIVATE $<BUILD_INTERFACE:${CMAKE_CURRENT_SOURCE_DIR}/android>
        wbgpuimage PRIVATE $<BUILD_INTERFACE:${CMAKE_CURRENT_SOURCE_DIR}/glm>
        wbgpuimage PRIVATE $<BUILD_INTERFACE:${CMAKE_CURRENT_SOURCE_DIR}/glm/gtc>
        wbgpuimage PRIVATE $<BUILD_INTERFACE:${CMAKE_CURRENT_SOURCE_DIR}/glm/gtx>)

# add lib dependencies
target_link_libraries(
        wbgpuimage
        glm
        GLESv2
        log
)
