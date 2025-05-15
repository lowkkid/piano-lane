package by.fpmi.bsu.pianolane.midi.note;

import java.util.Objects;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.ShortMessage;
import lombok.Setter;

@Setter
public abstract class NoteMessage {

    protected ShortMessage message;

    protected NoteMessage() {
        this.message = new ShortMessage();
    }

    protected void setMessage(int channelId, int midiNote, int velocity, int command) {
        try {
            message.setMessage(command, channelId, midiNote, velocity);
        } catch (InvalidMidiDataException e) {
            throw new RuntimeException(e);
        }
    }

    public ShortMessage message() {
        return message;
    }

    public int getChannel() {
        return message.getChannel();
    }

    public int getMidiNote() {
        return message.getData1();
    }

    public int getVelocity() {
        return message.getData2();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "channelId=" + getChannel() +
                ", midiNote=" + getMidiNote() +
                ", velocity=" + getVelocity() + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NoteMessage that = (NoteMessage) o;
        return getChannel() == that.getChannel()
                && getMidiNote() == that.getMidiNote()
                && getVelocity() == that.getVelocity();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getChannel(), getMidiNote(), getVelocity());
    }
}