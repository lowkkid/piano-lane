package by.fpmi.bsu.pianolane.ui.pianoroll;

import by.fpmi.bsu.pianolane.observer.MidiNoteDeleteObserver;
import java.io.Serializable;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class MidiNoteContainer implements Serializable {

    private final Map<Integer, List<MidiNote>> notesByChannel = new HashMap<>();

    public static MidiNoteContainer getInstance() {
        return MidiNoteContainerHolder.INSTANCE;
    }

    public void addNote(Integer channelId, MidiNote note) {
        List<MidiNote> currentNotes = notesByChannel.getOrDefault(channelId, new ArrayList<>());
        currentNotes.add(note);
        notesByChannel.put(channelId, currentNotes);
        log.info("Added Note to MidiNoteContainer with key {}", channelId);
    }

    public void removeNote(Integer channelId, MidiNote note) {
        notesByChannel.get(channelId).remove(note);
    }

    public void removeAllNotesForChanel(Integer channelId) {
        notesByChannel.remove(channelId);
    }

    public List<MidiNote> getAllNotesForChannel(Integer channelId) {
        return notesByChannel.getOrDefault(channelId, List.of());
    }

    private MidiNoteContainer() {}

    private static class MidiNoteContainerHolder {
        private static final MidiNoteContainer INSTANCE = new MidiNoteContainer();
    }
}
