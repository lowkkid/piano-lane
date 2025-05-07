package by.fpmi.bsu.pianolane.wrappers;

import static by.fpmi.bsu.pianolane.util.constants.DefaultValues.DEFAULT_VELOCITY_VALUE;
import static by.fpmi.bsu.pianolane.util.LogUtil.getAllTrackEvents;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.Track;

@Slf4j
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@ToString(exclude = "track")
@EqualsAndHashCode(exclude = "track")
//TODO: add track for equals and hash code
public class NoteEvent {

    private NoteMidiEvent noteOnEvent;
    private NoteMidiEvent noteOffEvent;
    @Setter
    private Track track;

    public NoteEvent(Track track, int channelId, int midiNote, int startTick, int noteDuration) throws InvalidMidiDataException {
        this.track = track;

        noteOnEvent = NoteMidiEvent.builder()
                .channelId(channelId)
                .midiNote(midiNote)
                .tick(startTick)
                .velocity(DEFAULT_VELOCITY_VALUE)
                .noteOn()
                .build();

        noteOffEvent = NoteMidiEvent.builder()
                .channelId(channelId)
                .midiNote(midiNote)
                .tick(startTick + noteDuration)
                .noteOff()
                .build();

        registerNoteEvent();
        log.debug("Registered note event for channel {}. Current events in this track are:\n {} ", channelId, getAllTrackEvents(track));

    }

    public void updateLength(int newLength) {
        track.remove(noteOffEvent.midiEvent());
        noteOffEvent.transfer(noteOnEvent.getTick() + newLength);
        track.add(noteOffEvent.midiEvent());
        log.debug("Resized note event. Current events in this track are:\n {} ", getAllTrackEvents(track));
    }

    public void updateVelocity(int newVelocity) {
        track.remove(noteOnEvent.midiEvent());
        noteOnEvent.updateVelocity(newVelocity);
        track.add(noteOnEvent.midiEvent());
    }

    public void destroy() {
        track.remove(noteOnEvent.midiEvent());
        track.remove(noteOffEvent.midiEvent());
    }

    private void registerNoteEvent() {
        track.add(noteOnEvent.midiEvent());
        track.add(noteOffEvent.midiEvent());
    }
}
