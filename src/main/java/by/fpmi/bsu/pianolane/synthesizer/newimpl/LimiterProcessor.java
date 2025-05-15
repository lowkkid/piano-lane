package by.fpmi.bsu.pianolane.synthesizer.newimpl;


import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;

public class LimiterProcessor implements AudioProcessor {
    private final float threshold;

    public LimiterProcessor(float threshold) {
        this.threshold = threshold;
    }

    @Override
    public boolean process(AudioEvent event) {
        float[] buffer = event.getFloatBuffer();
        for (int i = 0; i < buffer.length; i++) {
            if (buffer[i] > threshold) {
                buffer[i] = threshold;
            } else if (buffer[i] < -threshold) {
                buffer[i] = -threshold;
            }
        }
        return true;
    }

    @Override
    public void processingFinished() {}
}