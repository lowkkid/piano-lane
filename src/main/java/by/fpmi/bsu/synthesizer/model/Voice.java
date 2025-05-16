package by.fpmi.bsu.synthesizer.model;

import static by.fpmi.bsu.synthesizer.newimpl.Constants.SAMPLE_RATE;
import static by.fpmi.bsu.synthesizer.newimpl.SoundUtil.generateWaveform;

import by.fpmi.bsu.pianolane.util.enums.Waveform;

public class Voice {
    private final double frequency;
    private double phase;

    public Voice(double baseFrequency, double detuneAmount, double phase) {
        this.frequency = baseFrequency * (1.0 + detuneAmount);
        this.phase = phase;
    }

    public float generateSample(Waveform waveform) {
        float sample = generateWaveform(waveform, phase);
        phase += frequency / SAMPLE_RATE;
        if (phase >= 1.0) {
            phase -= 1.0;
        }
        return sample;
    }
}
