package by.fpmi.bsu.pianolane.common.util.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Waveform {
    SINE("Sine"),
    SQUARE("Square"),
    SAW("Saw"),
    TRIANGLE("Triangle"),
    NOISE("Noise");

    private final String displayName;

    public static Waveform fromDisplayName(String displayName) {
        for (Waveform type : Waveform.values()) {
            if (type.displayName.equals(displayName)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown waveform type: " + displayName);
    }
}
