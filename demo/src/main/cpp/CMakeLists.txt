cmake_minimum_required(VERSION 3.4.1)

#set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=gnu++11 -Wall")
add_definitions("-DGLM_FORCE_SIZE_T_LENGTH -DGLM_FORCE_RADIANS")

# Import the CMakeLists.txt for the glm library
add_subdirectory(glm)

# now build app's shared lib
add_library(wbgpuimage SHARED
    android/GPUImageJNI.cpp
    gpuimage/GPUImageFilter.cpp android/GPUImageMacros.h)

target_include_directories(wbgpuimage PRIVATE ${CMAKE_CURRENT_SOURCE_DIR}
                            wbgpuimage PRIVATE $<BUILD_INTERFACE:${CMAKE_CURRENT_SOURCE_DIR}/gpuimage>
                            wbgpuimage PRIVATE $<BUILD_INTERFACE:${CMAKE_CURRENT_SOURCE_DIR}/android>
                            wbgpuimage PRIVATE $<BUILD_INTERFACE:${CMAKE_CURRENT_SOURCE_DIR}/glm>
                            wbgpuimage PRIVATE $<BUILD_INTERFACE:${CMAKE_CURRENT_SOURCE_DIR}/glm/gtc>
                            wbgpuimage PRIVATE $<BUILD_INTERFACE:${CMAKE_CURRENT_SOURCE_DIR}/glm/gtx>

     )

# add lib dependencies
target_link_libraries(
     wbgpuimage
     glm
     GLESv2
     log
     )
