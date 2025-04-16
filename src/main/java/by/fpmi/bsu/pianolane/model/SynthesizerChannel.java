package by.fpmi.bsu.pianolane.model;

import static by.fpmi.bsu.synthesizer.util.SynthesizerUtilCollections.createSynthSettings;

import by.fpmi.bsu.synthesizer.newimpl.SynthPlayer;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class SynthesizerChannel extends Channel {

    private SynthPlayer synthPlayer;

    public SynthesizerChannel(int channelId) {
        super(channelId);
        var settings = createSynthSettings(channelId);
        System.out.println("SETTINGS +========================================= " + settings);
        synthPlayer = new SynthPlayer(settings);
        System.out.println("CREATED SYNTH PLAYER = " + synthPlayer.toString());
    }
}
