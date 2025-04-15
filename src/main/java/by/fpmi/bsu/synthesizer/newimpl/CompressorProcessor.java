package by.fpmi.bsu.synthesizer.newimpl;

import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;

public class CompressorProcessor implements AudioProcessor {
    private final float threshold;    // Например, 0.8f
    private final float ratio;        // Например, 4.0f для сжатия 4:1
    private final float attackCoeff;  // Чем ниже, тем быстрее компрессор реагирует (например, 0.1f)
    private final float releaseCoeff; // Чем ниже, тем быстрее происходит восстановление (например, 0.05f)
    private float gain = 1.0f;        // Начальный коэффициент усиления

    public CompressorProcessor(float threshold, float ratio, float attackCoeff, float releaseCoeff) {
        this.threshold = threshold;
        this.ratio = ratio;
        this.attackCoeff = attackCoeff;
        this.releaseCoeff = releaseCoeff;
    }

    @Override
    public boolean process(AudioEvent event) {
        float[] buffer = event.getFloatBuffer();

        for (int i = 0; i < buffer.length; i++) {
            float sample = buffer[i];
            float absSample = Math.abs(sample);
            float targetGain = 1.0f;

            if (absSample > threshold) {
                // Расчитываем избыточный уровень превышения порога и сжимаем его
                float excess = absSample - threshold;
                float compressedExcess = excess / ratio;
                float desiredLevel = threshold + compressedExcess;
                targetGain = desiredLevel / absSample;
            }
            // Гладкое изменение коэффициента усиления с использованием атаки и релиза
            if (targetGain < gain) {
                gain = attackCoeff * targetGain + (1 - attackCoeff) * gain;
            } else {
                gain = releaseCoeff * targetGain + (1 - releaseCoeff) * gain;
            }
            buffer[i] = sample * gain;
        }
        return true;
    }

    @Override
    public void processingFinished() {}
}