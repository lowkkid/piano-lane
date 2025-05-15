package by.fpmi.bsu.pianolane.synthesizer.oscillator.model;

import static by.fpmi.bsu.pianolane.common.Constants.SAMPLE_RATE;

import by.fpmi.bsu.pianolane.common.util.enums.Waveform;
import by.fpmi.bsu.pianolane.synthesizer.newimpl.Constants;
import by.fpmi.bsu.pianolane.synthesizer.newimpl.SoundUtil;

public class Voice {
    private final double frequency;
    private double phase;

    public Voice(double baseFrequency, double detuneAmount, double phase) {
        this.frequency = baseFrequency * (1.0 + detuneAmount);
        this.phase = phase;
    }

    public float generateSample(Waveform waveform) {
        float sample = SoundUtil.generateWaveform(waveform, phase);
        phase += frequency / SAMPLE_RATE;
        if (phase >= 1.0) {
            phase -= 1.0;
        }
        return sample;
    }
}
