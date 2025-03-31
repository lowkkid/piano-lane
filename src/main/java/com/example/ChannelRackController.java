package com.example;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.HBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import java.net.URL;
import java.util.ResourceBundle;

public class ChannelRackController implements Initializable {

    @FXML
    private VBox instrumentContainer;

    @FXML
    private Button closeButton;

    @FXML
    private BorderPane rootPane;


    private MainController mainController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Пример нескольких инструментов
        addInstrument("808BD", true);
        addInstrument("Clap 4", false);

        // Кнопка закрытия (X) – удаляет окно из родительского StackPane
        closeButton.setOnAction(event -> {
            mainController.closeChannelRack();
        });
    }

    /**
     * Добавляет строку инструмента в VBox instrumentContainer.
     */
    private void addInstrument(String instrumentName, boolean isOn) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER);
        row.setStyle("-fx-background-color: #3b3b3b; -fx-padding: 5;");

        // Индикатор (кружок)
        Circle indicator = new Circle(6);
        indicator.setFill(isOn ? Color.LIMEGREEN : Color.DARKGREEN);
        indicator.setCursor(Cursor.HAND);
        indicator.setOnMouseClicked(event -> {
            if (indicator.getFill().equals(Color.LIMEGREEN)) {
                indicator.setFill(Color.DARKGREEN);
            } else {
                indicator.setFill(Color.LIMEGREEN);
            }
        });

        // Две "крутилки"
        StackPane knob1 = createKnob();
        StackPane knob2 = createKnob();

        // Название инструмента
        Label nameLabel = new Label(instrumentName);
        nameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14;");

        // Панель для "шагов"
        StackPane stepPane = new StackPane();
        stepPane.setPrefHeight(30);
        stepPane.setPrefWidth(250);
        stepPane.setStyle("-fx-background-color: #2a2a2a;");

        row.getChildren().addAll(indicator, knob1, knob2, nameLabel, stepPane);
        instrumentContainer.getChildren().add(row);
    }

    /**
     * Создаёт имитацию "крутилки" с вращающейся стрелкой.
     */
    private StackPane createKnob() {
        StackPane knob = new StackPane();
        knob.setPrefSize(24, 24);

        Circle knobCircle = new Circle(12, Color.DARKGRAY);
        knobCircle.setStroke(Color.BLACK);
        knobCircle.setStrokeWidth(1);

        Group arrowGroup = new Group();
        Line line = new Line(0, 0, 0, -12);
        line.setStroke(Color.WHITE);
        line.setStrokeWidth(2);

        Polygon arrowHead = new Polygon(0.0, -15.0, -3.0, -9.0, 3.0, -9.0);
        arrowHead.setFill(Color.WHITE);

        arrowGroup.getChildren().addAll(line, arrowHead);
        arrowGroup.setRotate(0);
        knob.getChildren().addAll(knobCircle, arrowGroup);
        knob.setAlignment(Pos.CENTER);
        knob.setCursor(Cursor.HAND);

        knob.setOnScroll(e -> {
            double currentAngle = arrowGroup.getRotate();
            double delta = e.getDeltaY() / 3;
            double newAngle = currentAngle + delta;
            // Ограничиваем угол
            if (newAngle > 150) {
                newAngle = 150;
            } else if (newAngle < -150) {
                newAngle = -150;
            }
            arrowGroup.setRotate(newAngle);
        });

        return knob;
    }

    protected void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
}
