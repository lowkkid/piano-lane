package by.fpmi.bsu.synthesizer.newimpl;

import be.tarsos.dsp.io.TarsosDSPAudioInputStream;

import be.tarsos.dsp.io.TarsosDSPAudioFormat;

public class SilentAudioInputStream implements TarsosDSPAudioInputStream {
    private final TarsosDSPAudioFormat format;
    private final byte[] silentBuffer;

    public SilentAudioInputStream(TarsosDSPAudioFormat format, int bufferSize) {
        this.format = format;
        this.silentBuffer = new byte[bufferSize];
    }

    @Override
    public int read(byte[] buffer, int offset, int length) {
        int copyLength = Math.min(length, silentBuffer.length);
        System.arraycopy(silentBuffer, 0, buffer, offset, copyLength);
        return copyLength;
    }

    @Override
    public TarsosDSPAudioFormat getFormat() {
        return format;
    }

    @Override
    public long skip(long bytesToSkip) {
        return bytesToSkip;
    }

    @Override
    public void close() {}

    @Override
    public long getFrameLength() {
        return Long.MAX_VALUE;
    }
}
