package by.fpmi.bsu.pianolane.ui.pianoroll;

import by.fpmi.bsu.pianolane.model.Channel;
import by.fpmi.bsu.pianolane.observer.MidiNoteDeleteObserver;
import by.fpmi.bsu.pianolane.ui.GridPane;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import lombok.Getter;

@Getter
public class MidiNote {

    private final Integer id;
    private final Channel channel;
    private final Note note;
    private final Velocity velocity;
    private final GridPane noteParent;
    private final Pane velocityParent;
    private final List<MidiNoteDeleteObserver> midiNoteDeleteObservers = new ArrayList<>();

    public static Builder builder() {
        return new Builder();
    }

    private MidiNote(Integer id, Channel channel, Note note, Velocity velocity, GridPane noteParent, Pane velocityParent) {
        this.id = id;
        this.channel = channel;
        this.note = note;
        this.velocity = velocity;
        this.noteParent = noteParent;
        this.velocityParent = velocityParent;

        subscribeToMidiNoteDeleteEvent(this.channel);
        setMidiNoteDeleteListener();
    }


    private void setMidiNoteDeleteListener() {
        note.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                System.out.println("Note clicked with RMB");
                noteParent.getChildren().remove(note);
                velocityParent.getChildren().remove(velocity);
                MidiNoteContainer.removeNote(channel.getChannelId(), this);
                notifyDeleteEventObservers();
            }
        });

    }

    private void notifyDeleteEventObservers() {
        for (MidiNoteDeleteObserver observer : midiNoteDeleteObservers) {
            observer.onNoteDeleted(id);
        }
    }

    public void subscribeToMidiNoteDeleteEvent(MidiNoteDeleteObserver midiNoteDeleteObserver) {
        midiNoteDeleteObservers.add(midiNoteDeleteObserver);
    }

    public static class Builder {
        private GridPane noteParent;
        private Pane velocityParent;
        private Integer id;
        private Channel channel;
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

        public Builder channel(Channel channel) {
            this.channel = channel;
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
            note.subscribeToNoteResizedEvent(channel);
            noteParent.getChildren().add(note);
            Velocity velocity = new Velocity(id, velocityParent,  commonCoordinateX);
            velocityParent.getChildren().add(velocity);
            velocity.subscribeToVelocityChangedEvent(channel);
            return new MidiNote(id, channel, note, velocity, noteParent, velocityParent);
        }
    }
}
