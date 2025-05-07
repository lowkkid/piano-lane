package by.fpmi.bsu.pianolane.wrappers;

import javax.sound.midi.ShortMessage;

public class NoteOnMessage extends NoteMessage {

    public NoteOnMessage(int channelId, int midiNote, int velocity) {
        super();
        setMessage(channelId, midiNote, velocity);
    }

    public void updateVelocity(int newVelocity) {
        setMessage(message.getChannel(), message.getData1(), newVelocity);
    }

    private void setMessage(int channelId, int midiNote, int velocity) {
        super.setMessage(channelId, midiNote, velocity, ShortMessage.NOTE_ON);
    }
}