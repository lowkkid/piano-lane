package by.fpmi.bsu.pianolane.model;

import by.fpmi.bsu.synthesizer.newimpl.SynthPlayer;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SynthesizerChannel extends Channel {

    private SynthPlayer synthPlayer;

    public SynthesizerChannel(int channelId) {
        super(channelId);
        synthPlayer = new SynthPlayer();
    }
}
