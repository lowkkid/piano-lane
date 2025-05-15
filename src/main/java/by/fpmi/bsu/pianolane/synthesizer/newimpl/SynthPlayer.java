package by.fpmi.bsu.pianolane.synthesizer.newimpl;

import static by.fpmi.bsu.pianolane.synthesizer.newimpl.AudioDispatcherFactory.createAudioDispatcher;
import static by.fpmi.bsu.pianolane.synthesizer.newimpl.AudioDispatcherFactory.format;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.filters.BandPass;
import be.tarsos.dsp.filters.HighPass;
import be.tarsos.dsp.filters.LowPassFS;
import be.tarsos.dsp.filters.LowPassSP;
import be.tarsos.dsp.io.jvm.AudioPlayer;
import by.fpmi.bsu.pianolane.synthesizer.settings.SynthSettings;
import by.fpmi.bsu.pianolane.synthesizer.oscillator.model.Synth;
import java.util.ArrayList;
import java.util.List;
import javax.sound.sampled.LineUnavailableException;
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
        resolveFilterProcessor();
        try {
            dispatcher.addAudioProcessor(new AudioPlayer(format));
        } catch (LineUnavailableException e) {
            throw new RuntimeException(e);
        }
        dispatcherThread = new Thread(dispatcher);
        dispatcherThread.start();
        log.info("Started synth player thread: {}", dispatcherThread.getName());
    }

    private void resolveFilterProcessor() {
        var filterSettings = synthSettings.getFilterSettings();

        if (!filterSettings.isEnabled()) {
            return;
        }

        AudioProcessor filterProcessor;
        var frequency = (float) filterSettings.getFrequency();

        filterProcessor = switch (filterSettings.getFilterType()) {
            case LOW_PASS_SP -> new LowPassSP(frequency, 44100);
            case LOW_PASS_FS -> new LowPassFS(frequency, 44100);
            case HIGH_PASS -> new HighPass(frequency, 44100);
            case BAND_PASS -> new BandPass(frequency, frequency / (float) filterSettings.getQ(), 44100);
        };

        dispatcher.addAudioProcessor(filterProcessor);
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
