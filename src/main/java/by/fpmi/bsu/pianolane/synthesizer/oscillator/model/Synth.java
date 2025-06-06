package by.fpmi.bsu.pianolane.synthesizer.oscillator.model;

import by.fpmi.bsu.pianolane.synthesizer.settings.SynthSettings;
import java.util.Arrays;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class Synth {

    private Oscillator oscillatorA;
    private Oscillator oscillatorB;
    private SynthSettings synthSettings;

    private float velocity;

    private static final float BASE_GAIN = 0.3f;

    public Synth(SynthSettings settings, double frequency, float velocity) {
        this.velocity = velocity;
        this.synthSettings = settings;
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
        if (!synthSettings.isEnabled()) {
            Arrays.fill(buffer, 0.0f);
            return;
        }

        for (int i = 0; i < buffer.length; i++) {
            float amp = oscillatorA.getNextSample() + oscillatorB.getNextSample();
            float sample = amp * velocity * BASE_GAIN;
            buffer[i] += sample;
        }
    }
}
