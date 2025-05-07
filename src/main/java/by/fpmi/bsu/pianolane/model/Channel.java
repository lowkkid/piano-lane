package by.fpmi.bsu.pianolane.model;

import by.fpmi.bsu.pianolane.wrappers.NoteEvent;
import by.fpmi.bsu.pianolane.observer.MidiNoteDeleteObserver;
import by.fpmi.bsu.pianolane.observer.NoteResizedObserver;
import by.fpmi.bsu.pianolane.observer.VelocityChangedObserver;
import java.util.HashMap;
import java.util.Objects;
import lombok.Data;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.Track;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

import static by.fpmi.bsu.pianolane.util.MathUtil.uiToMidiNoteLength;
import static by.fpmi.bsu.pianolane.util.TracksUtil.createTrackWithId;

@Data
@Slf4j
@Getter
@SuperBuilder
@ToString(exclude = "track")
//TODO: add track for equals and hash code
public abstract class Channel implements MidiNoteDeleteObserver, NoteResizedObserver, VelocityChangedObserver {

    private final AtomicInteger notesSequence;
    private final Map<Integer, NoteEvent> noteEvents;

    protected int channelId;
    protected Track track;
    protected boolean muted;
    protected boolean soloed;

    public Channel(int channelId) {
        this.channelId = channelId;
        notesSequence = new AtomicInteger(0);
        noteEvents = new HashMap<>();
        track = createTrackWithId(String.valueOf(channelId));
        muted = false;
        soloed = false;
    }

    public Integer addNote(int midiNote, int startTick, int noteDuration) {
        try {
            NoteEvent noteEvent = new NoteEvent(track, channelId, midiNote, startTick, noteDuration);
            Integer key = notesSequence.getAndIncrement();
            noteEvents.put(key, noteEvent);
            return key;
        } catch (InvalidMidiDataException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteNote(Integer key) {
        NoteEvent eventToDelete = noteEvents.get(key);
        if (eventToDelete == null) {
            log.warn("Tried to delete note event with key {}, but NoteEvent was not found for specified key",key);
            return;
        }
        noteEvents.remove(key);
        eventToDelete.destroy();
    }

    @Override
    public void onNoteDeleted(Integer noteId) {
        log.debug("Deleting note {}", noteId);
        deleteNote(noteId);
    }

    @Override
    public void onNoteResized(Integer noteId, int newLength) {
        log.debug("Resizing note {} with new length {}", noteId, newLength);
        int midiLength = uiToMidiNoteLength(newLength);
        noteEvents.get(noteId).updateLength(midiLength);
    }

    @Override
    public void onVelocityChanged(Integer noteId, int newVelocity) {
        log.debug("Changing velocity for note with id {} to {}", noteId, newVelocity);
        noteEvents.get(noteId).updateVelocity(newVelocity);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Channel channel = (Channel) o;
        return channelId == channel.channelId &&
                muted == channel.muted &&
                soloed == channel.soloed &&
                notesSequence.get() == channel.notesSequence.get() &&
                Objects.equals(noteEvents, channel.noteEvents);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                notesSequence.get(),
                noteEvents,
                channelId,
                muted,
                soloed
        );
    }
}
