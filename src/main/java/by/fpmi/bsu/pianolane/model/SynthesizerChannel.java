package by.fpmi.bsu.pianolane.model;

import static by.fpmi.bsu.synthesizer.util.SynthesizerUtilCollections.createSynthSettings;

import by.fpmi.bsu.synthesizer.newimpl.SynthPlayer;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@ToString(callSuper = true)
@SuperBuilder
public class SynthesizerChannel extends Channel {

    private SynthPlayer synthPlayer;

    public SynthesizerChannel(int channelId) {
        super(channelId);
        var settings = createSynthSettings(channelId);
        synthPlayer = new SynthPlayer(settings);
    }
}
