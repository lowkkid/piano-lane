package by.fpmi.bsu.pianolane.mainwindow;

import static by.fpmi.bsu.pianolane.common.util.constants.FxmlPaths.CHANNEL_RACK_FXML;
import static by.fpmi.bsu.pianolane.common.util.constants.FxmlPaths.PIANO_ROLL_FXML;
import static by.fpmi.bsu.pianolane.common.util.constants.FxmlPaths.SYNTHESIZER_FXML;

import by.fpmi.bsu.pianolane.common.util.SpringFxmlLoader;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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

    public void closeChannelRack() {
        mainContent.getChildren().remove(channelRackView);
        channelRackView = null;
    }

    protected void openChannelRack() {
        SpringFxmlLoader springFxmlLoader = new SpringFxmlLoader();
        channelRackView = springFxmlLoader.load(CHANNEL_RACK_FXML);
        makeDraggable(channelRackView);
        mainContent.getChildren().add(channelRackView);
    }

    public void closePianoRoll() {
        mainContent.getChildren().remove(pianoRollView);
        pianoRollView = null;
    }

    public void openPianoRoll(int channelId) {
        log.info("Opening piano roll for channel {}", channelId);
        SpringFxmlLoader springFxmlLoader = new SpringFxmlLoader();
        pianoRollView = springFxmlLoader.load(PIANO_ROLL_FXML, channelId);
        makeDraggable(pianoRollView);
        mainContent.getChildren().add(pianoRollView);

    }

    public void openSynthesizer(int channelId) {
        SpringFxmlLoader springFxmlLoader = new SpringFxmlLoader();
        synthesizerView = springFxmlLoader.load(SYNTHESIZER_FXML, channelId);
        makeDraggable(synthesizerView);
        mainContent.getChildren().add(synthesizerView);
    }

    public void closeSynthesizer() {
        mainContent.getChildren().remove(synthesizerView);
        synthesizerView = null;
    }

    private void makeDraggable(Node node) {
        // Переменные для хранения начальной позиции клика
        final Delta dragDelta = new Delta();

        // Обработчик нажатия мыши
        node.setOnMousePressed(mouseEvent -> {
            // Сохраняем начальную позицию клика относительно текущего положения узла
            dragDelta.xCoordinate = node.getTranslateX() - mouseEvent.getSceneX();
            dragDelta.yCoordinate = node.getTranslateY() - mouseEvent.getSceneY();

            // Поднимаем элемент наверх при клике
            node.toFront();
        });

        // Обработчик перетаскивания
        node.setOnMouseDragged(mouseEvent -> {
            // Вычисляем новую позицию
            double newX = mouseEvent.getSceneX() + dragDelta.xCoordinate;
            double newY = mouseEvent.getSceneY() + dragDelta.yCoordinate;

            // Устанавливаем новую позицию без ограничений минимума в 0
            node.setTranslateX(newX);
            node.setTranslateY(newY);
        });
    }

    private static class Delta {
        double xCoordinate;
        double yCoordinate;
    }
}
