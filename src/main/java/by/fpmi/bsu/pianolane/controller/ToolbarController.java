package by.fpmi.bsu.pianolane.controller;

import by.fpmi.bsu.pianolane.MidiPlayer;
import by.fpmi.bsu.pianolane.util.LogUtil;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static by.fpmi.bsu.pianolane.util.GlobalInstances.CURRENT_PIANO_ROLL_CONTROLLER;
import static by.fpmi.bsu.pianolane.util.GlobalInstances.SEQUENCE;
import static by.fpmi.bsu.pianolane.util.GlobalInstances.updateSequence;

@Component
@RequiredArgsConstructor
public class ToolbarController {

    public Button saveProjectButton;
    public Button loadProjectButton;
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

    private static final String DEFAULT_FILENAME = "Untitlув";
    private static final String FILE_EXTENSION = "*.mid";
    private static final String FILE_DESCRIPTION = "MIDI Files";



    @FXML
    public void initialize() {
        initializePlayAndStopButton();
        initializeBpmSpinner();
        menuButton.setOnAction(e -> mainController.toggleChannelRack());
        initializeSaveProjectButton();
        saveProjectButton.setOnAction(event -> handleSaveProject());
        loadProjectButton.setOnAction(event -> handleLoadProject());
    }

    private void initializeSaveProjectButton() {
        saveProjectButton.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();

            fileChooser.setInitialFileName("Untitled");

            FileChooser.ExtensionFilter extFilter =
                    new FileChooser.ExtensionFilter("MIDI Files (*.mid)", "*.mid");
            fileChooser.getExtensionFilters().add(extFilter);

            File file = fileChooser.showSaveDialog(saveProjectButton.getScene().getWindow());

            if (file != null) {
                try {
                    saveSequence(file.getAbsolutePath());
                } catch (Exception e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Ошибка");
                    alert.setHeaderText("Error while saving project");
                    alert.setContentText("Failed to save project: " + e.getMessage());
                    alert.showAndWait();
                }
            }
        });
    }

    private void saveSequence(String filePath) {
        int[] fileTypes = MidiSystem.getMidiFileTypes(SEQUENCE);
        try {
            if (fileTypes.length > 0) {
                int fileType = contains(fileTypes, 1) ? 1 : fileTypes[0];
                MidiSystem.write(SEQUENCE, fileType, new File(filePath));
            }
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при создании MIDI-последовательности", e);
        }
    }

    private boolean contains(int[] array, int value) {
        for (int item : array) {
            if (item == value) return true;
        }
        return false;
    }

    private void handleSaveProject() {
        File file = showFileChooser(FileOperation.SAVE);
        if (file != null) {
            try {
                saveToFile(file.getAbsolutePath());
                showSuccessMessage("Файл успешно сохранен");
            } catch (Exception e) {
                showErrorMessage("Ошибка при сохранении файла", e.getMessage());
            }
        }
    }

    private void handleLoadProject() {
        File file = showFileChooser(FileOperation.LOAD);
        if (file != null) {
            try {
                loadFromFile(file.getAbsolutePath());
                showSuccessMessage("Файл успешно загружен");
            } catch (Exception e) {
                showErrorMessage("Ошибка при загрузке файла", e.getMessage());
            }
        }
    }

    private enum FileOperation {
        SAVE, LOAD
    }

    private File showFileChooser(FileOperation operation) {
        FileChooser fileChooser = new FileChooser();

        // Настройка фильтра расширений
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                FILE_DESCRIPTION + " (" + FILE_EXTENSION + ")",
                FILE_EXTENSION
        );
        fileChooser.getExtensionFilters().add(extFilter);

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All files", "*.*")
        );

        // Настройка начальной директории (опционально)
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        Window window = saveProjectButton.getScene().getWindow();

        if (operation == FileOperation.SAVE) {
            fileChooser.setInitialFileName(DEFAULT_FILENAME + ".mid");
            return fileChooser.showSaveDialog(window);
        } else {
            return fileChooser.showOpenDialog(window);
        }
    }

    private void showSuccessMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Успех");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showErrorMessage(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void saveToFile(String absolutePath) {
        saveSequence(absolutePath);
        System.out.println("Сохранение в файл: " + absolutePath);
    }

    private void loadFromFile(String absolutePath) throws Exception {
        Sequence sequence = MidiSystem.getSequence(new File(absolutePath));
        Arrays.stream(sequence.getTracks()).forEach(LogUtil::logAllTrackEvents);
        updateSequence(sequence);
        System.out.println("Загрузка из файла: " + absolutePath);
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
