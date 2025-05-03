package by.fpmi.bsu.pianolane.util;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

public class MathUtil {

    private static final Map<Integer, Double> MIDI_NOTE_FREQUENCIES = new HashMap<>();
    private static final Map<Integer, Float> UNISON_NORMALIZATION_COEFFICIENTS = new HashMap<>();
    private static final float UI_TO_MIDI_NOTE_COEFFICIENTS = 9.6f;

    static {
        IntStream.range(0, 110).forEach(
                i -> MIDI_NOTE_FREQUENCIES.put(i, 440.0 * Math.pow(2, (i - 69) / 12.0)));

        IntStream.range(0, 16).forEach(
                i -> UNISON_NORMALIZATION_COEFFICIENTS.put(i, (float) Math.sqrt(i))
        );
    }

    public static double midiNoteToFreq(int note) {
        return MIDI_NOTE_FREQUENCIES.get(note);
    }

    public static float normalizeAmplitudeWithUnison(float sum, int unison) {
        return sum / UNISON_NORMALIZATION_COEFFICIENTS.get(unison);
    }

    public static int uiToMidiNoteLength(int uiNoteLength) {
        return (int) (UI_TO_MIDI_NOTE_COEFFICIENTS * uiNoteLength);
    }

    public static int uiToMidiNoteLength(double uiNoteLength) {
        return (int) (UI_TO_MIDI_NOTE_COEFFICIENTS * uiNoteLength);
    }


}
