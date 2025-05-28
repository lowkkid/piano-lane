package by.fpmi.bsu.pianolane.pianoroll.components;

import static by.fpmi.bsu.pianolane.common.Constants.MidiConstants.DEFAULT_VELOCITY_VALUE;
import static by.fpmi.bsu.pianolane.common.Constants.MidiConstants.ENDING_MIDI_NOTE;
import static by.fpmi.bsu.pianolane.common.Constants.MidiConstants.NUM_KEYS;
import static by.fpmi.bsu.pianolane.common.Constants.MidiConstants.STARTING_MIDI_NOTE;
import static by.fpmi.bsu.pianolane.common.Constants.NOTES;
import static by.fpmi.bsu.pianolane.common.util.GlobalInstances.MIDI_CHANNELS;
import static by.fpmi.bsu.pianolane.pianoroll.PianoRollController.KEY_HEIGHT;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import lombok.Getter;

public class Keyboard extends Pane {

    private static final double KEY_WIDTH = 120.0;
    private static final double FONT_SIZE = 12.0;
    private static final double TEXT_X_OFFSET = 5.0;
    private static final double TEXT_Y_OFFSET = 5.0;

    private final List<KeyboardKey> keys = new ArrayList<>();


    public Keyboard() {
        loadFxml();
        drawKeyboard();
    }

    public void setChannelId(int channelId) {
        var midiChannel = MIDI_CHANNELS[channelId];
        keys.forEach(key -> {
            key.setOnMousePressed(e -> {
                midiChannel.noteOn(key.getMidiNote(),DEFAULT_VELOCITY_VALUE);
                e.consume();
            });

            key.setOnMouseReleased(e -> {
                midiChannel.noteOff(key.getMidiNote());
                e.consume();
            });
        });
    }

    private void loadFxml() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/pianoroll/fxml/keyboard.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException("Failed to load keyboard FXML", exception);
        }
    }

    /**
     * Draws the piano keyboard from bottom to top.
     * The keyboard consists of white and black keys with note labels.
     * The lowest note (with index 0) is drawn at the bottom of the keyboard.
     * Each key is drawn with its corresponding note name and octave number.
     */
    private void drawKeyboard() {
        int keyIndex = 0;

        for (int midiNote = STARTING_MIDI_NOTE; midiNote <= ENDING_MIDI_NOTE; midiNote++) {
            int noteIndex = midiNote % 12;
            int octaveNumber = midiNote / 12 - 1;
            boolean isBlackKey = NOTES[noteIndex].contains("#");

            double yPosition = (NUM_KEYS - 1 - keyIndex++) * KEY_HEIGHT;

            var key = createKey(yPosition, isBlackKey, midiNote);
            keys.add(key);
            var noteLabel = createNoteLabel(yPosition, noteIndex, octaveNumber, isBlackKey);

            this.getChildren().addAll(key, noteLabel);
        }
    }

    private KeyboardKey createKey(double yPosition, boolean isBlackKey, int midiNote) {
        var key = new KeyboardKey(0, yPosition, KEY_WIDTH, KEY_HEIGHT, midiNote);
        key.getStyleClass().add(isBlackKey ? "black-key" : "white-key");
        return key;
    }

    private Text createNoteLabel(double yPosition, int noteIndex, int octaveNumber, boolean isBlackKey) {
        Text noteLabel = new Text(
                TEXT_X_OFFSET,
                yPosition + KEY_HEIGHT - TEXT_Y_OFFSET,
                NOTES[noteIndex] + octaveNumber);
        noteLabel.setFont(new Font(FONT_SIZE));
        noteLabel.setFill(isBlackKey ? Color.WHITE : Color.BLACK);
        return noteLabel;
    }

    @Getter
    private static class KeyboardKey extends Rectangle {

        private final int midiNote;

        public KeyboardKey(double x, double y, double width, double height, int midiNote) {
            super(x, y, width, height);
            this.midiNote = midiNote;
        }
    }

}