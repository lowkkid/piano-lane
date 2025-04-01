package by.fpmi.bsu.pianolane.controller;

import by.fpmi.bsu.pianolane.MidiPlayer;
import by.fpmi.bsu.pianolane.util.SpringFxmlLoader;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.StackPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static by.fpmi.bsu.pianolane.util.constants.FxmlPaths.CHANNEL_RACK_FXML;
import static by.fpmi.bsu.pianolane.util.constants.FxmlPaths.PIANO_ROLL_FXML;

@Component
public class MainController {

    private static final Logger log = LoggerFactory.getLogger(MainController.class);
    @FXML
    private Button menuButton;

    @FXML
    private StackPane mainContent;

    @FXML
    private Button playButton;
    @FXML
    private Button stopButton;
    @FXML
    private Spinner<Double> bpmSpinner;

    private final MidiPlayer midiPlayer = MidiPlayer.getInstance();

    private Node channelRackView;
    private Node pianoRollView;

    @FXML
    public void initialize() {
        menuButton.setOnAction(e -> toggleChannelRack());
        initializePlayAndStopButton();
        initializeBpmSpinner();
    }

    /**
     * Загружает или убирает окно channel rack при нажатии на menuButton.
     */
    private void toggleChannelRack() {
        if (channelRackView == null) {
            openChannelRack();
        } else {
            closeChannelRack();
        }
    }

    protected void closeChannelRack() {
        mainContent.getChildren().remove(channelRackView);
        channelRackView = null;
    }

    protected void openChannelRack() {
        try {
            SpringFxmlLoader springFxmlLoader = new SpringFxmlLoader();
            channelRackView = springFxmlLoader.load(CHANNEL_RACK_FXML);
            mainContent.getChildren().add(channelRackView);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    protected void closePianoRoll() {
        mainContent.getChildren().remove(pianoRollView);
        pianoRollView = null;
    }

    protected void openPianoRoll(int channelId) {
        try {
            log.info("Opening piano roll for channel {}", channelId);
            SpringFxmlLoader springFxmlLoader = new SpringFxmlLoader();
            pianoRollView = springFxmlLoader.load(PIANO_ROLL_FXML, channelId);
            mainContent.getChildren().add(pianoRollView);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void initializePlayAndStopButton() {
        playButton.setOnAction(event -> {
            playNotes();
        });

        stopButton.setOnAction(event -> {
            stopNotes();
        });
    }

    private void stopNotes() {
        midiPlayer.stop();
    }

    private void playNotes() {
        midiPlayer.play();

//        if (playheadTimeline != null) {
//            playheadTimeline.stop();
//        }
//
//        playheadTimeline = new Timeline(new KeyFrame(Duration.millis(10), ev -> {
//            if (sequencer.isRunning()) {
//                long tickPos = sequencer.getTickPosition();
//                double newX = (tickPos / (double) ticksPerColumn) * cellWidth;
//                playheadLine.setX(newX);
//            } else {
//                playheadTimeline.stop();
//                playheadLine.setX(0);
//            }
//        }));
//        playheadTimeline.setCycleCount(Timeline.INDEFINITE);
//        playheadTimeline.play();
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
}
