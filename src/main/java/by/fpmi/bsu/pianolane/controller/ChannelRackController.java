package by.fpmi.bsu.pianolane.controller;

import static by.fpmi.bsu.pianolane.util.TracksUtil.getInstrumentForTrack;
import static by.fpmi.bsu.pianolane.util.TracksUtil.getTrackId;

import by.fpmi.bsu.pianolane.ui.ChannelRackItem;
import by.fpmi.bsu.pianolane.ui.pianoroll.MidiNoteContainer;
import by.fpmi.bsu.pianolane.model.ChannelCollection;
import java.util.Arrays;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javax.sound.midi.Track;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sound.midi.Instrument;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

@Component
@Slf4j
public class ChannelRackController implements Initializable {

    @FXML
    private VBox instrumentContainer;
    @FXML
    private Button closeButton;
    @FXML
    private Button addButton;

    private MainController mainController;
    private final ChannelCollection channelCollection = ChannelCollection.getInstance();

    private final ContextMenu channelRackItemContextMenu = new ContextMenu();
    private final MenuItem deleteItem = new MenuItem("Delete");

    private final ContextMenu synthesizersContextMenu = new ContextMenu();
    private final MenuItem customSynthesizerItem = new MenuItem("Custom Synthesizer");

    private final List<ChannelRackItem> rows = new ArrayList<>();
    private final MidiNoteContainer midiNoteContainer = MidiNoteContainer.getInstance();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        closeButton.setOnAction(event -> mainController.closeChannelRack());
        instrumentContainer.getChildren().addAll(rows);
        initializeContextMenu();
        setAddButton();
    }

    private void initializeContextMenu() {
        channelRackItemContextMenu.getItems().add(deleteItem);
        channelRackItemContextMenu.getStyleClass().add("dark-context-menu");
    }

    protected void addInstrument(Instrument instrument) {
        int channelId = channelCollection.addDefaultChannel(instrument);

        ChannelRackItem item = new ChannelRackItem(channelId, instrument.getName());
        registerChannelRackItem(item);
        rows.add(item);
        if (instrumentContainer != null) {
            instrumentContainer.getChildren().add(item);
        }
    }

    private void deleteInstrument(ChannelRackItem channelRackItem) {
        channelCollection.removeChannel(channelRackItem.getChannelId());
        rows.remove(channelRackItem);
        instrumentContainer.getChildren().remove(channelRackItem);
    }


    private void registerChannelRackItem(ChannelRackItem channelRackItem) {
        channelRackItem.getStepPane().setOnMouseClicked(mouseEvent ->
                mainController.openPianoRoll(channelRackItem.getChannelId())
        );
        channelRackItem.getInstrumentName().setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                deleteItem.setOnAction(e -> deleteInstrument(channelRackItem));
                channelRackItemContextMenu.show(channelRackItem.getInstrumentName(), event.getScreenX(), event.getScreenY());
                midiNoteContainer.removeAllNotesForChanel(channelRackItem.getChannelId());
            }
        });
    }

    private void addSynthesizer() {
        int channelId = channelCollection.addSynthesizerChannel();

        ChannelRackItem item = new ChannelRackItem(channelId, "Custom Synthesizer");
        registerChannelRackItem(item);
        rows.add(item);
        instrumentContainer.getChildren().add(item);
        item.getInstrumentName().setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                mainController.openSynthesizer(channelId);
            }
        });
    }

    private void setAddButton() {
        customSynthesizerItem.setOnAction(e -> addSynthesizer());
        synthesizersContextMenu.getItems().add(customSynthesizerItem);
        addButton.setOnMouseClicked(event ->
                synthesizersContextMenu.show(addButton, event.getScreenX(), event.getScreenY()));
    }

    public void loadFromTracks(Track[] tracks) {
        var channelRackItems = Arrays.stream(tracks)
                .map(track -> new ChannelRackItem(getTrackId(track), getInstrumentForTrack(track).getName()))
                .toList();

        channelRackItems.forEach(this::registerChannelRackItem);
        rows.addAll(channelRackItems);
    }

    @Autowired
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
}
