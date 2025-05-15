package by.fpmi.bsu.synthesizer.newimpl;

import static by.fpmi.bsu.synthesizer.newimpl.AudioDispatcherFactory.createAudioDispatcher;

import be.tarsos.dsp.AudioDispatcher;
import by.fpmi.bsu.synthesizer.model.Synth;
import by.fpmi.bsu.synthesizer.settings.SynthSettings;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;



@Getter
@Slf4j
public class SynthPlayer {

    private final List<Synth> activeSynths = new ArrayList<>();
    private AudioDispatcher dispatcher;
    private Thread dispatcherThread;
    private final SynthSettings synthSettings;

    public SynthPlayer(SynthSettings synthSettings) {
        this.synthSettings = synthSettings;
    }

    public void start() {
        dispatcher = createAudioDispatcher(activeSynths);
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

    public Synth addSynth(double frequency, float velocity) {
        Synth synth = new Synth(synthSettings, frequency, velocity);
        synchronized (activeSynths) {
            activeSynths.add(synth);
        }
        return synth;
    }

    private void stopVoices() {
        synchronized (activeSynths) {
            activeSynths.forEach(Synth::noteOff);
        }

        while (activeSynths.stream().anyMatch(synth -> !synth.isFinished())) {
            try {
                log.info("Can't stop synth thread, because it still has unfinished notes.");
                Thread.sleep(50);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
