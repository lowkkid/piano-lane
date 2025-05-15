package by.fpmi.bsu.pianolane.common;

import javafx.scene.paint.Color;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Constants {


    public static final int SAMPLE_RATE = 44100;
    public static final float SAMPLE_RATE_FLOAT = 44100.0f;



    public static final class MidiConstants {

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
