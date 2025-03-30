package by.fpmi.bsu.pianolane.observer;

import by.fpmi.bsu.pianolane.Note;

public interface NoteResizedObserver {

    void onNoteResized(Integer noteId, int newLength);
}
