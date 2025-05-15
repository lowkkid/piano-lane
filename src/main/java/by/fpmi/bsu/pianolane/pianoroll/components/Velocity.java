package by.fpmi.bsu.pianolane.pianoroll.components;

import static by.fpmi.bsu.pianolane.common.Constants.UiConstants.NOTE_AND_VELOCITY_COLOR;

import java.io.Serializable;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import lombok.Getter;

@Getter
public class Velocity extends Group implements Serializable {

    private final Integer noteId;
    private final Rectangle vertLine;
    private final Circle topCircle;
    private final Rectangle handle;
    private final DoubleProperty heightPercentage;
    private Pane parentPane;
    private final double x;

    public Velocity(Integer noteId, Pane parentPane, double x, double heightPercentage) {
        this.noteId = noteId;
        this.x = x;
        this.heightPercentage = new SimpleDoubleProperty(heightPercentage);
        vertLine = new Rectangle(x, 0, 2, 0);
        vertLine.setFill(NOTE_AND_VELOCITY_COLOR);

        if (parentPane != null) {
            setParentPane(parentPane);
        }

        // Create the circle at the top of the line
        topCircle = new Circle(x + 1, 0, 5);
        topCircle.setFill(NOTE_AND_VELOCITY_COLOR);
        topCircle.centerYProperty().bind(vertLine.yProperty().subtract(3));

        // Create the horizontal handle
        handle = new Rectangle(x + 1, 0, 15, 2);
        handle.setFill(NOTE_AND_VELOCITY_COLOR);
        handle.yProperty().bind(vertLine.yProperty().subtract(3));
        handle.setCursor(Cursor.V_RESIZE);

        // Add all elements to the group
        getChildren().addAll(vertLine, topCircle, handle);
    }

    public void setParentPane(Pane parentPane) {
        this.parentPane = parentPane;
        // Bind the vertical line's height to a percentage of the panel height
        vertLine.heightProperty().bind(parentPane.heightProperty().multiply(heightPercentage));

        // Position the line from the bottom
        vertLine.yProperty().bind(parentPane.heightProperty().subtract(vertLine.heightProperty()));
    }

    public double getHeightPercentage() {
        return heightPercentage.get();
    }

    public int getVelocityValue() {
        return (int) (heightPercentage.get() * 100);
    }

    public void setHeightPercentage(double percentage) {
        this.heightPercentage.set(percentage);
    }
}