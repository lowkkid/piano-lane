package by.fpmi.bsu.synthesizer;

import by.fpmi.bsu.synthesizer.filter.BiquadHighPassFilter;
import by.fpmi.bsu.synthesizer.filter.BiquadLowPassFilter;
import by.fpmi.bsu.synthesizer.listener.MagnitudeListener;
import by.fpmi.bsu.synthesizer.setting.FilterSettings;
import by.fpmi.bsu.synthesizer.setting.SoundCommonSettings;
import by.fpmi.bsu.synthesizer.setting.VisualizationSettings;
import lombok.Getter;
import lombok.Setter;
import org.jtransforms.fft.DoubleFFT_1D;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@Getter
@Setter
public class SoundGenerator {

    @Getter @Setter
    private double frequency;

    private final List<MagnitudeListener> listeners = new ArrayList<>();

    public void addMagnitudeListener(MagnitudeListener listener) {
        listeners.add(listener);
    }

    private void notifyListeners(double[] magnitudes) {
        for (MagnitudeListener listener : listeners) {
            listener.startDrawing(Arrays.copyOf(magnitudes, magnitudes.length));
        }
    }

    private SoundCommonSettings commonSettings = SoundCommonSettings.getInstance();

    private final FilterSettings filterSettings = FilterSettings.getInstance();

    private final VisualizationSettings visualizationSettings = VisualizationSettings.getInstance();

    private final int SAMPLE_RATE = 44100;

    private SourceDataLine line;

    @Getter
    private final Queue<double[]> fftQueue = new LinkedList<>();

    public static final int BUFFER_SIZE = 4096;
    private volatile double[] smoothedMagnitudes;
    private DoubleFFT_1D fft;


    private boolean isPlaying;
    public SoundGenerator(double frequency) {
        this.frequency = frequency;
        this.isPlaying = false;
        this.fft = new DoubleFFT_1D(BUFFER_SIZE / 2);
        this.smoothedMagnitudes = new double[visualizationSettings.getNumBands()];
    }

    private double getWaveformValue(double angle) {
        switch (commonSettings.getWaveformType()) {
            case SINE:
                return Math.sin(angle);
            case SQUARE:
                return Math.signum(Math.sin(angle));
            case SAWTOOTH:
                // Преобразование угла в диапазон [0, 2π]
                double normalizedAngle = angle % (2 * Math.PI);
                return (2.0 / Math.PI) * (normalizedAngle - Math.PI);
            case TRIANGLE:
                // Преобразование угла в диапазон [0, 2π]
                normalizedAngle = angle % (2 * Math.PI);
                return 2.0 * Math.abs((normalizedAngle / Math.PI) - Math.floor((normalizedAngle / Math.PI) + 0.5)) - 1.0;
            default:
                return 0.0;
        }
    }

    public void playSound() throws LineUnavailableException, IOException {
        if (isPlaying) return;

        AudioFormat format = new AudioFormat(SAMPLE_RATE, 16, 1, true, true);
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);

        // Открытие линии для воспроизведения
        line = (SourceDataLine) AudioSystem.getLine(info);
        line.open(format, SAMPLE_RATE);
        line.start();

        isPlaying = true;

        // Запуск потока воспроизведения
        Thread audioThread = new Thread(() -> {
            try {
                byte[] buffer = new byte[BUFFER_SIZE];
                double increment = 2 * Math.PI * frequency / SAMPLE_RATE;
                double angle = 0.0;

                BiquadLowPassFilter lowPassFilter = null;
                BiquadHighPassFilter highPassFilter = null;

                if (filterSettings.isLowPassEnabled()) {
                    lowPassFilter = new BiquadLowPassFilter(filterSettings.getLowPassCutoff(), SAMPLE_RATE, 1.0 / Math.sqrt(2));
                }

                if (filterSettings.isHighPassEnabled()) {
                    highPassFilter = new BiquadHighPassFilter(filterSettings.getHighPassCutoff(), SAMPLE_RATE, 1.0 / Math.sqrt(2));
                }

                while (isPlaying) {
                    for (int i = 0; i < buffer.length / 2; i++) {
                        // Получаем значение волны в зависимости от типа
                        double waveValue = getWaveformValue(angle);

                        // Преобразуем значение волны в 16-битный сэмпл
                        short sample = (short) (waveValue * (commonSettings.getAmplitude() / 100.0) * 32768);

                        // Записываем сэмпл в буфер в порядке Big Endian
                        buffer[2 * i] = (byte) ((sample >> 8) & 0xFF);    // старший байт
                        buffer[2 * i + 1] = (byte) (sample & 0xFF);       // младший байт

                        // Обновляем угол для следующего сэмпла
                        angle += increment;
                        if (angle > 2 * Math.PI) {
                            angle -= 2 * Math.PI;
                        }
                    }

                    short[] samples = bytesToShorts(buffer);

                    double[] samplesDouble = new double[samples.length];
                    for (int i = 0; i < samples.length; i++) {
                        samplesDouble[i] = samples[i] / 32768.0; // Нормализация в диапазон [-1, 1]
                    }

                    if (lowPassFilter != null) {
                        samplesDouble = lowPassFilter.filter(samplesDouble);
                    }

                    if (highPassFilter != null) {
                        samplesDouble = highPassFilter.filter(samplesDouble);
                    }

                    short[] filteredSamples = new short[samplesDouble.length];
                    for (int i = 0; i < samplesDouble.length; i++) {
                        filteredSamples[i] = (short) Math.max(Math.min(samplesDouble[i] * 32767.0, Short.MAX_VALUE), Short.MIN_VALUE);
                    }

                    for (int i = 0; i < filteredSamples.length; i++) {
                        buffer[2 * i] = (byte) ((filteredSamples[i] >> 8) & 0xFF);    // старший байт
                        buffer[2 * i + 1] = (byte) (filteredSamples[i] & 0xFF);       // младший байт
                    }

                    // Преобразуем обратно в byte[] с учетом big endian
                    byte[] filteredBuffer = shortsToBytes(samples);

                    line.write(buffer, 0, buffer.length);

                    // Конвертация байтов в массив double для FFT
                    double[] audioData = new double[BUFFER_SIZE / 2];
                    for (int i = 0; i < audioData.length; i++) {
                        int high = buffer[2 * i];
                        int low = buffer[2 * i + 1];
                        short sample = (short) ((high << 8) | (low & 0xFF));
                        audioData[i] = sample / 32768.0; // Нормализация
                    }

                    double[] fftData = Arrays.copyOf(audioData, BUFFER_SIZE / 2); // Размер fftData = 2048
                    fft.realForward(fftData);

                    // Вычисление амплитуд частот
                    double[] currentMagnitudes = computeMagnitudes(fftData, BUFFER_SIZE / 2, visualizationSettings.getNumBands());

                    // Обновление сглаженных амплитуд
                    updateSmoothedMagnitudes(currentMagnitudes);

                    notifyListeners(smoothedMagnitudes);
                }

                line.drain();
                line.stop();
                line.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        audioThread.setDaemon(true);
        audioThread.start();
    }

    // Метод для остановки звука
    public void stopSound() {
        if (!isPlaying) return; // Если звук не воспроизводится, ничего не делаем

        isPlaying = false;
        if (line != null && line.isOpen()) {
            line.stop();
            line.close();
        }
        for (var x : listeners) {
            x.stopDrawing();
        }
    }

    private double[] computeMagnitudes(double[] fftData, int n, int numBands) {
        double[] magnitudes = new double[numBands];
        int bandSize = (n / 2) / numBands; // Например, 1024 / 64 = 16

        for (int band = 0; band < numBands; band++) {
            double sum = 0.0;
            int start = band * bandSize;
            int end = (band + 1) * bandSize;
            if (band == numBands - 1) {
                end = n / 2; // Последняя полоса включает все оставшиеся частоты
            }
            for (int k = start; k < end; k++) {
                double real, imag;
                if (k == 0) {
                    real = fftData[0];
                    imag = 0.0;
                } else {
                    real = fftData[2 * k];
                    imag = fftData[2 * k + 1];
                }
                sum += Math.sqrt(real * real + imag * imag);
            }
            magnitudes[band] = sum / (end - start);
        }

        return magnitudes;
    }

    /**
     * Обновляет сглаженные амплитуды с использованием фактора затухания.
     */
    private void updateSmoothedMagnitudes(double[] currentMagnitudes) {
        for (int i = 0; i < smoothedMagnitudes.length; i++) {
            smoothedMagnitudes[i] = visualizationSettings.getSmoothingFactor() * smoothedMagnitudes[i] + (1 - visualizationSettings.getSmoothingFactor()) * currentMagnitudes[i];
        }
    }

    /**
     * Преобразует массив байт в массив short с учетом big endian порядка.
     *
     * @param bytes Массив байт.
     * @return Массив short.
     */
    private short[] bytesToShorts(byte[] bytes) {
        int shortLength = bytes.length / 2;
        short[] shorts = new short[shortLength];
        for (int i = 0; i < shortLength; i++) {
            int high = bytes[2 * i] & 0xFF;      // старший байт
            int low = bytes[2 * i + 1] & 0xFF;   // младший байт
            shorts[i] = (short) ((high << 8) | low);
        }
        return shorts;
    }


    /**
     * Преобразует массив short обратно в массив байт с учетом big endian порядка.
     *
     * @param shorts Массив short.
     * @return Массив байт.
     */
    public byte[] shortsToBytes(short[] shorts) {
        byte[] bytes = new byte[shorts.length * 2];
        for (int i = 0; i < shorts.length; i++) {
            bytes[2 * i] = (byte) ((shorts[i] >> 8) & 0xFF);    // старший байт
            bytes[2 * i + 1] = (byte) (shorts[i] & 0xFF);       // младший байт
        }
        return bytes;
    }
}
