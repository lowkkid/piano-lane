package by.fpmi.bsu.synthesizer.newimpl;

import by.fpmi.bsu.pianolane.util.enums.Waveform;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SoundUtil {

    public static float generateWaveform(Waveform waveform, double phase) {
        return switch (waveform) {
            case SINE -> (float) Math.sin(2 * Math.PI * phase);
            case SQUARE -> phase < 0.5 ? 1.0f : -1.0f;
            case SAW -> (float) (2.0 * (phase - 0.5));
            case TRIANGLE -> (float) (1.0 - 4.0 * Math.abs(phase - 0.5));
            case NOISE -> (float) (Math.random() * 2.0 - 1.0);
        };
    }
}
