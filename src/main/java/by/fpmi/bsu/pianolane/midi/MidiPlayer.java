package by.fpmi.bsu.pianolane.midi;

import static by.fpmi.bsu.pianolane.common.util.GlobalInstances.SEQUENCER;

import by.fpmi.bsu.pianolane.midi.channel.ChannelCollection;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class MidiPlayer {

    private final ChannelCollection channelCollection = ChannelCollection.getInstance();
    private float bpm = 120.0f;

    public void play() {
        if (SEQUENCER.isRunning()) {
            SEQUENCER.stop();
        }
        channelCollection.startSynthesizerChannels();
        SEQUENCER.setTickPosition(0);
        SEQUENCER.setTempoInBPM(bpm);
        SEQUENCER.start();
    }

    public void stop() {
        channelCollection.stopSynthesizerChannels();
        SEQUENCER.stop();
        SEQUENCER.setTickPosition(0);
    }
}
