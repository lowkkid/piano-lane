package by.fpmi.bsu.pianolane.ui;

import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChannelRackItem extends HBox {

    private int channelId;
    private StackPane stepPane = new StackPane();
    private Label instrumentName;

    private final String defaultInstrumentNameStyle =
            "-fx-border-color: transparent; -fx-border-width: 1;"
                    + "-fx-text-fill: white; -fx-font-size: 14; -fx-label-padding:  3px;";

    private final String hoveredInstrumentNameStyle =
            "-fx-border-color: white; -fx-border-width: 1;"
                    + "-fx-text-fill: white; -fx-font-size: 14; -fx-label-padding: 3px";

    public ChannelRackItem(int channelId, String instrumentName) {
        super(10);
        this.channelId = channelId;
        setAlignment(Pos.CENTER_LEFT);
        setStyle("-fx-background-color: #3b3b3b; -fx-padding: 5;");

        // Индикатор
        Circle indicator = new Circle(6);
        indicator.setFill(Color.LIMEGREEN);
        indicator.setCursor(Cursor.HAND);
        StackPane indicatorContainer = new StackPane(indicator);
        indicatorContainer.setPrefWidth(24);
        indicatorContainer.setMinWidth(24);
        indicatorContainer.setMaxWidth(24);

        indicator.setOnMouseClicked(event -> {
            if (indicator.getFill().equals(Color.LIMEGREEN)) {
                indicator.setFill(Color.DARKGREEN);
            } else {
                indicator.setFill(Color.LIMEGREEN);
            }
        });

        // Ручки управления
        StackPane knob1 = createKnob();
        knob1.setPrefWidth(24);
        knob1.setMinWidth(24);
        knob1.setMaxWidth(24);

        StackPane knob2 = createKnob();
        knob2.setPrefWidth(24);
        knob2.setMinWidth(24);
        knob2.setMaxWidth(24);

        // Название инструмента
        this.instrumentName = new Label(instrumentName);
        this.instrumentName.setStyle(defaultInstrumentNameStyle);
        this.instrumentName.setPrefWidth(120);
        this.instrumentName.setMinWidth(120);
        this.instrumentName.setMaxWidth(120);

        // Панель шагов - занимает все оставшееся пространство
        stepPane.setPrefHeight(30);
        stepPane.setPrefWidth(400); // Увеличена предпочтительная ширина
        stepPane.setMinWidth(300);  // Минимальная ширина
        stepPane.setStyle("-fx-background-color: #2a2a2a;");

        getChildren().addAll(indicatorContainer, knob1, knob2, this.instrumentName, stepPane);

        // Установка приоритетов роста - только stepPane может расти
        HBox.setHgrow(indicatorContainer, Priority.NEVER);
        HBox.setHgrow(knob1, Priority.NEVER);
        HBox.setHgrow(knob2, Priority.NEVER);
        HBox.setHgrow(this.instrumentName, Priority.NEVER);
        HBox.setHgrow(stepPane, Priority.ALWAYS); // Разрешаем расти и занимать все доступное пространство

        addInstrumentNameListeners();
    }

    private void addInstrumentNameListeners() {
        instrumentName.setOnMouseEntered(event ->
                instrumentName.setStyle(hoveredInstrumentNameStyle)
        );
        instrumentName.setOnMouseExited(event ->
                instrumentName.setStyle(defaultInstrumentNameStyle)
        );
    }

    private StackPane createKnob() {
        StackPane knob = new StackPane();
        knob.setPrefSize(24, 24);
        knob.setMinSize(24, 24);
        knob.setMaxSize(24, 24);

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
            if (newAngle > 150) {
                newAngle = 150;
            } else if (newAngle < -150) {
                newAngle = -150;
            }
            arrowGroup.setRotate(newAngle);
        });

        return knob;
    }
}