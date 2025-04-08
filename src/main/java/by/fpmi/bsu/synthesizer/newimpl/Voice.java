package by.fpmi.bsu.synthesizer.newimpl;

import lombok.Data;
import lombok.Getter;

import java.util.Map;

import static by.fpmi.bsu.synthesizer.newimpl.Constants.SAMPLE_RATE;

@Data
public class Voice {
    private final double frequency;
    private final Waveform waveform;
    private final float velocity;
    private final ADSREnvelope envelope;
    private double phase;
    private final double sampleRate;

    private static final float BASE_GAIN = 0.3f;

    public Voice(double frequency, float velocity, Waveform waveform, double sampleRate) {
        // Детюн: до ±1% от основной частоты
        this.frequency = frequency * (1.0 + (Math.random() * 0.02 - 0.01)); // ±1%
        this.waveform = waveform;
        this.velocity = velocity;
        this.sampleRate = sampleRate;
        this.phase = Math.random(); // random phase spread

        this.envelope = new ADSREnvelope(0.5, 0, 1, 2, sampleRate);
        this.envelope.noteOn();
    }

    public void noteOff() {
        envelope.noteOff();
    }

    public boolean isFinished() {
        return envelope.isFinished();
    }

    public void fillBuffer(float[] buffer, float gain) {
        for (int i = 0; i < buffer.length; i++) {
            float env = envelope.nextSample();
            if (envelope.isFinished()) continue;
            float amp = generateSample(phase) * env * velocity * gain * BASE_GAIN;
            buffer[i] += amp;

            phase += frequency / sampleRate;
            if (phase >= 1.0) phase -= 1.0;
        }
    }

    private float generateSample(double phase) {
        return switch (waveform) {
            case SINE -> (float) Math.sin(2 * Math.PI * phase);
            case SQUARE -> phase < 0.5 ? 1.0f : -1.0f;
            case SAW -> (float) (2.0 * (phase - 0.5));
            case TRIANGLE -> (float) (1.0 - 4.0 * Math.abs(phase - 0.5));
            case NOISE -> (float) (Math.random() * 2.0 - 1.0);
        };
    }
}
