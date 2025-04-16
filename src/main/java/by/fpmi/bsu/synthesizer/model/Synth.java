package by.fpmi.bsu.synthesizer.model;

import by.fpmi.bsu.synthesizer.newimpl.Waveform;
import by.fpmi.bsu.synthesizer.settings.SynthSettings;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import static by.fpmi.bsu.synthesizer.newimpl.Constants.SAMPLE_RATE;

@Data
@Slf4j
public class Synth {

    private Oscillator oscillatorA;
    private Oscillator oscillatorB;

    private float velocity;

    private static final float BASE_GAIN = 0.3f;

    public Synth(SynthSettings settings, double frequency, float velocity) {
        this.velocity = velocity;
        oscillatorA = new Oscillator(settings.getOscillatorASettings(), frequency);
        oscillatorB = new Oscillator(settings.getOscillatorBSettings(), frequency);
    }

    public void noteOff() {
        oscillatorA.noteOff();
        oscillatorB.noteOff();
    }

    public boolean isFinished() {
        return oscillatorA.isFinished() && oscillatorB.isFinished();
    }

    public void fillBuffer(float[] buffer) {
        for (int i = 0; i < buffer.length; i++) {
            float amp = oscillatorA.getNextSample() + oscillatorB.getNextSample();
            float sample = amp * velocity * BASE_GAIN;
            buffer[i] += sample;
        }
    }
}
