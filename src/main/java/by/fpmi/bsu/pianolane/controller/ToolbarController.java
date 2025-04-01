package by.fpmi.bsu.pianolane.controller;

import by.fpmi.bsu.pianolane.MidiPlayer;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static by.fpmi.bsu.pianolane.util.GlobalInstances.CURRENT_PIANO_ROLL_CONTROLLER;
import static by.fpmi.bsu.pianolane.util.GlobalInstances.SPRING_CONTEXT;

@Component
@RequiredArgsConstructor
public class ToolbarController {

    @FXML
    private Button playButton;
    @FXML
    private Button stopButton;
    @FXML
    private Spinner<Double> bpmSpinner;
    @FXML
    private Button menuButton;

    private final MidiPlayer midiPlayer;
    private final MainController mainController;



    @FXML
    public void initialize() {
        initializePlayAndStopButton();
        initializeBpmSpinner();
        menuButton.setOnAction(e -> mainController.toggleChannelRack());
    }

    private void initializePlayAndStopButton() {
        playButton.setOnAction(event -> {
            playNotes();
        });

        stopButton.setOnAction(event -> {
            stopNotes();
        });
    }

    private void initializeBpmSpinner() {
        SpinnerValueFactory<Double> valueFactory =
                new SpinnerValueFactory.DoubleSpinnerValueFactory(1.0, 200.0, 120.0, 1.0);
        bpmSpinner.setValueFactory(valueFactory);

        valueFactory.setConverter(new javafx.util.StringConverter<>() {
            @Override
            public String toString(Double value) {
                if (value == null) return "";
                return String.format("%.1f", value);
            }

            @Override
            public Double fromString(String string) {
                try {
                    return Double.valueOf(string);
                } catch (NumberFormatException e) {
                    return 0.0;
                }
            }
        });

        bpmSpinner.valueProperty().addListener((obs, oldVal, newVal) -> {
            midiPlayer.setBpm(newVal.floatValue());
        });
    }

    private void stopNotes() {
        midiPlayer.stop();
    }

    private void playNotes() {
        midiPlayer.play();
        CURRENT_PIANO_ROLL_CONTROLLER.startPlayhead();
    }
}
