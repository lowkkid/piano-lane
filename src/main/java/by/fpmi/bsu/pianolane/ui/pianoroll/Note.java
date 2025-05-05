package by.fpmi.bsu.pianolane.ui.pianoroll;

import static by.fpmi.bsu.pianolane.ui.Constants.NOTE_AND_VELOCITY_COLOR;

import javafx.scene.Cursor;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Note extends Rectangle {

    private static final double RESIZE_AREA_WIDTH = 5;

    private final Integer noteId;
    private boolean isResizing = false;

    public Note(Integer noteId, double x, double y, double width, double height) {
        super(x, y, width, height);
        this.noteId = noteId;
        setFill(NOTE_AND_VELOCITY_COLOR);
        setStroke(Color.BLACK);

        setResizeHandlers();
    }

    private void setResizeHandlers() {
        this.setOnMouseMoved(event -> {
            if (isInResizeArea(event.getX())) {
                this.setCursor(Cursor.E_RESIZE);
            } else {
                this.setCursor(Cursor.DEFAULT);
            }
        });

        this.setOnMousePressed(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                if (isInResizeArea(event.getX())) {
                    isResizing = true;
                }
                event.consume();
            }
        });
    }

    private boolean isInResizeArea(double mouseX) {
        double rightEdge = this.getX() + this.getWidth();
        return mouseX >= rightEdge - RESIZE_AREA_WIDTH && mouseX <= rightEdge;
    }
}
