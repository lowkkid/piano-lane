package by.fpmi.bsu.pianolane.synthesizer.newimpl;

import static by.fpmi.bsu.pianolane.common.Constants.SAMPLE_RATE;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.TarsosDSPAudioFormat;
import by.fpmi.bsu.pianolane.synthesizer.oscillator.model.Synth;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AudioDispatcherFactory {

    private static final int bufferSize = 512;
    private static final int bufferOverlap = 0;
    public static final TarsosDSPAudioFormat format = new TarsosDSPAudioFormat(
            SAMPLE_RATE,
            16,
            1,
            true,
            false
    );

    public static AudioDispatcher createAudioDispatcher(List<Synth> activeSynths) {
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


                synchronized (activeSynths) {
                    Iterator<Synth> iterator = activeSynths.iterator();
                    while (iterator.hasNext()) {
                        Synth synth = iterator.next();
                        synth.fillBuffer(buffer);
                        if (synth.isFinished()) {
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
        return dispatcher;
    }

    private static float softClip(float x) {
        return (float) Math.tanh(x);
    }
}
