package by.fpmi.bsu.pianolane.util;

import lombok.Data;

import javax.sound.midi.Instrument;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.Patch;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

import static by.fpmi.bsu.pianolane.util.GlobalInstances.SEQUENCE;
import static by.fpmi.bsu.pianolane.util.GlobalInstances.SYNTHESIZER;
import static by.fpmi.bsu.pianolane.util.GlobalInstances.createTrack;

@Data
public class Channel {
    private int channelId;
    private Track track;
    private Instrument instrument;
    private boolean muted;
    private boolean soloed;

    public Channel(int channelId, Instrument instrument) {
        this.channelId = channelId;
        this.instrument = instrument;
        track = createTrack();
        muted = false;
        soloed = false;

        linkInstrumentToChannel();
    }

    private void linkInstrumentToChannel() {
        SYNTHESIZER.loadInstrument(this.instrument);
        Patch patch = instrument.getPatch();

        try {
            ShortMessage programChange = new ShortMessage();
            programChange.setMessage(ShortMessage.PROGRAM_CHANGE, channelId, patch.getProgram(), 0);
            track.add(new MidiEvent(programChange, 0));
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
        }
    }
}
