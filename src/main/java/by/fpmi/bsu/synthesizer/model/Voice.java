package by.fpmi.bsu.synthesizer.model;

import static by.fpmi.bsu.synthesizer.newimpl.Constants.SAMPLE_RATE;
import static by.fpmi.bsu.synthesizer.newimpl.SoundUtil.generateWaveform;

import by.fpmi.bsu.synthesizer.newimpl.Waveform;

public class Voice {
    private double frequency;
    private double phase;
    private double detune;

    public Voice(double baseFrequency, double detuneAmount, double phase) {
        this.detune = detuneAmount;
        this.frequency = baseFrequency * (1.0 + detune);
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
