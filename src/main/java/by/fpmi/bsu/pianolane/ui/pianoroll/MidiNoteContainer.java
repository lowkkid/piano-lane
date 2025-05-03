package by.fpmi.bsu.pianolane.ui.pianoroll;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class MidiNoteContainer {

    private static final Map<Integer, List<MidiNote>> notesByChannel = new HashMap<>();

    public static void addNote(Integer channelId, MidiNote note) {
        List<MidiNote> currentNotes = notesByChannel.getOrDefault(channelId, new ArrayList<>());
        currentNotes.add(note);
        notesByChannel.put(channelId, currentNotes);
        log.info("Added Note to MidiNoteContainer with key {}", channelId);
    }

    public static void removeNote(Integer channelId, MidiNote note) {
        notesByChannel.get(channelId).remove(note);
    }

    public static void removeAllNotesForChanel(Integer channelId) {
        notesByChannel.remove(channelId);
    }

    public static List<MidiNote> getAllNotesForChannel(Integer channelId) {
        return notesByChannel.getOrDefault(channelId, List.of());
    }
}
