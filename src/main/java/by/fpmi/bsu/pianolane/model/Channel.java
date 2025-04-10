package by.fpmi.bsu.pianolane.model;

import by.fpmi.bsu.pianolane.NoteEvent;
import by.fpmi.bsu.pianolane.observer.NoteDeleteObserver;
import by.fpmi.bsu.pianolane.observer.NoteResizedObserver;
import lombok.Data;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.Track;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static by.fpmi.bsu.pianolane.util.GlobalInstances.createTrack;

@Data
public abstract class Channel implements NoteDeleteObserver, NoteResizedObserver {

    private static final AtomicInteger NOTES_SEQUENCE = new AtomicInteger(0);
    private static final Map<Integer, NoteEvent> NOTE_EVENTS = new ConcurrentHashMap<>();

    protected int channelId;
    protected Track track;
    protected boolean muted;
    protected boolean soloed;


    public Channel(int channelId) {
        this.channelId = channelId;
        track = createTrack();
        muted = false;
        soloed = false;
    }

    public Integer addNote(int midiNote, int startTick, int noteDuration) {
        try {
            NoteEvent noteEvent = new NoteEvent(track, channelId, midiNote, startTick, noteDuration);
            Integer key = NOTES_SEQUENCE.getAndIncrement();
            NOTE_EVENTS.put(key, noteEvent);
            return key;
        } catch (InvalidMidiDataException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteNote(Integer key) {
        NoteEvent eventToDelete = NOTE_EVENTS.get(key);
        if (eventToDelete == null) {
            System.out.println("No NoteEvent found for key " + key);
            return;
        }
        NOTE_EVENTS.remove(key);
        eventToDelete.destroy();
    }

    public void resizeNote(Integer key, int newLength) {
        NOTE_EVENTS.get(key).updateLength(newLength);
    }

    @Override
    public void onNoteDeleted(Integer noteId) {
        System.out.println("Delete note " + noteId);
        deleteNote(noteId);
    }

    @Override
    public void onNoteResized(Integer noteId, int newLength) {
        System.out.println("Midi player tries to resize note " + noteId + " with length " + newLength);
        //TODO: get rid of magic numbers
        int midiLength = newLength / 50 * 480;
        resizeNote(noteId, midiLength);
    }
}
