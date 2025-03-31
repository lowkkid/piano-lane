package by.fpmi.bsu.pianolane;

import lombok.extern.slf4j.Slf4j;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

@Slf4j
public class NoteEvent {

    private MidiEvent noteOnEvent;
    private final ShortMessage noteOnMessage;

    private MidiEvent noteOffEvent;
    private final ShortMessage noteOffMessage;
    private final Track track;

    private static final int noteOffVelocity = 0;
    //TODO get rid of one channel for all instruments, let user send any instrument on specific channel
    private static final int CHANNEL = 0;

    public NoteEvent(Track track, int midiNote, int startTick, int noteDuration) throws InvalidMidiDataException {
        this.track = track;
        noteOnMessage = new ShortMessage();
        noteOnMessage.setMessage(ShortMessage.NOTE_ON, CHANNEL, midiNote, 100);
        noteOnEvent = new MidiEvent(noteOnMessage, startTick);

        noteOffMessage = new ShortMessage();
        noteOffMessage.setMessage(ShortMessage.NOTE_OFF, CHANNEL, midiNote, noteOffVelocity);
        noteOffEvent = new MidiEvent(noteOffMessage, startTick + noteDuration);

        registerNoteEvent();
    }

    public void updateLength(int newLength) {
        //track.remove(noteOffEvent);
        noteOffEvent.setTick(noteOnEvent.getTick() + newLength);
        //noteOffEvent = new MidiEvent(noteOffMessage, noteOnEvent.getTick() + newLength);
        //track.add(noteOffEvent);
    }

    public void destroy() {
        track.remove(noteOnEvent);
        track.remove(noteOffEvent);
    }

    private void registerNoteEvent() {
        track.add(noteOnEvent);
        track.add(noteOffEvent);
    }
}
