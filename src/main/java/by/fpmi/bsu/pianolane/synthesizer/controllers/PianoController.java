package by.fpmi.bsu.pianolane.synthesizer.controllers;

import by.fpmi.bsu.pianolane.synthesizer.constants.NoteFrequencies;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import org.springframework.stereotype.Component;

import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;

@Component
public class PianoController {

    @FXML
    public HBox pianoPane;

    @FXML
    public void initialize() {
        setupButton("C3", NoteFrequencies.C3_FREQUENCY);
        setupButton("C3_SHARP", NoteFrequencies.C3_SHARP_FREQUENCY);
        setupButton("D3", NoteFrequencies.D3_FREQUENCY);
        setupButton("D3_SHARP", NoteFrequencies.D3_SHARP_FREQUENCY);
        setupButton("E3", NoteFrequencies.E3_FREQUENCY);
        setupButton("F3", NoteFrequencies.F3_FREQUENCY);
        setupButton("F3_SHARP", NoteFrequencies.F3_SHARP_FREQUENCY);
        setupButton("G3", NoteFrequencies.G3_FREQUENCY);
        setupButton("G3_SHARP", NoteFrequencies.G3_SHARP_FREQUENCY);
        setupButton("A3", NoteFrequencies.A3_FREQUENCY);
        setupButton("A3_SHARP", NoteFrequencies.A3_SHARP_FREQUENCY);
        setupButton("B3", NoteFrequencies.B3_FREQUENCY);

        setupButton("C4", NoteFrequencies.C4_FREQUENCY);
        setupButton("C4_SHARP", NoteFrequencies.C4_SHARP_FREQUENCY);
        setupButton("D4", NoteFrequencies.D4_FREQUENCY);
        setupButton("D4_SHARP", NoteFrequencies.D4_SHARP_FREQUENCY);
        setupButton("E4", NoteFrequencies.E4_FREQUENCY);
        setupButton("F4", NoteFrequencies.F4_FREQUENCY);
        setupButton("F4_SHARP", NoteFrequencies.F4_SHARP_FREQUENCY);
        setupButton("G4", NoteFrequencies.G4_FREQUENCY);
        setupButton("G4_SHARP", NoteFrequencies.G4_SHARP_FREQUENCY);
        setupButton("A4", NoteFrequencies.A4_FREQUENCY);
        setupButton("A4_SHARP", NoteFrequencies.A4_SHARP_FREQUENCY);
        setupButton("B4", NoteFrequencies.B4_FREQUENCY);

        setupButton("C5", NoteFrequencies.C5_FREQUENCY);
        setupButton("C5_SHARP", NoteFrequencies.C5_SHARP_FREQUENCY);
        setupButton("D5", NoteFrequencies.D5_FREQUENCY);
        setupButton("D5_SHARP", NoteFrequencies.D5_SHARP_FREQUENCY);
        setupButton("E5", NoteFrequencies.E5_FREQUENCY);
        setupButton("F5", NoteFrequencies.F5_FREQUENCY);
        setupButton("F5_SHARP", NoteFrequencies.F5_SHARP_FREQUENCY);
        setupButton("G5", NoteFrequencies.G5_FREQUENCY);
        setupButton("G5_SHARP", NoteFrequencies.G5_SHARP_FREQUENCY);
        setupButton("A5", NoteFrequencies.A5_FREQUENCY);
        setupButton("A5_SHARP", NoteFrequencies.A5_SHARP_FREQUENCY);
        setupButton("B5", NoteFrequencies.B5_FREQUENCY);
    }

    private void setupButton(String key, double frequency) {
        Button button = (Button) pianoPane.lookup("#" + key);
        if (button != null) {
            button.setOnMousePressed(event -> {
                try {
                    playSoundForKey(key, frequency);
                } catch (LineUnavailableException | IOException e) {
                    e.printStackTrace();
                }
            });
            button.setOnMouseReleased(event -> stopSoundForKey(key));
        }
    }

    private void playSoundForKey(String key, double frequency) throws LineUnavailableException, IOException {
//        SoundGenerator soundGenerator;
//        if (localSoundGenerators.containsKey(key)) {
//            soundGenerator = localSoundGenerators.get(key);
//        } else {
//            soundGenerator = new SoundGenerator(frequency);
//            soundGenerator.addMagnitudeListener(VisualizerController.getInstance());
//            localSoundGenerators.put(key, soundGenerator);
//        }
//
//        new Thread(() -> {
//            try {
//                soundGenerator.playSound(); // Воспроизведение в отдельном потоке
//            } catch (LineUnavailableException | IOException e) {
//                e.printStackTrace();
//            }
//        }).start();
    }

    private void stopSoundForKey(String key) {
//        SoundGenerator soundGenerator = localSoundGenerators.get(key);
//        if (soundGenerator != null) {
//            soundGenerator.stopSound();
//        }
    }
}
