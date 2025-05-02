package by.fpmi.bsu.pianolane;

import by.fpmi.bsu.pianolane.model.ChannelCollection;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import static by.fpmi.bsu.pianolane.util.GlobalInstances.SEQUENCER;

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
