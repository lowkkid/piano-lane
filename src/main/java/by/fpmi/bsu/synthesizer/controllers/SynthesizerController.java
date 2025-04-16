package by.fpmi.bsu.synthesizer.controllers;



import static by.fpmi.bsu.synthesizer.util.SynthesizerUtilCollections.getSynthSettings;

import by.fpmi.bsu.pianolane.controller.MainController;
import by.fpmi.bsu.pianolane.util.SpringFxmlLoader;
import by.fpmi.bsu.synthesizer.settings.SynthSettings;
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
        Node oscillatorA = springFxmlLoader.loadOscillator("OSC A", false, settings.getOscillatorASettings());
        Region firstSpacer = new Region();
        HBox.setHgrow(firstSpacer, Priority.ALWAYS);
        Node oscillatorB = springFxmlLoader.loadOscillator("OSC B", true, settings.getOscillatorBSettings());
        Region secondSpacer = new Region();
        HBox.setHgrow(secondSpacer, Priority.ALWAYS);
        Node filters = springFxmlLoader.loadFilter();
        mainContainer.getChildren().addAll(oscillatorA, firstSpacer, oscillatorB, secondSpacer, filters);
        closeButton.setOnMouseClicked(e -> mainController.closeSynthesizer());
    }

}
