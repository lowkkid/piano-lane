package by.fpmi.bsu.synthesizer.models;
public enum WaveformType {
    SINE("Синусоида"),
    SQUARE("Квадратная"),
    SAWTOOTH("Пилообразная"),
    TRIANGLE("Треугольная");

    private final String displayName;

    WaveformType(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
