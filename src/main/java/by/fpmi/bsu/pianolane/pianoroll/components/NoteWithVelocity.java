package by.fpmi.bsu.pianolane.pianoroll.components;

import java.io.Serializable;
import javafx.scene.layout.Pane;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
@NoArgsConstructor
//TODO: experiment with final fields and newInstance in serializer
public class NoteWithVelocity implements Serializable {

    private Integer id;
    private Note note;
    private Velocity velocity;

    public static Builder builder() {
        return new Builder();
    }

    private NoteWithVelocity(Integer id, Note note, Velocity velocity) {
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
        private double velocityHeightPercentage;

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

        public Builder velocityHeightPercentage(double heightPercentage) {
            this.velocityHeightPercentage = heightPercentage;
            return this;
        }

        public NoteWithVelocity build() {
            Note note = new Note(id, commonCoordinateX, noteCoordinateY, noteWidth, noteHeight);
            noteParent.getChildren().add(note);
            Velocity velocity = new Velocity(id, velocityParent,  commonCoordinateX, velocityHeightPercentage);
            velocityParent.getChildren().add(velocity);
            return new NoteWithVelocity(id, note, velocity);
        }
    }
}
