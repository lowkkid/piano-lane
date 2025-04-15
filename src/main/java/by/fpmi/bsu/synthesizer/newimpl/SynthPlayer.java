package by.fpmi.bsu.synthesizer.newimpl;

import be.tarsos.dsp.AudioDispatcher;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import static by.fpmi.bsu.synthesizer.newimpl.AudioDispatcherFactory.createAudioDispatcher;

@Getter
@Setter
@NoArgsConstructor
@Slf4j
public class SynthPlayer {

    private final List<Voice> activeVoices = new ArrayList<>();
    private AudioDispatcher dispatcher;
    private Thread dispatcherThread;
    private Waveform waveform = Waveform.TRIANGLE;

    public void start() {
        dispatcher = createAudioDispatcher(activeVoices);
        dispatcherThread = new Thread(dispatcher);
        dispatcherThread.start();
        log.info("Started synth player thread: {}", dispatcherThread.getName());
    }

    public void stop() {
        if (dispatcher != null) {
            stopVoices();
            dispatcher.stop();
        }
        if (dispatcherThread != null && dispatcherThread.isAlive()) {
            try {
                log.info("Waiting for dispatcher thread {} to stop", dispatcherThread.getName());
                dispatcherThread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        if (dispatcherThread != null) {
            log.info("Stopped synth player thread: {}", dispatcherThread.getName());

        }
    }

    public Voice addVoice(double frequency, float velocity) {
        Voice voice = new Voice(frequency, velocity, waveform);
        synchronized (activeVoices) {
            activeVoices.add(voice);
        }
        return voice;
    }

    public void updateWaveForm(Waveform waveform) {
        this.waveform = waveform;
        activeVoices.forEach(voice -> voice.setWaveform(waveform));
    }

    private void stopVoices() {
        synchronized (activeVoices) {
            activeVoices.forEach(Voice::noteOff);
        }

        while (activeVoices.stream().anyMatch(voice -> !voice.isFinished())) {
            try {
                log.info("Can't stop synth thread, because it still has unfinished notes.");
                Thread.sleep(50);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
