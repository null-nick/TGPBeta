package org.webrtc;

import android.content.Context;
import android.os.SystemClock;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class FileVideoCapturer implements VideoCapturer {
    private static final String TAG = "FileVideoCapturer";
    private CapturerObserver capturerObserver;
    private final VideoReader videoReader;
    private final Timer timer = new Timer();
    private final TimerTask tickTask = new TimerTask() {
        @Override
        public void run() {
            FileVideoCapturer.this.tick();
        }
    };

    public interface VideoReader {
        void close();

        VideoFrame getNextFrame();
    }

    private static class VideoReaderY4M implements VideoReader {
        private static final int FRAME_DELIMETER_LENGTH = 6;
        private static final String TAG = "VideoReaderY4M";
        private static final String Y4M_FRAME_DELIMETER = "FRAME";
        private final int frameHeight;
        private final int frameWidth;
        private final RandomAccessFile mediaFile;
        private final FileChannel mediaFileChannel;
        private final long videoStart;

        public VideoReaderY4M(String str) {
            RandomAccessFile randomAccessFile = new RandomAccessFile(str, "r");
            this.mediaFile = randomAccessFile;
            this.mediaFileChannel = randomAccessFile.getChannel();
            StringBuilder sb = new StringBuilder();
            while (true) {
                int read = this.mediaFile.read();
                if (read == -1) {
                    throw new RuntimeException("Found end of file before end of header for file: " + str);
                }
                if (read == 10) {
                    this.videoStart = this.mediaFileChannel.position();
                    String str2 = "";
                    int i = 0;
                    int i2 = 0;
                    for (String str3 : sb.toString().split("[ ]")) {
                        char charAt = str3.charAt(0);
                        if (charAt == 'C') {
                            str2 = str3.substring(1);
                        } else if (charAt == 'H') {
                            i2 = Integer.parseInt(str3.substring(1));
                        } else if (charAt == 'W') {
                            i = Integer.parseInt(str3.substring(1));
                        }
                    }
                    Logging.d("VideoReaderY4M", "Color space: " + str2);
                    if (!str2.equals("420") && !str2.equals("420mpeg2")) {
                        throw new IllegalArgumentException("Does not support any other color space than I420 or I420mpeg2");
                    }
                    if (i % 2 == 1 || i2 % 2 == 1) {
                        throw new IllegalArgumentException("Does not support odd width or height");
                    }
                    this.frameWidth = i;
                    this.frameHeight = i2;
                    Logging.d("VideoReaderY4M", "frame dim: (" + i + ", " + i2 + ")");
                    return;
                }
                sb.append((char) read);
            }
        }

        @Override
        public void close() {
            try {
                this.mediaFile.close();
            } catch (IOException e) {
                Logging.e("VideoReaderY4M", "Problem closing file", e);
            }
        }

        @Override
        public VideoFrame getNextFrame() {
            long nanos = TimeUnit.MILLISECONDS.toNanos(SystemClock.elapsedRealtime());
            JavaI420Buffer allocate = JavaI420Buffer.allocate(this.frameWidth, this.frameHeight);
            ByteBuffer dataY = allocate.getDataY();
            ByteBuffer dataU = allocate.getDataU();
            ByteBuffer dataV = allocate.getDataV();
            allocate.getStrideY();
            allocate.getStrideU();
            allocate.getStrideV();
            try {
                int i = FRAME_DELIMETER_LENGTH;
                ByteBuffer allocate2 = ByteBuffer.allocate(i);
                if (this.mediaFileChannel.read(allocate2) < i) {
                    this.mediaFileChannel.position(this.videoStart);
                    if (this.mediaFileChannel.read(allocate2) < i) {
                        throw new RuntimeException("Error looping video");
                    }
                }
                String str = new String(allocate2.array(), Charset.forName("US-ASCII"));
                if (str.equals("FRAME\n")) {
                    this.mediaFileChannel.read(dataY);
                    this.mediaFileChannel.read(dataU);
                    this.mediaFileChannel.read(dataV);
                    return new VideoFrame(allocate, 0, nanos);
                }
                throw new RuntimeException("Frames should be delimited by FRAME plus newline, found delimter was: '" + str + "'");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public FileVideoCapturer(String str) {
        try {
            this.videoReader = new VideoReaderY4M(str);
        } catch (IOException e) {
            Logging.d("FileVideoCapturer", "Could not open video file: " + str);
            throw e;
        }
    }

    @Override
    public void changeCaptureFormat(int i, int i2, int i3) {
    }

    @Override
    public void dispose() {
        this.videoReader.close();
    }

    @Override
    public void initialize(SurfaceTextureHelper surfaceTextureHelper, Context context, CapturerObserver capturerObserver) {
        this.capturerObserver = capturerObserver;
    }

    @Override
    public boolean isScreencast() {
        return false;
    }

    @Override
    public void startCapture(int i, int i2, int i3) {
        this.timer.schedule(this.tickTask, 0L, 1000 / i3);
    }

    @Override
    public void stopCapture() {
        this.timer.cancel();
    }

    public void tick() {
        VideoFrame nextFrame = this.videoReader.getNextFrame();
        this.capturerObserver.onFrameCaptured(nextFrame);
        nextFrame.release();
    }
}
