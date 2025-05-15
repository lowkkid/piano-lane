package by.fpmi.bsu.pianolane.common.ui;

import java.util.function.Consumer;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ElementsFactory {

    public static VBox createKnobWithLabel(
            int size,
            Color color,
            String labelText,
            double initialValue,
            double minValue,
            double maxValue,
            Consumer<Number> listener) {
        VBox box = new VBox(5);
        box.setAlignment(Pos.CENTER);

        KnobControl knob = new KnobControl(size, color, initialValue, minValue, maxValue);

        Label label = new Label(labelText);
        label.setTextFill(color);
        label.setFont(Font.font("Arial", FontWeight.NORMAL, 11));

        knob.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (listener != null) {
                listener.accept(newVal);
            }
        });

        box.getChildren().addAll(knob, label);
        return box;
    }

    public static KnobControl createKnob(
            int size,
            Color color,
            double initialValue,
            double minValue,
            double maxValue) {

        return new KnobControl(size, color, initialValue, minValue, maxValue);
    }

    public static CheckBox createRoundGreenCheckbox() {
        CheckBox checkBox = new CheckBox();
        checkBox.setSelected(true);
        checkBox.setCursor(Cursor.HAND);
        checkBox.getStyleClass().add("round-check-box");
        return checkBox;
    }
}
