package by.fpmi.bsu.pianolane.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.sound.midi.Instrument;
import javax.sound.midi.Track;

import static by.fpmi.bsu.pianolane.util.GlobalInstances.deleteTrack;

@Component
@Slf4j
public class ChannelCollection {

    private static final Channel[] channels = new Channel[16];

    /**
     * @param instrument instrument for new channel
     * @return id of created Channel
     */
    public int addChannel(Instrument instrument) {
        for (int i = 0; i < 16; i++) {
            if (channels[i] == null) {
                channels[i] = new Channel(i, instrument, false);
                log.info("Added channel: {}", channels[i]);
                return i;
            }
        }
        throw new IllegalStateException("No free MIDI channels available");
    }

    public int addCustomChannel() {
        for (int i = 0; i < 16; i++) {
            if (channels[i] == null) {
                channels[i] = new Channel(i, null, true);
                log.info("Added custom channel: {}", channels[i]);
                return i;
            }
        }
        throw new IllegalStateException("No free MIDI channels available");
    }

    public void removeChannel(int channelId) {
        Track trackToDelete = channels[channelId].getTrack();
        deleteTrack(trackToDelete);
        channels[channelId] = null;
    }

    public Channel getChannel(int channelId) {
        return channels[channelId];
    }

    public static boolean isCustom(int channelId) {
        return channels[channelId] != null && channels[channelId].isCustom();
    }
}
