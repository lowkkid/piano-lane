package by.fpmi.bsu.pianolane.common;

import javafx.scene.paint.Color;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Constants {

    public static final String[] NOTES = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};

    public static final int SAMPLE_RATE = 44100;
    public static final float SAMPLE_RATE_FLOAT = 44100.0f;



    public static final class MidiConstants {

        public static final int STARTING_MIDI_NOTE = 21;
        public static final int ENDING_MIDI_NOTE = 108;
        public static final int NUM_KEYS = ENDING_MIDI_NOTE - STARTING_MIDI_NOTE + 1;        // 5 octaves (60 keys)

        public static final int RESOLUTION = 480;
        public static final int DEFAULT_VELOCITY_VALUE = 75;
        public static final double VELOCITY_IN_PERCENTS = (double) DEFAULT_VELOCITY_VALUE / 100;
        public static final int VOLUME_CONTROLLER = 7;
        public static final int PAN_CONTROLLER = 10;

    }

    public static final class UiConstants {
        public static final Color NOTE_AND_VELOCITY_COLOR = Color.web("#347deb");
    }
}
