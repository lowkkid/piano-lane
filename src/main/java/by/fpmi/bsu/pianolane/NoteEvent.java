package by.fpmi.bsu.pianolane;

import lombok.extern.slf4j.Slf4j;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

import static by.fpmi.bsu.pianolane.util.LogUtil.getAllTrackEvents;

@Slf4j
public class NoteEvent {

    private MidiEvent noteOnEvent;
    private final ShortMessage noteOnMessage;

    private MidiEvent noteOffEvent;
    private final ShortMessage noteOffMessage;
    private final Track track;

    private static final int noteOffVelocity = 0;

    public NoteEvent(Track track, int channelId, int midiNote, int startTick, int noteDuration) throws InvalidMidiDataException {
        this.track = track;
        noteOnMessage = new ShortMessage();
        noteOnMessage.setMessage(ShortMessage.NOTE_ON, channelId, midiNote, 100);
        noteOnEvent = new MidiEvent(noteOnMessage, startTick);

        noteOffMessage = new ShortMessage();
        noteOffMessage.setMessage(ShortMessage.NOTE_OFF, channelId, midiNote, noteOffVelocity);
        noteOffEvent = new MidiEvent(noteOffMessage, startTick + noteDuration);

        registerNoteEvent();
        log.debug("Registered note event for channel {}. Current events in this track are:\n {} ", channelId, getAllTrackEvents(track));

    }

    public void updateLength(int newLength) {
        track.remove(noteOffEvent);
        noteOffEvent = new MidiEvent(noteOffMessage, noteOnEvent.getTick() + newLength);
        track.add(noteOffEvent);
        log.debug("Resized note event. Current events in this track are:\n {} ", getAllTrackEvents(track));
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
