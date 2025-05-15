package by.fpmi.bsu.pianolane.util;

import static by.fpmi.bsu.pianolane.util.GlobalInstances.SEQUENCE;
import static by.fpmi.bsu.pianolane.util.GlobalInstances.SEQUENCER;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.function.Predicate;
import javax.sound.midi.Instrument;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TracksUtil {

    private static final int META_TYPE_MARKER = 6;

    public static Instrument getInstrumentForTrack(Track track) {
        Objects.requireNonNull(track);
        var shortMessage = (ShortMessage) findMessageInTrack(
                track, message -> message instanceof ShortMessage sm &&
                        sm.getCommand() == ShortMessage.PROGRAM_CHANGE);
        assert shortMessage != null;
        return InstrumentsUtil.getInstrumentById(shortMessage.getData1());
    }

    public static int getInstrumentIdForTrack(Track track) {
        Objects.requireNonNull(track);
        var shortMessage = (ShortMessage) findMessageInTrack(
                track, message -> message instanceof ShortMessage sm &&
                        sm.getCommand() == ShortMessage.PROGRAM_CHANGE);
        assert shortMessage != null;
        return shortMessage.getData1();
    }


    public static Integer getTrackId(Track track) {
        if (track == null) {
            return null;
        }
        var metaMessage = (MetaMessage) findMessageInTrack(
                track, message -> message instanceof MetaMessage mm && mm.getType() == META_TYPE_MARKER);
        assert metaMessage != null;
        String id = new String(metaMessage.getData(), StandardCharsets.UTF_8);
        return Integer.parseInt(id);
    }

    public static MidiEvent isMidiEventExistsInTrack(Track track, MidiEvent desired) {
        for (int i = 0; i < track.size(); i++) {
            MidiEvent existing = track.get(i);
            MidiMessage existingMessage = existing.getMessage();
            MidiMessage desiredMessage = desired.getMessage();
            long tick = existing.getTick();

            if (existingMessage instanceof ShortMessage em && desiredMessage instanceof ShortMessage dm) {
                if (tick == desired.getTick()
                        && em.getCommand() == dm.getCommand()
                        && em.getChannel() == dm.getChannel()
                        && em.getData1() == dm.getData1()
                        && em.getData2() == dm.getData2()) {
                    return existing;
                }
            } else if (existingMessage instanceof MetaMessage em && desiredMessage instanceof MetaMessage dm) {
                if (tick == desired.getTick() && em.getType() == dm.getType()) {
                    return existing;
                }
            }
        }
        return null;
    }

    private static MidiMessage findMessageInTrack(Track track,
                                     Predicate<MidiMessage> matcher) {
        for (int i = 0; i < track.size(); i++) {
            MidiMessage message = track.get(i).getMessage();
            if (matcher.test(message)) {
                return message;
            }
        }
        return null;
    }

    public static Track createTrackWithId(String trackId) {
        if (trackId == null || trackId.isEmpty()) {
            throw new IllegalArgumentException("Track ID cannot be null or empty");
        }
        Track track = createTrack();

        MetaMessage metaMessage = new MetaMessage();
        try {
            metaMessage.setMessage(META_TYPE_MARKER, trackId.getBytes(), trackId.length());
        } catch (InvalidMidiDataException e) {
            throw new RuntimeException(e);
        }
        track.add(new MidiEvent(metaMessage, 0));
        return track;
    }

    public static Track createTrack() {
        Track track = SEQUENCE.createTrack();
        try {
            SEQUENCER.open();
            SEQUENCER.setSequence(SEQUENCE);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return track;
    }

    public static void deleteTrack(Track track) {
        SEQUENCE.deleteTrack(track);
        try {
            SEQUENCER.setSequence(SEQUENCE);
        } catch (InvalidMidiDataException e) {
            throw new RuntimeException(e);
        }
    }
}
