package by.fpmi.bsu.pianolane.wrappers;

import java.util.Objects;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.ShortMessage;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class NoteMidiEvent {

    private MidiEvent midiEvent;
    private final NoteMessage noteMessage;

    public NoteMidiEvent(NoteMessage noteMessage, long tick) {
        this.noteMessage = Objects.requireNonNull(noteMessage, "Note Message cannot be null");
        midiEvent = new MidiEvent(noteMessage.message(), tick);
    }

    public void updateVelocity(int newVelocity) {
        if (noteMessage instanceof NoteOffMessage) {
            throw new UnsupportedOperationException("Velocity can't be changed for NoteOffMessage");
        }
        NoteOnMessage noteOnMessage = (NoteOnMessage) noteMessage;
        noteOnMessage.updateVelocity(newVelocity);
    }

    public void transfer(long newTick) {
        midiEvent.setTick(newTick);
    }

    public MidiEvent midiEvent() {
        return midiEvent;
    }

    public NoteMessage noteMessage() {
        return noteMessage;
    }

    public long getTick() {
        return midiEvent.getTick();
    }

    public void setMidiEvent(MidiEvent midiEvent) {
        this.midiEvent = midiEvent;
        noteMessage.setMessage((ShortMessage) midiEvent.getMessage());
    }

    @Override
    public String toString() {
        return "NoteMidiEvent{" +
                "tick=" + getTick() +
                ", noteMessage=" + noteMessage +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NoteMidiEvent that = (NoteMidiEvent) o;
        return getTick() == that.getTick() &&
                Objects.equals(noteMessage, that.noteMessage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTick(), noteMessage);
    }

    public static Builder builder() {
        return new Builder();
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Builder {
        private Integer channelId;
        private Integer midiNote;
        private Integer velocity;
        private Boolean isNoteOn;
        private Long tick;

        public Builder channelId(int channelId) {
            this.channelId = channelId;
            return this;
        }

        public Builder midiNote(int midiNote) {
            this.midiNote = midiNote;
            return this;
        }

        public Builder velocity(int velocity) {
            this.velocity = velocity;
            return this;
        }

        public Builder noteOn() {
            this.isNoteOn = true;
            return this;
        }

        public Builder noteOff() {
            this.isNoteOn = false;
            return this;
        }

        public Builder tick(long tick) {
            this.tick = tick;
            return this;
        }

        public NoteMidiEvent build() {
            Objects.requireNonNull(tick, "Tick must be specified");
            Objects.requireNonNull(channelId, "Channel ID must be specified");
            Objects.requireNonNull(midiNote, "MIDI note must be specified");
            Objects.requireNonNull(isNoteOn, "Note type (on/off) must be specified");

            NoteMessage noteMessage;

            if (isNoteOn) {
                Objects.requireNonNull(velocity, "Velocity must be specified for Note On messages");
                noteMessage = new NoteOnMessage(channelId, midiNote, velocity);
            } else {
                noteMessage = new NoteOffMessage(channelId, midiNote);
            }

            return new NoteMidiEvent(noteMessage, tick);
        }
    }
}
