package by.fpmi.bsu.pianolane.midi.note;

import javax.sound.midi.ShortMessage;

public class NoteOffMessage extends NoteMessage {

    private static final int NOTE_OFF_VELOCITY = 0;

    public NoteOffMessage(int channelId, int midiNote) {
        super();
        setMessage(channelId, midiNote);
    }

    private void setMessage(int channelId, int midiNote) {
        super.setMessage(channelId, midiNote, NOTE_OFF_VELOCITY, ShortMessage.NOTE_OFF);
    }
}