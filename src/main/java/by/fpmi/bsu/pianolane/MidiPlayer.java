package by.fpmi.bsu.pianolane;

import by.fpmi.bsu.pianolane.observer.NoteDeleteObserver;
import by.fpmi.bsu.pianolane.observer.NoteResizedObserver;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class MidiPlayer implements NoteDeleteObserver, NoteResizedObserver {

    private static final AtomicInteger NOTES_SEQUENCE = new AtomicInteger(0);
    private static final AtomicInteger CHANNEL_SEQUENCE = new AtomicInteger(0);
    private static final Map<Integer, NoteEvent> NOTE_EVENTS = new ConcurrentHashMap<>();

    public static Sequencer SEQUENCER;
    public static Sequence SEQUENCE;
    public static Track TRACK;
    public static List<Integer> CHANNELS = new ArrayList<>();

    private float bpm = 120.0f;

    static {
        try {
            SEQUENCER = MidiSystem.getSequencer();
            SEQUENCE = new Sequence(Sequence.PPQ, 480);
            TRACK = SEQUENCE.createTrack();

            SEQUENCER.open();
            SEQUENCER.setSequence(SEQUENCE);
        } catch (MidiUnavailableException | InvalidMidiDataException e) {
            throw new RuntimeException(e);
        }
    }

    public static MidiPlayer getInstance() {
        return MidiPlayerInstance.INSTANCE;
    }

    private MidiPlayer() {}

    public void addChannel() {

    }

    public Integer addNote(int midiNote, int startTick, int noteDuration) {
        try {
            NoteEvent noteEvent = new NoteEvent(TRACK, midiNote, startTick, noteDuration);
            Integer key = NOTES_SEQUENCE.getAndIncrement();
            NOTE_EVENTS.put(key, noteEvent);
            System.out.println("Note with key " + key + " added to MidiPlayer");
            return key;
        } catch (InvalidMidiDataException e) {
            throw new RuntimeException(e);
        }
    }

    public void play() {
        if (SEQUENCER.isRunning()) {
            SEQUENCER.stop();
        }
        SEQUENCER.setTickPosition(0);
        SEQUENCER.setTempoInBPM(bpm);
        SEQUENCER.start();
    }

    public void stop() {
        SEQUENCER.stop();
        SEQUENCER.setTickPosition(0);
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

    public void setBpm(float bpm) {
        this.bpm = bpm;
        SEQUENCER.setTempoInBPM(bpm);
    }

    @Override
    public void onNoteDeleted(Integer noteId) {
        System.out.println("Midi player tries to delete note " + noteId);
        deleteNote(noteId);
    }

    @Override
    public void onNoteResized(Integer noteId, int newLength) {
        System.out.println("Midi player tries to resize note " + noteId + " with length " + newLength);
        //TODO: get rid of magic numbers
        int midiLength = newLength / 50 * 480;
        resizeNote(noteId, midiLength);
    }

    private static class MidiPlayerInstance {
        private static final MidiPlayer INSTANCE = new MidiPlayer();
    }
}
