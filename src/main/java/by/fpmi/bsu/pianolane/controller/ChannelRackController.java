package by.fpmi.bsu.pianolane.controller;

import by.fpmi.bsu.pianolane.ui.ChannelRackItem;
import by.fpmi.bsu.pianolane.util.ChannelCollection;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sound.midi.Instrument;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

@Component
public class ChannelRackController implements Initializable {

    @FXML
    private VBox instrumentContainer;

    @FXML
    private Button closeButton;

    @FXML
    private BorderPane rootPane;

    private MainController mainController;
    private ChannelCollection channelCollection;

    List<ChannelRackItem> rows = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Пример нескольких инструментов
//        addInstrument("808BD", true);
//        addInstrument("Clap 4", false);

        // Кнопка закрытия (X) – удаляет окно из родительского StackPane
        closeButton.setOnAction(event -> {
            mainController.closeChannelRack();
        });
        instrumentContainer.getChildren().addAll(rows);
    }

    /**
     * Добавляет строку инструмента в VBox instrumentContainer.
     */
    protected void addInstrument(Instrument instrument) {
        int channelId = channelCollection.addChannel(instrument);

        ChannelRackItem item = new ChannelRackItem(channelId, instrument.getName());
        registerChannelRackItem(item);
        instrumentContainer.getChildren().add(item);
    }

    private void registerChannelRackItem(ChannelRackItem channelRackItem) {
        channelRackItem.getStepPane().setOnMouseClicked(mouseEvent -> {
            mainController.openPianoRoll();
        });
        rows.add(channelRackItem);
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
