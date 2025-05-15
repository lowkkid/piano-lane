package by.fpmi.bsu.pianolane.common.channelrack;

import static by.fpmi.bsu.pianolane.common.util.MidiUtil.getInstrumentForTrack;
import static by.fpmi.bsu.pianolane.common.util.MidiUtil.getTrackId;

import by.fpmi.bsu.pianolane.mainwindow.MainController;
import by.fpmi.bsu.pianolane.midi.channel.ChannelCollection;
import by.fpmi.bsu.pianolane.pianoroll.MidiNoteContainer;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javax.sound.midi.Instrument;
import javax.sound.midi.Track;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;



@Component
@Slf4j
public class ChannelRackController implements Initializable {

    private static final String SYNTHESIZER_NAME = "Simple synthesizer";

    @FXML
    private VBox instrumentContainer;
    @FXML
    private Button closeButton;
    @FXML
    private Button addButton;

    private final MainController mainController;

    public ChannelRackController(MainController mainController) {
        this.mainController = mainController;
    }

    private final ChannelCollection channelCollection = ChannelCollection.getInstance();
    private final MidiNoteContainer midiNoteContainer = MidiNoteContainer.getInstance();

    private final ContextMenu channelRackItemContextMenu = new ContextMenu();
    private final MenuItem deleteItem = new MenuItem("Delete");

    private final ContextMenu synthesizersContextMenu = new ContextMenu();
    private final MenuItem customSynthesizerItem = new MenuItem(SYNTHESIZER_NAME);

    private final List<ChannelRackItem> rows = new ArrayList<>();


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

    public void addInstrument(Instrument instrument) {
        int channelId = channelCollection.addDefaultChannel(instrument);

        ChannelRackItem item = new ChannelRackItem(channelId, generateUniqueNameForChannelRackItem(instrument.getName()));
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
                channelRackItemContextMenu.show(
                        channelRackItem.getInstrumentName(), event.getScreenX(), event.getScreenY());
                midiNoteContainer.removeAllNotesForChanel(channelRackItem.getChannelId());
            }
        });
        channelRackItem.getIsEnabled().addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            boolean isMuted = !channelRackItem.isEnabled();
            if (event.getButton() == MouseButton.SECONDARY) {
                var otherChannelRackItems = rows.stream()
                        .filter(item -> item.getChannelId() != channelRackItem.getChannelId())
                        .toList();

                channelRackItem.setEnabled(true);
                if (!channelCollection.getChannel(channelRackItem.getChannelId()).isSoloed()) {
                    otherChannelRackItems.forEach(item -> item.setEnabled(false));
                } else {
                    otherChannelRackItems.forEach(item -> item.setEnabled(true));
                }
                channelCollection.soloChannel(channelRackItem.getChannelId());
                event.consume();
            } else if (event.getButton() == MouseButton.PRIMARY) {
                channelCollection.getChannel(channelRackItem.getChannelId()).setMute(isMuted);
            }
        });
        channelRackItem.getPanKnob().valueProperty().addListener((obs, oldVal, newVal) -> {
                    System.out.println(newVal.intValue());
                    channelCollection.getChannel(channelRackItem.getChannelId()).setPan(newVal.intValue());
                }
        );
        channelRackItem.getVolumeKnob().valueProperty().addListener((obs, oldVal, newVal) -> {
            System.out.println(newVal.intValue());
            channelCollection.getChannel(channelRackItem.getChannelId()).setVolume(newVal.intValue());
        });
    }

    private void addSynthesizer() {
        int channelId = channelCollection.addSynthesizerChannel();

        ChannelRackItem item = new ChannelRackItem(channelId, SYNTHESIZER_NAME);
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

    private String generateUniqueNameForChannelRackItem(String baseName) {
        final String NAME_TEMPLATE = "%s #%d";
        var existingNames = rows.stream()
                .map(row -> row.getInstrumentName().getText())
                .collect(Collectors.toSet());

        var uniqueName = baseName;
        var suffix = 1;

        while (existingNames.contains(uniqueName)) {
            uniqueName = String.format(NAME_TEMPLATE, baseName, suffix++);
        }

        return uniqueName;
    }

//    @Autowired
//    public void setMainController(MainController mainController) {
//        this.mainController = mainController;
//    }
}
