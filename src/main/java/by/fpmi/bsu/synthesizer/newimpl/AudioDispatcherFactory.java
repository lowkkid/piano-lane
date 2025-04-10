package by.fpmi.bsu.synthesizer.newimpl;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.TarsosDSPAudioFormat;
import be.tarsos.dsp.io.jvm.AudioPlayer;
import lombok.extern.slf4j.Slf4j;

import javax.sound.sampled.LineUnavailableException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static by.fpmi.bsu.synthesizer.newimpl.Constants.SAMPLE_RATE;

@Slf4j
public class AudioDispatcherFactory {

    private static final int bufferSize = 512;
    private static final int bufferOverlap = 0;
    private static final TarsosDSPAudioFormat format = new TarsosDSPAudioFormat(
            SAMPLE_RATE,
            16,
            1,
            true,
            false
    );

    public static AudioDispatcher createAudioDispatcher(List<Voice> activeVoices) {
        AudioDispatcher dispatcher = new AudioDispatcher(
                new SilentAudioInputStream(format, bufferSize * 1000),
                bufferSize,
                bufferOverlap
        );
        dispatcher.addAudioProcessor(new AudioProcessor() {
            @Override
            public boolean process(AudioEvent event) {
                float[] buffer = event.getFloatBuffer();

                // reset buffer
                Arrays.fill(buffer, 0f);


                synchronized (activeVoices) {
                    Iterator<Voice> iterator = activeVoices.iterator();
                    while (iterator.hasNext()) {
                        Voice voice = iterator.next();
                        //todo: instead of 0.8 use "level" knob from ui
                        voice.fillBuffer(buffer, 0.8f);
                        if (voice.isFinished()) {
                            iterator.remove();
                        }
                    }
                }

                for (int i = 0; i < buffer.length; i++) {
                    buffer[i] = softClip(buffer[i]);
                }
                return true;
            }

            @Override
            public void processingFinished() {}
        });

        try {
            dispatcher.addAudioProcessor(new AudioPlayer(format));
        } catch (LineUnavailableException e) {
            throw new RuntimeException(e);
        }

        return dispatcher;
    }

    private static float softClip(float x) {
        return (float) Math.tanh(x);
    }
}
