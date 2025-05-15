package by.fpmi.bsu.pianolane.pianoroll;

import by.fpmi.bsu.pianolane.pianoroll.components.NoteWithVelocity;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class MidiNoteContainer implements Serializable {

    private final Map<Integer, List<NoteWithVelocity>> notesByChannel = new HashMap<>();

    public static MidiNoteContainer getInstance() {
        return MidiNoteContainerHolder.INSTANCE;
    }

    public void addNote(Integer channelId, NoteWithVelocity note) {
        List<NoteWithVelocity> currentNotes = notesByChannel.getOrDefault(channelId, new ArrayList<>());
        currentNotes.add(note);
        notesByChannel.put(channelId, currentNotes);
        log.info("Added Note to MidiNoteContainer with key {}", channelId);
    }

    public void removeNote(Integer channelId, NoteWithVelocity note) {
        notesByChannel.get(channelId).remove(note);
    }

    public void removeAllNotesForChanel(Integer channelId) {
        notesByChannel.remove(channelId);
    }

    public List<NoteWithVelocity> getAllNotesForChannel(Integer channelId) {
        return notesByChannel.getOrDefault(channelId, List.of());
    }

    private MidiNoteContainer() {}

    private static class MidiNoteContainerHolder {
        private static final MidiNoteContainer INSTANCE = new MidiNoteContainer();
    }
}
