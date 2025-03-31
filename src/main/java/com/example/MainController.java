package com.example;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class MainController {

    @FXML
    private Button menuButton;

    @FXML
    private StackPane mainContent;

    // Ссылка на окно channel rack (если уже загружено)
    private Node channelRackView;

    @FXML
    public void initialize() {
        menuButton.setOnAction(e -> toggleChannelRack());
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
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("channel-rack.fxml"));
            channelRackView = loader.load();
            ChannelRackController channelRackController = loader.getController();
            channelRackController.setMainController(this);
            mainContent.getChildren().add(channelRackView);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
