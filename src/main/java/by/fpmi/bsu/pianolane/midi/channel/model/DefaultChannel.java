package by.fpmi.bsu.pianolane.midi.channel.model;

import java.util.Objects;
import lombok.Getter;
import lombok.Setter;

import javax.sound.midi.Instrument;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.Patch;
import javax.sound.midi.ShortMessage;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import static by.fpmi.bsu.pianolane.common.util.GlobalInstances.SYNTHESIZER;

@Getter
@Setter
@SuperBuilder
@ToString(callSuper = true)
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
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        DefaultChannel that = (DefaultChannel) o;

        return instrument != null && that.instrument != null ?
                Objects.equals(instrument.getName(), that.instrument.getName()) :
                instrument == that.instrument;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(),
                instrument != null ? instrument.getName() : null);
    }
}
