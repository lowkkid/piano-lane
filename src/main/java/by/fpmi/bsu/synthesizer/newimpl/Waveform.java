package by.fpmi.bsu.synthesizer.newimpl;

import lombok.Getter;

@Getter
public enum Waveform {
    SINE("Sine"),
    SQUARE("Square"),
    SAW("Saw"),
    TRIANGLE("Triangle"),
    NOISE("Noise");

    private final String displayName;

    Waveform(String displayName) {
        this.displayName = displayName;
    }

    public static Waveform fromDisplayName(String displayName) {
        for (Waveform type : Waveform.values()) {
            if (type.displayName.equals(displayName)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown waveform type: " + displayName);
    }
}
