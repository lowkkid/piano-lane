package by.fpmi.bsu.pianolane.util;

import by.fpmi.bsu.pianolane.model.Channel;
import by.fpmi.bsu.pianolane.model.DefaultChannel;
import by.fpmi.bsu.pianolane.model.SynthesizerChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.sound.midi.Instrument;
import javax.sound.midi.Track;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static by.fpmi.bsu.pianolane.util.GlobalInstances.deleteTrack;

@Component
@Slf4j
public class ChannelCollection {

    private static final Channel[] channels = new Channel[16];
    private static int SYNTHESIZER_THREAD_POOL_SIZE = 0;
    /**
     * This service only used when stopping synthesizer threads,
     * because it's need to be done asynchronously and independently of each other
     */
    private static ExecutorService executorService;

    /**
     * @param instrument instrument for new channel
     * @return id of created Channel
     */
    public static int addChannel(Instrument instrument) {
        for (int i = 0; i < 16; i++) {
            if (channels[i] == null) {
                channels[i] = new DefaultChannel(i, instrument);
                log.info("Added channel: {}", channels[i]);
                return i;
            }
        }
        throw new IllegalStateException("No free MIDI channels available");
    }

    public static int addSynthesizerChannel() {
        for (int i = 0; i < 16; i++) {
            if (channels[i] == null) {
                channels[i] = new SynthesizerChannel(i);
                log.info("Added synthesizer channel: {}", channels[i]);
                executorService = Executors.newFixedThreadPool(++SYNTHESIZER_THREAD_POOL_SIZE);
                return i;
            }
        }
        throw new IllegalStateException("No free MIDI channels available");
    }

    public static void removeChannel(int channelId) {
        Track trackToDelete = channels[channelId].getTrack();
        deleteTrack(trackToDelete);
        channels[channelId] = null;
    }

    public static Channel getChannel(int channelId) {
        return channels[channelId];
    }

    public static boolean isSynthesizer(int channelId) {
        return channels[channelId] != null && channels[channelId] instanceof SynthesizerChannel;
    }

    public static void startSynthesizerChannels() {
        log.info("Starting synthesizers threads");
        for (int i = 0; i < 16; i++) {
            if (channels[i] != null) {
                if (channels[i] instanceof SynthesizerChannel) {
                    ((SynthesizerChannel) channels[i]).getSynthPlayer().start();
                }
            }
        }
    }

    public static void stopSynthesizerChannels() {
        log.info("Stopping synthesizers threads");
        for (int i = 0; i < 16; i++) {
            final int channelIndex = i;
            if (channels[i] != null) {
                if (channels[i] instanceof SynthesizerChannel) {
                    executorService.submit(() -> {
                        SynthesizerChannel channel = (SynthesizerChannel) channels[channelIndex];
                        log.info("Stopping synthesizer from channel {} in thread {}", channelIndex, Thread.currentThread().getName());
                        channel.getSynthPlayer().stop();
                    });
                }
            }
        }
    }
}
