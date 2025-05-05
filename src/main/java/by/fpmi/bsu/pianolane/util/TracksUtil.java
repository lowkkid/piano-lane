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
public class TracksUtil {

    private static final int META_TYPE_MARKER = 6;

    public static Instrument getInstrumentForTrack(Track track) {
        Objects.requireNonNull(track);
        var shortMessage = (ShortMessage) findMessageInTrack(
                track, message -> message instanceof ShortMessage sm &&
                        sm.getCommand() == ShortMessage.PROGRAM_CHANGE);
        assert shortMessage != null;
        return InstrumentsUtil.getInstrumentById(shortMessage.getData1());
    }

    public static String getTrackId(Track track) {
        Objects.requireNonNull(track);
        var metaMessage = (MetaMessage) findMessageInTrack(
                track, message -> message instanceof MetaMessage mm && mm.getType() == META_TYPE_MARKER);
        assert metaMessage != null;
        return new String(metaMessage.getData(), StandardCharsets.UTF_8);
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
