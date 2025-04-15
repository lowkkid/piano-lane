package by.fpmi.bsu.synthesizer.newimpl;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import static by.fpmi.bsu.synthesizer.newimpl.Constants.SAMPLE_RATE;
import static by.fpmi.bsu.synthesizer.newimpl.SoundUtil.generateWaveform;

@Data
@Slf4j
public class Voice {
    private double frequency;
    private Waveform waveform;
    private float velocity;
    private ADSREnvelope envelope;
    private double phase;

    private int unisonCount = 1;
    private double unisonDetune = 0.3;

    private static final float BASE_GAIN = 0.3f;

    private List<VoiceComponent> components = new ArrayList<>();

    public Voice(double frequency, float velocity, Waveform waveform) {
        // Детюн: до ±1% от основной частоты
        this.frequency = frequency;
        this.waveform = waveform;
        this.velocity = velocity;
        this.phase = Math.random(); // random phase spread

        this.envelope = new ADSREnvelope(0.5, 0, 1, 0.1);
        this.envelope.noteOn();
        createUnisonComponents();
    }

    private void createUnisonComponents() {
        components.clear();

        if (unisonCount == 1) {
            // Один голос без расстройки
            components.add(new VoiceComponent(frequency, 0));
            return;
        }

        for (int i = 0; i < unisonCount; i++) {
            // Вычисляем нормированное равномерное распределение в диапазоне (0, 1)
            double normalizedPosition = (i + 0.5) / unisonCount;
            // Отображаем на диапазон (-1, 1)
            double detune = unisonDetune * (2.0 * normalizedPosition - 1.0);
            // Применяем небольшую нелинейность (опционально)
            detune = Math.signum(detune) * Math.pow(Math.abs(detune), 0.8);
            components.add(new VoiceComponent(frequency, detune));
        }
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

            float sum = 0;
            for (VoiceComponent component : components) {
                sum += component.generateSample(waveform);
            }

            // Нормализуем по количеству голосов
            sum /= Math.sqrt(unisonCount);

            float amp = sum * env * velocity * gain * BASE_GAIN;
            buffer[i] += amp;

            phase += frequency / SAMPLE_RATE;
            if (phase >= 1.0) phase -= 1.0;
        }
    }
}
