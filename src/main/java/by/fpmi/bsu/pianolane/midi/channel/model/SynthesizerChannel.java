package by.fpmi.bsu.pianolane.midi.channel.model;

import static by.fpmi.bsu.pianolane.synthesizer.SettingsContainer.createSynthSettings;

import by.fpmi.bsu.pianolane.synthesizer.newimpl.SynthPlayer;
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

    @Override
    public void setMute(boolean muted) {
        this.muted = muted;
        synthPlayer.getSynthSettings().setEnabled(!muted);
    }

    @Override
    public void setVolume(int volume) {

    }

    @Override
    public void setPan(int pan) {

    }
}
