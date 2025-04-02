package by.fpmi.bsu.synthesizer.controllers;

import by.fpmi.bsu.synthesizer.SoundGenerator;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import org.springframework.stereotype.Component;

import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static by.fpmi.bsu.synthesizer.constants.NoteFrequencies.*;

@Component
public class PianoController {
    private final Map<String, SoundGenerator> localSoundGenerators = new HashMap<>();

    @FXML
    public HBox pianoPane;

    @FXML
    public void initialize() {
        setupButton("C3", C3_FREQUENCY);
        setupButton("C3_SHARP", C3_SHARP_FREQUENCY);
        setupButton("D3", D3_FREQUENCY);
        setupButton("D3_SHARP", D3_SHARP_FREQUENCY);
        setupButton("E3", E3_FREQUENCY);
        setupButton("F3", F3_FREQUENCY);
        setupButton("F3_SHARP", F3_SHARP_FREQUENCY);
        setupButton("G3", G3_FREQUENCY);
        setupButton("G3_SHARP", G3_SHARP_FREQUENCY);
        setupButton("A3", A3_FREQUENCY);
        setupButton("A3_SHARP", A3_SHARP_FREQUENCY);
        setupButton("B3", B3_FREQUENCY);

        setupButton("C4", C4_FREQUENCY);
        setupButton("C4_SHARP", C4_SHARP_FREQUENCY);
        setupButton("D4", D4_FREQUENCY);
        setupButton("D4_SHARP", D4_SHARP_FREQUENCY);
        setupButton("E4", E4_FREQUENCY);
        setupButton("F4", F4_FREQUENCY);
        setupButton("F4_SHARP", F4_SHARP_FREQUENCY);
        setupButton("G4", G4_FREQUENCY);
        setupButton("G4_SHARP", G4_SHARP_FREQUENCY);
        setupButton("A4", A4_FREQUENCY);
        setupButton("A4_SHARP", A4_SHARP_FREQUENCY);
        setupButton("B4", B4_FREQUENCY);

        setupButton("C5", C5_FREQUENCY);
        setupButton("C5_SHARP", C5_SHARP_FREQUENCY);
        setupButton("D5", D5_FREQUENCY);
        setupButton("D5_SHARP", D5_SHARP_FREQUENCY);
        setupButton("E5", E5_FREQUENCY);
        setupButton("F5", F5_FREQUENCY);
        setupButton("F5_SHARP", F5_SHARP_FREQUENCY);
        setupButton("G5", G5_FREQUENCY);
        setupButton("G5_SHARP", G5_SHARP_FREQUENCY);
        setupButton("A5", A5_FREQUENCY);
        setupButton("A5_SHARP", A5_SHARP_FREQUENCY);
        setupButton("B5", B5_FREQUENCY);
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
        SoundGenerator soundGenerator;
        if (localSoundGenerators.containsKey(key)) {
            soundGenerator = localSoundGenerators.get(key);
        } else {
            soundGenerator = new SoundGenerator(frequency);
            soundGenerator.addMagnitudeListener(VisualizerController.getInstance());
            localSoundGenerators.put(key, soundGenerator);
        }

        new Thread(() -> {
            try {
                soundGenerator.playSound(); // Воспроизведение в отдельном потоке
            } catch (LineUnavailableException | IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void stopSoundForKey(String key) {
        SoundGenerator soundGenerator = localSoundGenerators.get(key);
        if (soundGenerator != null) {
            soundGenerator.stopSound();
        }
    }
}
