package by.fpmi.bsu.pianolane.ui.pianoroll;

import static by.fpmi.bsu.pianolane.ui.Constants.NOTE_AND_VELOCITY_COLOR;
import static by.fpmi.bsu.pianolane.util.constants.DefaultValues.NORMALIZED_DEFAULT_VELOCITY_VALUE;

import by.fpmi.bsu.pianolane.observer.VelocityChangedObserver;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.util.concurrent.atomic.AtomicBoolean;

public class Velocity extends Group {

    private final Integer noteId;
    private final Rectangle vertLine;
    private final Circle topCircle;
    private final Rectangle handle;
    private final DoubleProperty heightPercentage;
    private final Pane parent;
    private final double x;
    private final List<VelocityChangedObserver> velocityChangedObservers = new ArrayList<>();

    public Velocity(Integer noteId, Pane parent, double x) {
        this.noteId = noteId;
        this.parent = parent;
        this.x = x;
        this.heightPercentage = new SimpleDoubleProperty(NORMALIZED_DEFAULT_VELOCITY_VALUE);
        // Create the main vertical line (anchored at the bottom)
        vertLine = new Rectangle(x, 0, 2, 0); // height will be set by binding
        vertLine.setFill(NOTE_AND_VELOCITY_COLOR);

        // Bind the vertical line's height to a percentage of the panel height
        vertLine.heightProperty().bind(parent.heightProperty().multiply(heightPercentage));

        // Position the line from the bottom
        vertLine.yProperty().bind(parent.heightProperty().subtract(vertLine.heightProperty()));

        // Create the circle at the top of the line
        topCircle = new Circle(x + 1, 0, 5);
        topCircle.setFill(NOTE_AND_VELOCITY_COLOR);
        topCircle.centerYProperty().bind(vertLine.yProperty().subtract(3));

        // Create the horizontal handle
        handle = new Rectangle(x + 1, 0, 15, 2);
        handle.setFill(NOTE_AND_VELOCITY_COLOR);
        handle.yProperty().bind(vertLine.yProperty().subtract(3));

        // Add all elements to the group
        getChildren().addAll(vertLine, topCircle, handle);

        setupDragHandling();
    }

    private void setupDragHandling() {
        // Make the handle draggable
        handle.setCursor(Cursor.V_RESIZE);

        // Track whether we're currently dragging
        AtomicBoolean dragging = new AtomicBoolean(false);

        // Store initial values for drag calculation
        final DoubleProperty initialY = new SimpleDoubleProperty();
        final DoubleProperty initialHeight = new SimpleDoubleProperty();

        handle.setOnMousePressed(e -> {
            dragging.set(true);
            initialY.set(e.getSceneY());
            initialHeight.set(heightPercentage.get());
            e.consume();
        });

        handle.setOnMouseDragged(e -> {
            if (dragging.get()) {
                // Calculate change in Y position (negative because moving up should increase height)
                double deltaY = -(e.getSceneY() - initialY.get()) / parent.getHeight();

                // Update the height percentage (constrain between 0.1 and 1.0)
                double newPercentage = Math.min(1.0, Math.max(0.1, initialHeight.get() + deltaY));
                heightPercentage.set(newPercentage);

                e.consume();
            }
        });

        handle.setOnMouseReleased(e -> {
            dragging.set(false);
            notifyResizeEventObservers();
            e.consume();
        });
    }

    public double getHeightPercentage() {
        return heightPercentage.get();
    }

    public void setHeightPercentage(double percentage) {
        this.heightPercentage.set(percentage);
    }

    public void subscribeToVelocityChangedEvent(VelocityChangedObserver velocityChangedObserver) {
        velocityChangedObservers.add(velocityChangedObserver);
    }

    private void notifyResizeEventObservers() {
        for (VelocityChangedObserver observer : velocityChangedObservers) {
            observer.onVelocityChanged(noteId, (int) (heightPercentage.get() * 100));
        }
    }
}