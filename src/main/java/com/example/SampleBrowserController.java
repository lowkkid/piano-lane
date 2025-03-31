package com.example;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

import javax.sound.midi.Instrument;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;
import java.net.URL;
import java.util.ResourceBundle;

public class SampleBrowserController implements Initializable {

    @FXML
    private TreeView<String> sampleTree;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        TreeItem<String> rootItem = new TreeItem<>("System Sounds");
        rootItem.setExpanded(true);

        try {
            Synthesizer synthesizer = MidiSystem.getSynthesizer();
            synthesizer.open();
            for (Instrument instrument : synthesizer.getAvailableInstruments()) {
                TreeItem<String> instrumentItem = new TreeItem<>(instrument.toString());
                rootItem.getChildren().add(instrumentItem);
            }
            synthesizer.close();
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
        }

        sampleTree.setRoot(rootItem);
    }
}
