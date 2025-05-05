package by.fpmi.bsu.pianolane.ui.pianoroll;

import by.fpmi.bsu.pianolane.ui.GridPane;
import java.io.Serializable;
import javafx.scene.layout.Pane;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class MidiNote implements Serializable {

    private final Integer id;
    private final Note note;
    private final Velocity velocity;

    public static Builder builder() {
        return new Builder();
    }

    private MidiNote(Integer id, Note note, Velocity velocity) {
        this.id = id;
        this.note = note;
        this.velocity = velocity;
    }

    public static class Builder {
        private GridPane noteParent;
        private Pane velocityParent;
        private Integer id;
        private double commonCoordinateX;
        private double noteCoordinateY;
        private double noteWidth;
        private double noteHeight;

        public Builder noteParent(GridPane parent) {
            this.noteParent = parent;
            return this;
        }

        public Builder velocityParent(Pane parent) {
            this.velocityParent = parent;
            return this;
        }

        public Builder id(Integer id) {
            this.id = id;
            return this;
        }

        public Builder commonCoordinateX(double x) {
            this.commonCoordinateX = x;
            return this;
        }

        public Builder noteCoordinateY(double y) {
            this.noteCoordinateY = y;
            return this;
        }

        public Builder noteWidth(double width) {
            this.noteWidth = width;
            return this;
        }

        public Builder noteHeight(double height) {
            this.noteHeight = height;
            return this;
        }

        public MidiNote build() {
            Note note = new Note(id, commonCoordinateX, noteCoordinateY, noteWidth, noteHeight);
            noteParent.getChildren().add(note);
            Velocity velocity = new Velocity(id, velocityParent,  commonCoordinateX);
            velocityParent.getChildren().add(velocity);
            return new MidiNote(id, note, velocity);
        }
    }
}
