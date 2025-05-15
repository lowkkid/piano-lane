package by.fpmi.bsu.pianolane.synthesizer.controllers;



import static by.fpmi.bsu.pianolane.synthesizer.SettingsContainer.getSynthSettings;

import by.fpmi.bsu.pianolane.mainwindow.MainController;
import by.fpmi.bsu.pianolane.common.util.SpringFxmlLoader;
import by.fpmi.bsu.pianolane.synthesizer.settings.SynthSettings;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class SynthesizerController {

    public HBox mainContainer;
    public SpringFxmlLoader springFxmlLoader = new SpringFxmlLoader();
    @FXML
    private Button closeButton;

    private MainController mainController;
    private final int channelId;

    @Autowired
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public SynthesizerController(int channelId) {
        this.channelId = channelId;
    }

    public void initialize() {
        SynthSettings settings = getSynthSettings(channelId);
        Node oscillatorA = springFxmlLoader.loadOscillator(
                "OSC A", settings.getOscillatorASettings().isEnabled(), settings.getOscillatorASettings());
        Region firstSpacer = new Region();
        HBox.setHgrow(firstSpacer, Priority.ALWAYS);
        Node oscillatorB = springFxmlLoader.loadOscillator(
                "OSC B", settings.getOscillatorBSettings().isEnabled(), settings.getOscillatorBSettings());
        Region secondSpacer = new Region();
        HBox.setHgrow(secondSpacer, Priority.ALWAYS);
        Node filters = springFxmlLoader.loadFilter(settings.getFilterSettings());
        mainContainer.getChildren().addAll(oscillatorA, firstSpacer, oscillatorB, secondSpacer, filters);
        closeButton.setOnMouseClicked(e -> mainController.closeSynthesizer());
    }

}
