package by.fpmi.bsu.pianolane.synthesizer.oscillator.model;

import static by.fpmi.bsu.pianolane.common.util.MathUtil.normalizeAmplitudeWithUnison;

import by.fpmi.bsu.pianolane.synthesizer.settings.OscillatorSettings;
import java.util.ArrayList;
import java.util.List;

public class Oscillator {

    private final List<Voice> voices;
    private final OscillatorSettings settings;

    private boolean isEnabled;
    private Envelope envelope;
    private double frequency;

    public Oscillator(OscillatorSettings settings, double frequency) {
        this.settings = settings;
        this.frequency = frequency;
        voices = new ArrayList<>(settings.getUnison());
        envelope = new Envelope(settings.getAttack(), settings.getDecay(), settings.getSustain(), settings.getRelease());
        envelope.noteOn();
        isEnabled = settings.isEnabled();
        updateVoices();
    }

    public boolean isFinished() {
        if (isEnabled) {
            return envelope.isFinished();
        }
        return true;
    }

    public void noteOff() {
        envelope.noteOff();
    }

    public float getNextSample() {
        if (envelope.isFinished() || !settings.isEnabled()) return 0;
        float env = envelope.nextSample();

        float sum = 0;
        for (Voice voice : voices) {
            sum += voice.generateSample(settings.getWaveform());
        }
        sum = normalizeAmplitudeWithUnison(sum, settings.getUnison());

        return sum * env * (float) settings.getLevel();
    }

    private void updateVoices() {
        voices.clear();
        int voicesAmount = settings.getUnison();

        if (voicesAmount == 1) {
            voices.add(new Voice(frequency, 0, settings.getPhase()));
            return;
        }

        for (int i = 0; i < voicesAmount; i++) {
            double normalizedPosition = (i + 0.5) / voicesAmount;
            // Отображаем на диапазон (-1, 1)
            double detune = settings.getDetune() * (2.0 * normalizedPosition - 1.0);
            // Применяем небольшую нелинейность (опционально)
            detune = Math.signum(detune) * Math.pow(Math.abs(detune), 0.8);
            voices.add(new Voice(frequency, detune, settings.getPhase()));
        }
    }
}
