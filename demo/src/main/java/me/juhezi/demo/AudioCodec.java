package me.juhezi.demo;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class AudioCodec {

    private static final String TAG = "AudioCodec";
    private String encodeType;
    private String srcPath;
    private String dstPath;
    private MediaCodec mediaDecode;
    private MediaCodec mediaEncode;
    private MediaExtractor mediaExtractor;
    private ByteBuffer[] decodeInputBuffers;
    private ByteBuffer[] decodeOutputBuffers;
    private ByteBuffer[] encodeInputBuffers;
    private ByteBuffer[] encodeOutputBuffers;
    private MediaCodec.BufferInfo decodeBufferInfo;
    private MediaCodec.BufferInfo encodeBufferInfo;
    private FileOutputStream fos;
    private BufferedOutputStream bos;
    private FileInputStream fis;
    private BufferedInputStream bis;
    private ArrayList<byte[]> chunkPCMDataContainer;    //PCM 数据块容器
    private OnCompleteListener onCompleteListener;
    private OnProgressListener onProgressListener;
    private long fileTotalSize;
    private long decodeSize;

    private boolean codeOver = false;

    public static AudioCodec newInstance() {
        return new AudioCodec();
    }

    /**
     * 设置编码器类型
     *
     * @param encodeType
     */
    public void setEncodeType(String encodeType) {
        this.encodeType = encodeType;
    }

    public void setIOPath(String srcPath, String dstPath) {
        this.srcPath = srcPath;
        this.dstPath = dstPath;
    }

    /**
     * 初始化 Decode、Encode、输入输出流等一系列操作
     */
    public void prepare() {
        if (TextUtils.isEmpty(encodeType)) {
            throw new IllegalArgumentException("encodeType can't be empty");
        }

        if (TextUtils.isEmpty(srcPath)) {
            throw new IllegalArgumentException("srcPath can't be empty");
        }

        if (TextUtils.isEmpty(dstPath)) {
            throw new IllegalArgumentException("dstPath can't be empty");
        }

        try {
            fos = new FileOutputStream(new File(dstPath));
            bos = new BufferedOutputStream(fos, 200 * 1024);
            File file = new File(srcPath);
            fileTotalSize = file.length();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        chunkPCMDataContainer = new ArrayList<>();
        initMediaDecode();
        if (encodeType.equals(MediaFormat.MIMETYPE_AUDIO_AAC)) {
            initAACMediaEncode();   // AAC 编码器
        } else if (encodeType.equals(MediaFormat.MIMETYPE_AUDIO_MPEG)) {
            initMPEGMediaEncode();  // mp3 编码器
        }
    }

    /**
     * 初始化解码器
     */
    private void initMediaDecode() {
        try {
            mediaExtractor = new MediaExtractor();//此类可分离视频文件的音轨和视频轨道
            mediaExtractor.setDataSource(srcPath);//媒体文件的位置
            for (int i = 0; i < mediaExtractor.getTrackCount(); i++) {//遍历媒体轨道 此处我们传入的是音频文件，所以也就只有一条轨道
                MediaFormat format = mediaExtractor.getTrackFormat(i);
                String mime = format.getString(MediaFormat.KEY_MIME);
                if (mime.startsWith("audio")) {//获取音频轨道
//                    format.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, 200 * 1024);
                    mediaExtractor.selectTrack(i);//选择此音频轨道
                    mediaDecode = MediaCodec.createDecoderByType(mime);//创建Decode解码器
                    mediaDecode.configure(format, null, null, 0);
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (mediaDecode == null) {
            Log.e(TAG, "create mediaDecode failed");
            return;
        }
        mediaDecode.start();//启动MediaCodec ，等待传入数据
        decodeInputBuffers = mediaDecode.getInputBuffers();//MediaCodec在此ByteBuffer[]中获取输入数据
        decodeOutputBuffers = mediaDecode.getOutputBuffers();//MediaCodec将解码后的数据放到此ByteBuffer[]中 我们可以直接在这里面得到PCM数据
        decodeBufferInfo = new MediaCodec.BufferInfo();//用于描述解码得到的byte[]数据的相关信息
        Log.i(TAG, "buffers:" + decodeInputBuffers.length);

    }

    /**
     * 初始化 AAC 编码器
     */
    private void initAACMediaEncode() {
        try {
            MediaFormat encodeFormat = MediaFormat.createAudioFormat(encodeType, 44100, 2);//参数对应-> mime type、采样率、声道数
            encodeFormat.setInteger(MediaFormat.KEY_BIT_RATE, 96000);//比特率
            encodeFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC);
            encodeFormat.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, 100 * 1024);
            mediaEncode = MediaCodec.createEncoderByType(encodeType);
            mediaEncode.configure(encodeFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (mediaEncode == null) {
            Log.e(TAG, "create mediaEncode failed");
            return;
        }
        mediaEncode.start();
        encodeInputBuffers = mediaEncode.getInputBuffers();
        encodeOutputBuffers = mediaEncode.getOutputBuffers();
        encodeBufferInfo = new MediaCodec.BufferInfo();
    }

    private void initMPEGMediaEncode() {
    }

    public void startAsync() {
        Log.i(TAG, "startAsync: Start");

        new Thread(new DecodeRunnable()).start();
        new Thread(new EncodeRunnable()).start();

    }

    /**
     * 将 PCM 数据存入 {@link #chunkPCMDataContainer}
     *
     * @param pcmChunk
     */
    private void putPCMData(byte[] pcmChunk) {
        synchronized (AudioCodec.class) {   // 需要加锁
            chunkPCMDataContainer.add(pcmChunk);
        }
    }

    private byte[] getPCMData() {
        synchronized (AudioCodec.class) {
            Log.i(TAG, "getPCMData: " + chunkPCMDataContainer.size());
            if (chunkPCMDataContainer.isEmpty()) {
                return null;
            }
            byte[] pcmChunk = chunkPCMDataContainer.get(0);
            chunkPCMDataContainer.remove(pcmChunk);
            return pcmChunk;
        }
    }

    /**
     * 解码音频文件，得到 PCM 数据块
     */
    private void srcAudioFormatToPCM() {
        for (int i = 0; i < decodeInputBuffers.length - 1; i++) {
            int inputIndex = mediaDecode.dequeueInputBuffer(-1);    //获取可用的inputBuffer -1代表一直等待，0表示不等待 建议-1,避免丢帧
            if (inputIndex < 0) {
                codeOver = true;
                return;
            }

            ByteBuffer inputBuffer = decodeInputBuffers[inputIndex];    //拿到 input Buffer
            inputBuffer.clear();    //清空之前传入 inputBuffer 内的数据
            int sampleSize = mediaExtractor.readSampleData(inputBuffer, 0);     // MediaExtractor 读取数据到 inputBuffer
            if (sampleSize < 0) {   // 小于 0 代表所有数据已经读取完毕
                codeOver = true;
            } else {
                mediaDecode.queueInputBuffer(inputIndex, 0, sampleSize, 0, 0);  // 通知 MediaDecode 解码刚刚传入的数据
                mediaExtractor.advance();   //MediaExtractor 移到下一采样点
                decodeSize += sampleSize;
            }
        }

        // 获取解码得到的 byte[] 数据，参数 BufferInfo 上面已经介绍 10000 同样为等待时间，-1 代表 一直等待，0 代表不等待
        int outputIndex = mediaDecode.dequeueOutputBuffer(decodeBufferInfo, 10000);

        ByteBuffer outputBuffer;
        byte[] chunkPCM;
        while (outputIndex >= 0) {  // 每次解码完成的数据不一定能一次吐出，所以用 while 循环，保证解码器吐出所有数据
            outputBuffer = decodeOutputBuffers[outputIndex];    // 拿到用于存放 PCM 数据的 Buffer
            chunkPCM = new byte[decodeBufferInfo.size]; // BufferInfo 内定义了此数据块的大小
            outputBuffer.get(chunkPCM); // 将 Buffer 内的数据取出到字节数组中
            outputBuffer.clear();   // 数据取出后一定记得清空此 Buffer MediaCodec 是循环使用这些 Buffer 的，不清空下次会得到同样的数据。
            putPCMData(chunkPCM);   // 供编码器线程获取数据
            mediaDecode.releaseOutputBuffer(outputIndex, false);    // 此操作一定要做，不然 MediaCodec 用完所有的 Buffer 后，将不能向外输出数据
            outputIndex = mediaDecode.dequeueOutputBuffer(decodeBufferInfo, 10000); //再次获取数据
        }
    }

    /**
     * 编码 PCM 数据，得到 encodeType 格式的音频文件，并保存到 dstPath
     */
    private void dstAudioFormatFromPCM() {
        int inputIndex;
        ByteBuffer inputBuffer;
        int outputIndex;
        ByteBuffer outputBuffer;
        byte[] chunkAudio;
        int outBitSize;
        int outPacketSize;
        byte[] chunkPCM;
        for (int i = 0; i < encodeInputBuffers.length - 1; i++) {
            chunkPCM = getPCMData();    // 获取解码器所在线程输出的数据
            if (chunkPCM == null) {
                break;
            }
            inputIndex = mediaEncode.dequeueInputBuffer(-1);
            inputBuffer = encodeInputBuffers[inputIndex];
            inputBuffer.clear();
            inputBuffer.limit(chunkPCM.length);
            inputBuffer.put(chunkPCM);  // PCM 数据填充给 inputBuffer
            mediaEncode.queueInputBuffer(inputIndex, 0, chunkPCM.length, 0, 0); // 通知编码器 编码
        }
        outputIndex = mediaEncode.dequeueOutputBuffer(encodeBufferInfo, 10000);
        while (outputIndex >= 0) {
            outBitSize = encodeBufferInfo.size;
            outPacketSize = outBitSize + 7; // 7 为 ADTS 头部的大小
            outputBuffer = encodeOutputBuffers[outputIndex];    //拿到输出 Buffer
            outputBuffer.position(encodeBufferInfo.offset);
            outputBuffer.limit(encodeBufferInfo.offset + outBitSize);
            chunkAudio = new byte[outPacketSize];
            addADTStoPacket(chunkAudio, outPacketSize); // 添加 ADTS
            outputBuffer.get(chunkAudio, 7, outBitSize);    // 将编码得到的 AAC 数据，取出到 byte[]，偏移量为 7
            outputBuffer.position(encodeBufferInfo.offset);
            try {
                bos.write(chunkAudio, 0, chunkAudio.length);    // BufferOutputStream 将文本保存到内存卡中 *.aac
            } catch (IOException e) {
                e.printStackTrace();
            }
            mediaEncode.releaseOutputBuffer(outputIndex, false);
            outputIndex = mediaEncode.dequeueOutputBuffer(encodeBufferInfo, 10000);
        }
    }


    /**
     * 添加ADTS头
     *
     * @param packet
     * @param packetLen
     */
    private void addADTStoPacket(byte[] packet, int packetLen) {
        int profile = 2; // AAC LC
        int freqIdx = 4; // 44.1KHz
        int chanCfg = 2; // CPE


        // fill in ADTS data
        packet[0] = (byte) 0xFF;
        packet[1] = (byte) 0xF9;
        packet[2] = (byte) (((profile - 1) << 6) + (freqIdx << 2) + (chanCfg >> 2));
        packet[3] = (byte) (((chanCfg & 3) << 6) + (packetLen >> 11));
        packet[4] = (byte) ((packetLen & 0x7FF) >> 3);
        packet[5] = (byte) (((packetLen & 7) << 5) + 0x1F);
        packet[6] = (byte) 0xFC;
    }

    /**
     * 释放资源
     */
    public void release() {
        try {
            if (bos != null) {
                bos.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    bos = null;
                }
            }
        }

        try {
            if (fos != null) {
                fos.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            fos = null;
        }

        if (mediaEncode != null) {
            mediaEncode.stop();
            mediaEncode.release();
            mediaEncode = null;
        }

        if (mediaDecode != null) {
            mediaDecode.stop();
            mediaDecode.release();
            mediaDecode = null;
        }

        if (mediaExtractor != null) {
            mediaExtractor.release();
            mediaExtractor = null;
        }

        if (onCompleteListener != null) {
            onCompleteListener = null;
        }

        if (onProgressListener != null) {
            onProgressListener = null;
        }
        Log.i(TAG, "release");
    }


    /**
     * 解码线程
     */
    private class DecodeRunnable implements Runnable {

        @Override
        public void run() {
            while (!codeOver) {
                srcAudioFormatToPCM();
            }
        }
    }

    /**
     * 编码线程
     */
    private class EncodeRunnable implements Runnable {

        @Override
        public void run() {
            long t = System.currentTimeMillis();
            while (!codeOver || !chunkPCMDataContainer.isEmpty()) {
                dstAudioFormatFromPCM();
            }
            if (onCompleteListener != null) {
                onCompleteListener.completed();
            }
            Log.i(TAG, "size:" + fileTotalSize + " decodeSize:" + decodeSize + "time:" + (System.currentTimeMillis() - t));
        }
    }


    /**
     * 转码完成回调接口
     */
    public interface OnCompleteListener {
        void completed();
    }

    /**
     * 转码进度监听器
     */
    public interface OnProgressListener {
        void progress();
    }


}
