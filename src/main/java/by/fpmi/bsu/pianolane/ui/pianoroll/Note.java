package by.fpmi.bsu.pianolane.ui.pianoroll;

import static by.fpmi.bsu.pianolane.ui.Constants.NOTE_AND_VELOCITY_COLOR;

import by.fpmi.bsu.pianolane.observer.NoteDeleteObserver;
import by.fpmi.bsu.pianolane.observer.NoteResizedObserver;
import by.fpmi.bsu.pianolane.model.Channel;
import by.fpmi.bsu.pianolane.ui.NoteContainer;
import by.fpmi.bsu.pianolane.ui.GridPane;
import javafx.scene.Cursor;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Note extends Rectangle {

    private final int cellWidth = 50;

    private final Integer noteId;

    private final List<NoteDeleteObserver> noteDeleteObservers = new ArrayList<>();
    private final List<NoteResizedObserver> noteResizedObservers = new ArrayList<>();

    private static final double RESIZE_AREA_WIDTH = 5;
    private boolean isResizing = false;

    public Note(Integer noteId, Channel channel, double x, double y, double width, double height) {
        super(x, y, width, height);
        this.noteId = noteId;
        setFill(NOTE_AND_VELOCITY_COLOR);
        setStroke(Color.BLACK);

        setRightMouseClickListener();
        setResizeHandlers();
        subscribeToNoteDeleteEvent(channel);
        subscribeToNoteResizedEvent(channel);
    }

    private void setRightMouseClickListener() {
        this.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                System.out.println("Note clicked with RMB");
                GridPane roll = (GridPane) this.getParent();
                roll.getChildren().remove(this);
                NoteContainer.removeNote(roll.getChannelId(), this);
                notifyDeleteEventObservers();
            }
        });
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

        this.setOnMouseDragged(event -> {
            if (isResizing) {
                double newWidth = event.getX() - this.getX();
                newWidth = Math.round(newWidth / cellWidth) * cellWidth;
                newWidth = Math.max(newWidth, cellWidth);
                this.setWidth(newWidth);
                event.consume();
            }
        });

        this.setOnMouseReleased(event -> {
            isResizing = false;
            event.consume();
            notifyResizeEventObservers();
        });
    }

    private boolean isInResizeArea(double mouseX) {
        double rightEdge = this.getX() + this.getWidth();
        return mouseX >= rightEdge - RESIZE_AREA_WIDTH && mouseX <= rightEdge;
    }

    public void subscribeToNoteDeleteEvent(NoteDeleteObserver noteDeleteObserver) {
        noteDeleteObservers.add(noteDeleteObserver);
    }

    public void subscribeToNoteResizedEvent(NoteResizedObserver noteResizedObserver) {
        noteResizedObservers.add(noteResizedObserver);
    }

    private void notifyDeleteEventObservers() {
        for (NoteDeleteObserver observer : noteDeleteObservers) {
            observer.onNoteDeleted(noteId);
        }
    }

    private void notifyResizeEventObservers() {
        for (NoteResizedObserver observer : noteResizedObservers) {
            observer.onNoteResized(noteId, (int) getWidth());
        }
    }
}
