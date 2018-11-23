#!/bin/bash
NDK=/Users/shixi_yunrui/Developer/android-ndk-r15c/
PLATFORM=$NDK/platforms/android-19/arch-arm/
PREBUILT=$NDK/toolchains/arm-linux-androideabi-4.9/prebuilt
function build_one
{
    ./configure --target-os=linux --prefix=$PREFIX \
    --enable-cross-compile \
    --enable-runtime-cpudetect \
    --disable-asm \
    --arch=arm \
    --cc=$PREBUILT/linux-x86_64/bin/arm-linux-androideabi-gcc \
    --cross-prefix=$PREBUILT/linux-x86_64/bin/arm-linux-androideabi- \
    --disable-stripping \
    --nm=$PREBUILT/linux-x86_64/bin/arm-linux-androideabi-nm \
    --sysroot=$PLATFORM \
    --enable-gpl --enable-shared --disable-static --enable-small \
    --disable-ffprobe --disable-ffplay --disable-ffmpeg --disable-ffserver --disable-debug \
    --extra-cflags="-fPIC -DANDROID -D__thumb__ -mthumb -Wfatal-errors -Wno-deprecated -mfloat-abi=softfp -marm -march=armv7-a"  
}
CPU=arm
PREFIX=$(pwd)/android/$CPU
ADDI_CFLAGS="-marm"
make clean
build_one
make
make install
