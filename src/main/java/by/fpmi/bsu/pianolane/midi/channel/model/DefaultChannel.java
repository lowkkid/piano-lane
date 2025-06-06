package by.fpmi.bsu.pianolane.midi.channel.model;

import java.util.Objects;
import javax.sound.midi.MidiChannel;
import lombok.Getter;
import lombok.Setter;

import javax.sound.midi.Instrument;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.Patch;
import javax.sound.midi.ShortMessage;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import static by.fpmi.bsu.pianolane.common.Constants.MidiConstants.PAN_CONTROLLER;
import static by.fpmi.bsu.pianolane.common.Constants.MidiConstants.VOLUME_CONTROLLER;
import static by.fpmi.bsu.pianolane.common.util.GlobalInstances.MIDI_CHANNELS;
import static by.fpmi.bsu.pianolane.common.util.GlobalInstances.SYNTHESIZER;

@Getter
@Setter
@SuperBuilder
@ToString(callSuper = true)
public class DefaultChannel extends Channel {

    private Instrument instrument;
    protected MidiChannel original;

    public DefaultChannel(int channelId, Instrument instrument) {
        super(channelId);
        this.instrument = instrument;
        original = MIDI_CHANNELS[channelId];
        linkInstrumentToChannel();
    }

    public void setMute(boolean muted) {
        this.muted = muted;
        original.setMute(muted);
    }

    public void setVolume(int volume) {
        this.volume = volume;
        original.controlChange(VOLUME_CONTROLLER, volume);
    }

    public void setPan(int pan) {
        this.pan = pan;
        original.controlChange(PAN_CONTROLLER, pan);
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

    private void linkInstrumentToChannel() {
        SYNTHESIZER.loadInstrument(this.instrument);
        Patch patch = instrument.getPatch();

        try {
            original.programChange(patch.getBank(), patch.getProgram());
            ShortMessage programChange = new ShortMessage();
            programChange.setMessage(ShortMessage.PROGRAM_CHANGE, channelId, patch.getProgram(), 0);
            track.add(new MidiEvent(programChange, 0));
        } catch (InvalidMidiDataException e) {
            throw new RuntimeException(e);
        }
    }
}
