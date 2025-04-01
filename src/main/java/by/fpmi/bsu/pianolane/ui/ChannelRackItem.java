package by.fpmi.bsu.pianolane.ui;

import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
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
            "-fx-border-color: transparent; -fx-border-width: 1;" +
            "-fx-text-fill: white; -fx-font-size: 14; -fx-label-padding:  3px;";

    private final String hoveredInstrumentNameStyle =
            "-fx-border-color: white; -fx-border-width: 1;" +
            "-fx-text-fill: white; -fx-font-size: 14; -fx-label-padding: 3px";

    public ChannelRackItem(int channelId, String instrumentName) {
        super(10);
        this.channelId = channelId;
        setAlignment(Pos.CENTER);
        setStyle("-fx-background-color: #3b3b3b; -fx-padding: 5;");

        Circle indicator = new Circle(6);
        indicator.setFill(Color.LIMEGREEN);
        indicator.setCursor(Cursor.HAND);
        indicator.setOnMouseClicked(event -> {
            if (indicator.getFill().equals(Color.LIMEGREEN)) {
                indicator.setFill(Color.DARKGREEN);
            } else {
                indicator.setFill(Color.LIMEGREEN);
            }
        });

        StackPane knob1 = createKnob();
        StackPane knob2 = createKnob();

        this.instrumentName = new Label(instrumentName);
        this.instrumentName.setStyle(defaultInstrumentNameStyle);

        stepPane.setPrefHeight(30);
        stepPane.setPrefWidth(250);
        stepPane.setStyle("-fx-background-color: #2a2a2a;");

        getChildren().addAll(indicator, knob1, knob2, this.instrumentName, stepPane);
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
