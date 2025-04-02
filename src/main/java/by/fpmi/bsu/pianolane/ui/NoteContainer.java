package by.fpmi.bsu.pianolane.ui;

import by.fpmi.bsu.pianolane.Note;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class NoteContainer {

    private static final Map<Integer, List<Note>> notesByChannel = new HashMap<>();

    public static void addNote(Integer channelId, Note note) {
        List<Note> currentNotes = notesByChannel.get(channelId);
        if (currentNotes == null) {
            currentNotes = new ArrayList<>();
        }
        currentNotes.add(note);
        notesByChannel.put(channelId, currentNotes);
    }

    public static void removeNotesInChanel(Integer channelId) {
        notesByChannel.remove(channelId);
    }

    public static List<Note> getNotesForChannel(Integer channelId) {
        return notesByChannel.getOrDefault(channelId, List.of());
    }
}
