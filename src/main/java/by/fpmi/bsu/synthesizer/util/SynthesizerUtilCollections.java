package by.fpmi.bsu.synthesizer.util;

import by.fpmi.bsu.synthesizer.settings.SynthSettings;
import java.util.HashMap;
import java.util.Map;

public class SynthesizerUtilCollections {

    public static final Map<Integer, SynthSettings> SYNTH_SETTINGS_BY_CHANNEL = new HashMap<>();

    public static SynthSettings createSynthSettings(Integer channelId) {
        if (SYNTH_SETTINGS_BY_CHANNEL.containsKey(channelId)) {
            throw new IllegalArgumentException("Synth settings already exists for this channel");
        }

        SynthSettings synthSettings = new SynthSettings();
        SYNTH_SETTINGS_BY_CHANNEL.put(channelId, synthSettings);
        return synthSettings;
    }

    public static SynthSettings getSynthSettings(Integer channelId) {
        return SYNTH_SETTINGS_BY_CHANNEL.get(channelId);
    }

}
