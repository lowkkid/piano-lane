package by.fpmi.bsu.pianolane.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sound.midi.Instrument;
import javax.sound.midi.MidiUnavailableException;

import static by.fpmi.bsu.pianolane.util.GlobalInstances.SYNTHESIZER;

@Component
@Slf4j
public class SampleBrowserController {

    @FXML
    private TreeView<Instrument> sampleTree;

    private ChannelRackController channelRackController;

    public void initialize() {
        TreeItem<Instrument> rootItem = new TreeItem<>(null);
        rootItem.setExpanded(true);

        try {
            SYNTHESIZER.open();
            for (Instrument instrument : SYNTHESIZER.getAvailableInstruments()) {
                TreeItem<Instrument> instrumentItem = new TreeItem<>(instrument);
                rootItem.getChildren().add(instrumentItem);
            }
            SYNTHESIZER.close();
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
        }

        sampleTree.setRoot(rootItem);

        sampleTree.setCellFactory(tv -> new TreeCell<>() {
            @Override
            protected void updateItem(Instrument item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName());
                }
            }
        });
        setDoubleClickOnTreeItemListener();
    }

    @Autowired
    public void setChannelRackController(ChannelRackController channelRackController) {
        this.channelRackController = channelRackController;
    }

    private void setDoubleClickOnTreeItemListener() {
        sampleTree.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                TreeItem<Instrument> selectedItem = sampleTree.getSelectionModel().getSelectedItem();
                if (selectedItem != null && selectedItem.getValue() != null) {
                    Instrument chosenInstrument = selectedItem.getValue();
                    channelRackController.addInstrument(chosenInstrument);
                }
            }
        });
    }
}
