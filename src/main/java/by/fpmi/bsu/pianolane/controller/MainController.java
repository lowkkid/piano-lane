package by.fpmi.bsu.pianolane.controller;

import by.fpmi.bsu.pianolane.MidiPlayer;
import by.fpmi.bsu.pianolane.util.SpringFxmlLoader;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sound.midi.Sequencer;
import java.io.IOException;

import static by.fpmi.bsu.pianolane.util.GlobalInstances.SEQUENCER;

@Component
public class MainController {

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
            channelRackView = springFxmlLoader.load("channel-rack.fxml");
            mainContent.getChildren().add(channelRackView);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    protected void closePianoRoll() {
        mainContent.getChildren().remove(pianoRollView);
        pianoRollView = null;
    }

    protected void openPianoRoll() {
        try {
            SpringFxmlLoader springFxmlLoader = new SpringFxmlLoader();
            pianoRollView = springFxmlLoader.load("piano-roll.fxml");
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
}
