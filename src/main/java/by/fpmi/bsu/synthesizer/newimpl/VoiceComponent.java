package by.fpmi.bsu.synthesizer.newimpl;

import static by.fpmi.bsu.synthesizer.newimpl.Constants.SAMPLE_RATE;
import static by.fpmi.bsu.synthesizer.newimpl.SoundUtil.generateWaveform;

public class VoiceComponent {
    private double frequency;
    private double phase;
    private double detune;

    public VoiceComponent(double baseFrequency, double detuneAmount) {
        this.detune = detuneAmount;
        this.frequency = baseFrequency * (1.0 + detune);
        this.phase = Math.random();
    }

    public float generateSample(Waveform waveform) {
        float sample = generateWaveform(waveform, phase);
        phase += frequency / SAMPLE_RATE;
        if (phase >= 1.0) phase -= 1.0;
        return sample;
    }
}
