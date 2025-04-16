package by.fpmi.bsu.synthesizer.newimpl;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.TarsosDSPAudioFormat;
import be.tarsos.dsp.io.jvm.AudioPlayer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static by.fpmi.bsu.synthesizer.newimpl.Constants.SAMPLE_RATE;

public class SynthTest {

    public static void main(String[] args) throws Exception {
//        int bufferSize = 512;
//        int bufferOverlap = 0;
//
//        TarsosDSPAudioFormat format = new TarsosDSPAudioFormat(
//                SAMPLE_RATE,
//                16,
//                1,
//                true,
//                false
//        );
//
//        AudioDispatcher dispatcher = new AudioDispatcher(
//                new SilentAudioInputStream(format, bufferSize * 1000),
//                bufferSize,
//                bufferOverlap
//        );
//
//        List<Voice> activeVoices = new ArrayList<>();
//
//
//        dispatcher.addAudioProcessor(new AudioProcessor() {
//            @Override
//            public boolean process(AudioEvent event) {
//                float[] buffer = event.getFloatBuffer();
//
//                // Обнуляем буфер
//                for (int i = 0; i < buffer.length; i++) buffer[i] = 0f;
//
//
//                Iterator<Voice> iterator = activeVoices.iterator();
//                while (iterator.hasNext()) {
//                    Voice voice = iterator.next();
//                    voice.fillBuffer(buffer, 1);
//                    if (voice.isFinished()) {
//                        iterator.remove();
//                    }
//                }
//
//                // Применяем soft-clipping для защиты от перегруза
//                for (int i = 0; i < buffer.length; i++) {
//                    buffer[i] = softClip(buffer[i]);
//                }
//
//                return true;
//            }
//
//            @Override
//            public void processingFinished() {}
//        });
//
//        dispatcher.addAudioProcessor(new AudioPlayer(format));
//        Thread main = new Thread(dispatcher);
//        main.start();
//        Thread.sleep(4000);
//        activeVoices.get(0).noteOff();

    }

    // Мягкий ограничитель (soft clipper)
    private static float softClip(float x) {
        // Альтернатива: tanh-клиппер (звучит мягко)
        return (float) Math.tanh(x);
    }
}
