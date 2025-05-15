package by.fpmi.bsu.pianolane.midi.channel.model;

import static by.fpmi.bsu.pianolane.common.util.GlobalInstances.MIDI_CHANNELS;
import static by.fpmi.bsu.pianolane.common.util.GlobalInstances.SYNTHESIZER;
import static by.fpmi.bsu.pianolane.common.util.MathUtil.uiToMidiNoteLength;
import static by.fpmi.bsu.pianolane.common.util.MidiUtil.createTrackWithId;
import static by.fpmi.bsu.pianolane.common.util.MidiUtil.getTrackId;
import static by.fpmi.bsu.pianolane.common.Constants.MidiConstants.PAN_CONTROLLER;
import static by.fpmi.bsu.pianolane.common.Constants.MidiConstants.VOLUME_CONTROLLER;

import by.fpmi.bsu.pianolane.common.noteobserver.MidiNoteDeleteObserver;
import by.fpmi.bsu.pianolane.common.noteobserver.NoteResizedObserver;
import by.fpmi.bsu.pianolane.common.noteobserver.VelocityChangedObserver;
import by.fpmi.bsu.pianolane.midi.note.NoteEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.Track;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@Getter
@SuperBuilder
public abstract class Channel implements MidiNoteDeleteObserver, NoteResizedObserver, VelocityChangedObserver {

    private final AtomicInteger notesSequence;
    private final Map<Integer, NoteEvent> noteEvents;

    protected int channelId;
    protected MidiChannel original;
    protected Track track;
    protected boolean muted;
    protected boolean soloed;
    protected int volume; // 0-127
    protected int pan; // 0-127 (64 = center)

    public Channel(int channelId) {
        this.channelId = channelId;
        notesSequence = new AtomicInteger(0);
        noteEvents = new HashMap<>();
        original = MIDI_CHANNELS[channelId];
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
        var eventToDelete = noteEvents.get(key);
        if (eventToDelete == null) {
            log.warn("Tried to delete note event with key {}, but NoteEvent was not found for specified key", key);
            return;
        }
        noteEvents.remove(key);
        eventToDelete.destroy();
    }

    public void setMute(boolean muted) {
        log.info("Synthesizer class: {}", SYNTHESIZER.getClass().getName());
        log.info("Channel class: {}", original.getClass().getName());
        // Проверьте, поддерживает ли синтезатор нужные функции

        log.info("Setting mute to {} for channel with id {}", muted, channelId);
        this.muted = muted;
        log.info("Channel {} before mute change: active={}", channelId, original.getMute());
        original.setMute(muted);
        log.info("Channel {} after mute change: active={}", channelId, original.getMute());
        log.info("Channel {} before mute change: active={}", channelId, original.getController(7));
        //original.controlChange(7, 0);
        log.info("Channel {} after mute change: active={}", channelId, original.getController(7));
    }

    public void setVolume(int volume) {
        this.volume = volume;
            original.controlChange(VOLUME_CONTROLLER, volume);
    }

    public void setPan(int pan) {
        this.pan = pan;
        original.controlChange(PAN_CONTROLLER, pan);
    }

    public void setSoloed(boolean soloed) {
        this.soloed = soloed;
    }

    public void setTrack(Track track) {
        this.track = track;
        for (NoteEvent noteEvent : noteEvents.values()) {
            noteEvent.setTrack(track);
        }
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
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Channel channel = (Channel) o;
        return channelId == channel.channelId
                && Objects.equals(getTrackId(track), getTrackId(channel.track))
                && muted == channel.muted
                && soloed == channel.soloed
                && notesSequence.get() == channel.notesSequence.get()
                && Objects.equals(noteEvents, channel.noteEvents);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                notesSequence.get(),
                noteEvents,
                channelId,
                getTrackId(track),
                muted,
                soloed
        );
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{"
                + "noteSequence=" + notesSequence.get()
                + ", noteEvents=" + noteEvents
                + ", channelId=" + channelId
                + ", track=" + getTrackId(track)
                + ", muted=" + muted
                + ", soloed" + soloed;
    }

}
