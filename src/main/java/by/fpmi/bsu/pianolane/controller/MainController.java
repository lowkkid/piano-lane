package by.fpmi.bsu.pianolane.controller;

import by.fpmi.bsu.pianolane.MidiPlayer;
import by.fpmi.bsu.pianolane.util.SpringFxmlLoader;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.StackPane;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static by.fpmi.bsu.pianolane.util.constants.FxmlPaths.CHANNEL_RACK_FXML;
import static by.fpmi.bsu.pianolane.util.constants.FxmlPaths.PIANO_ROLL_FXML;
import static by.fpmi.bsu.pianolane.util.constants.FxmlPaths.SYNTHESIZER_FXML;

@Component
@Slf4j
public class MainController {


    @FXML
    private StackPane mainContent;

    private Node channelRackView;
    private Node pianoRollView;
    private Node synthesizerView;

    @FXML
    public void initialize() {

    }

    public void toggleChannelRack() {
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
        SpringFxmlLoader springFxmlLoader = new SpringFxmlLoader();
        channelRackView = springFxmlLoader.load(CHANNEL_RACK_FXML);
        mainContent.getChildren().add(channelRackView);
    }

    protected void closePianoRoll() {
        mainContent.getChildren().remove(pianoRollView);
        pianoRollView = null;
    }

    protected void openPianoRoll(int channelId) {
        log.info("Opening piano roll for channel {}", channelId);
        SpringFxmlLoader springFxmlLoader = new SpringFxmlLoader();
        pianoRollView = springFxmlLoader.load(PIANO_ROLL_FXML, channelId);
        mainContent.getChildren().add(pianoRollView);

    }

    protected void openSynthesizer() {
        SpringFxmlLoader springFxmlLoader = new SpringFxmlLoader();
        synthesizerView = springFxmlLoader.load(SYNTHESIZER_FXML);
        mainContent.getChildren().add(synthesizerView);
    }

    public void closeSynthesizer() {
        mainContent.getChildren().remove(synthesizerView);
        synthesizerView = null;
    }
}
