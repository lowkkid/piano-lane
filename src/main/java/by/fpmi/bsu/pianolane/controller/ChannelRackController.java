package by.fpmi.bsu.pianolane.controller;

import by.fpmi.bsu.pianolane.ui.ChannelRackItem;
import by.fpmi.bsu.pianolane.util.ChannelCollection;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
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

    private MainController mainController;
    private ChannelCollection channelCollection;

    private final ContextMenu contextMenu = new ContextMenu();
    private final MenuItem deleteItem = new MenuItem("Delete");

    List<ChannelRackItem> rows = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        closeButton.setOnAction(event -> mainController.closeChannelRack());
        instrumentContainer.getChildren().addAll(rows);
        initializeContextMenu();
    }

    private void initializeContextMenu() {
        contextMenu.getItems().add(deleteItem);
        contextMenu.getStyleClass().add("dark-context-menu");
    }

    /**
     * Добавляет строку инструмента в VBox instrumentContainer.
     */
    protected void addInstrument(Instrument instrument) {
        int channelId = channelCollection.addChannel(instrument);

        ChannelRackItem item = new ChannelRackItem(channelId, instrument.getName());
        registerChannelRackItem(item);
        rows.add(item);
        instrumentContainer.getChildren().add(item);
    }

    private void deleteInstrument(ChannelRackItem channelRackItem) {
        channelCollection.removeChannel(channelRackItem.getChannelId());
        rows.remove(channelRackItem);
        instrumentContainer.getChildren().remove(channelRackItem);
    }


    private void registerChannelRackItem(ChannelRackItem channelRackItem) {
        log.info("Registering channelRackItem pane withId {}", channelRackItem.getChannelId());
        channelRackItem.getStepPane().setOnMouseClicked(mouseEvent ->
            mainController.openPianoRoll(channelRackItem.getChannelId())
        );
        channelRackItem.getInstrumentName().setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                deleteItem.setOnAction(e -> deleteInstrument(channelRackItem));
                contextMenu.show(channelRackItem.getInstrumentName(), event.getScreenX(), event.getScreenY());

            }
        });
    }

    @Autowired
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @Autowired
    public void setChannelCollection(ChannelCollection channelCollection) {
        this.channelCollection = channelCollection;
    }
}
