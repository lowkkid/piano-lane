package by.fpmi.bsu.pianolane.controller;

import by.fpmi.bsu.pianolane.MidiPlayer;
import by.fpmi.bsu.pianolane.project.ProjectManager;
import by.fpmi.bsu.pianolane.ui.button.OpenButton;
import by.fpmi.bsu.pianolane.ui.button.SaveButton;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.stage.FileChooser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static by.fpmi.bsu.pianolane.util.GlobalInstances.CURRENT_PIANO_ROLL_CONTROLLER;

@Component
@RequiredArgsConstructor
@Slf4j
public class ToolbarController {

    @FXML
    public SaveButton saveProjectButton;
    public OpenButton loadProjectButton;
    public SaveButton exportToWavButton;
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
    private final ProjectManager projectManager;

    private static final String PROJECT_FILE_EXTENSION = "*.plp";
    private static final String PROJECT_FILES_DESCRIPTION = "Piano Lane Project files";
    private static final FileChooser.ExtensionFilter PROJECT_FILES_FILTER = new FileChooser.ExtensionFilter(
            PROJECT_FILES_DESCRIPTION + " (" + PROJECT_FILE_EXTENSION + ")",
            PROJECT_FILE_EXTENSION
    );

    @FXML
    public void initialize() {
        initializePlayAndStopButton();
        initializeBpmSpinner();
        menuButton.setOnAction(e -> mainController.toggleChannelRack());
        saveProjectButton.setOnSave(projectManager::saveProject);
        saveProjectButton.addExtensionFilter(PROJECT_FILES_FILTER);
        loadProjectButton.setOnOpen(projectManager::loadProject);
        loadProjectButton.addExtensionFilter(PROJECT_FILES_FILTER);
    }

    private void initializePlayAndStopButton() {
        playButton.setOnAction(event -> playNotes());
        stopButton.setOnAction(event -> stopNotes());
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

        bpmSpinner.valueProperty().addListener((obs, oldVal, newVal) -> midiPlayer.setBpm(newVal.floatValue()));
    }

    private void stopNotes() {
        midiPlayer.stop();
        CURRENT_PIANO_ROLL_CONTROLLER.removePlayhead();
    }

    private void playNotes() {
        midiPlayer.play();
        CURRENT_PIANO_ROLL_CONTROLLER.startPlayhead();
    }
}
