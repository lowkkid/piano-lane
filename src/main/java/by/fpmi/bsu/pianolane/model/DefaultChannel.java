package by.fpmi.bsu.pianolane.model;

import lombok.Getter;
import lombok.Setter;

import javax.sound.midi.Instrument;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.Patch;
import javax.sound.midi.ShortMessage;
import lombok.experimental.SuperBuilder;

import static by.fpmi.bsu.pianolane.util.GlobalInstances.SYNTHESIZER;

@Getter
@Setter
@SuperBuilder
public class DefaultChannel extends Channel {

    private Instrument instrument;

    public DefaultChannel(int channelId, Instrument instrument) {
        super(channelId);
        this.instrument = instrument;
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
