package by.fpmi.bsu.pianolane.model;

import static by.fpmi.bsu.pianolane.util.TracksUtil.deleteTrack;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.sound.midi.Instrument;
import javax.sound.midi.Track;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
@Builder
@AllArgsConstructor
@EqualsAndHashCode(exclude = "executorService")
@ToString(exclude = "executorService")
public class ChannelCollection {

    public static ChannelCollection getInstance() {
        return ChannelCollectionHolder.INSTANCE;
    }

    private final Channel[] channels;
    @Builder.Default
    private int synthesizersThreadPoolCount = 0;

    private ExecutorService executorService;

    public void resetFrom(ChannelCollection other) {
        System.arraycopy(other.channels, 0, this.channels, 0, this.channels.length);
        this.synthesizersThreadPoolCount = other.synthesizersThreadPoolCount;
        this.executorService = other.executorService;
    }

    public int addDefaultChannel(Instrument instrument) {
        for (int i = 0; i < 16; i++) {
            if (channels[i] == null) {
                channels[i] = new DefaultChannel(i, instrument);
                log.info("Added default channel: {}", channels[i]);
                return i;
            }
        }
        throw new IllegalStateException("No free MIDI channels available");
    }

    public int addSynthesizerChannel() {
        for (int i = 0; i < 16; i++) {
            if (channels[i] == null) {
                channels[i] = new SynthesizerChannel(i);
                log.info("Added synthesizer channel: {}", channels[i]);
                executorService = Executors.newFixedThreadPool(++synthesizersThreadPoolCount);
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

    public boolean isSynthesizer(int channelId) {
        return channels[channelId] != null && channels[channelId] instanceof SynthesizerChannel;
    }

    public void startSynthesizerChannels() {
        log.info("Starting synthesizers threads");
        Arrays.stream(channels)
                .filter(channel -> channel instanceof SynthesizerChannel)
                .map(channel -> (SynthesizerChannel) channel)
                .forEach(synthChannel -> synthChannel.getSynthPlayer().start());
    }

    public void stopSynthesizerChannels() {
        Arrays.stream(channels)
                .filter(channel -> channel instanceof SynthesizerChannel)
                .map(channel -> (SynthesizerChannel) channel)
                .forEach(synthChannel -> executorService.submit(() -> {
                    log.info("Stopping synthesizer from channel {} in thread {}",
                            synthChannel.getChannelId(), Thread.currentThread().getName());
                    synthChannel.getSynthPlayer().stop();
                }));
    }

    private ChannelCollection() {
        channels = new Channel[16];
    }

    private static class ChannelCollectionHolder {
        private static final ChannelCollection INSTANCE = new ChannelCollection();
    }
}
