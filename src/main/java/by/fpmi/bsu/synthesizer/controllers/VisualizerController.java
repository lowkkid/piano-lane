package by.fpmi.bsu.synthesizer.controllers;

import by.fpmi.bsu.synthesizer.listener.MagnitudeListener;
import by.fpmi.bsu.synthesizer.setting.VisualizationSettings;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.springframework.stereotype.Component;

@Component
public class VisualizerController implements MagnitudeListener {

    @FXML
    private Canvas visualizerCanvas;

    private VisualizationSettings visualizationSettings = VisualizationSettings.getInstance();
    private final double[] magnitudes = new double[visualizationSettings.getNumBands()];
    private final double[] smoothedMagnitudes = new double[visualizationSettings.getNumBands()];
    private GraphicsContext gc;
    private static VisualizerController controller;

    AnimationTimer timer;

    public static VisualizerController getInstance() {
        return controller;
    }

    public void initialize() {
        gc = visualizerCanvas.getGraphicsContext2D();
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                double[] displayMagnitudes = new double[visualizationSettings.getNumBands()];
                synchronized (magnitudes) {
                    System.arraycopy(smoothedMagnitudes, 0, displayMagnitudes, 0, visualizationSettings.getNumBands());
                }
                draw(displayMagnitudes);
            }
        };
        controller = this;

        startAnimation();
    }

    /**
     * Обновление амплитуд при получении новых данных от AudioGenerator.
     */
    @Override
    public void startDrawing(double[] newMagnitudes) {
        synchronized (magnitudes) {
            for (int i = 0; i < visualizationSettings.getNumBands(); i++) {
                smoothedMagnitudes[i] = visualizationSettings.getSmoothingFactor() * smoothedMagnitudes[i] + (1 - visualizationSettings.getSmoothingFactor()) * newMagnitudes[i];
            }
        }
    }

    @Override
    public void stopDrawing() {
//        Arrays.fill(magnitudes, 0.0);
//        Arrays.fill(smoothedMagnitudes, 0.0);
    }

    /**
     * Запуск анимации визуализатора с использованием AnimationTimer для плавного обновления.
     */
    private void startAnimation() {
        timer.start();
    }

    /**
     * Отрисовка визуализации на Canvas.
     *
     * @param mags Массив амплитуд частотных полос.
     */
    private void draw(double[] mags) {
        double width = visualizerCanvas.getWidth();
        double height = visualizerCanvas.getHeight();

        clear();

        double barWidth = width / visualizationSettings.getNumBands();

        // Рисование градиентных полос
        for (int i = 0; i < visualizationSettings.getNumBands(); i++) {
            double magnitude = mags[i];
            double logMagnitude = (magnitude != 0 ? Math.log10(magnitude) : 0) / 2;
            double barHeight = logMagnitude * height;
            if (barHeight > height) {
                barHeight = height;
            }

            double ratio = (double) i / visualizationSettings.getNumBands();
            Color color = Color.RED.interpolate(Color.YELLOW, ratio).deriveColor(0, 1, 1, 0.8);

            gc.setFill(color);

            // Рисуем тонкие линии вместо широких прямоугольников
            gc.fillRect(i * barWidth + barWidth * 0.4, height - barHeight, barWidth, barHeight);
        }

        // Рисование обозначений частот
        gc.setFill(Color.WHITE);
        gc.setFont(new Font(12));
        gc.setStroke(Color.GRAY);
        gc.setLineWidth(1);

//        double[] octaveFrequencies = {20, 60, 250, 500, 1000, 2000, 4000, 8000, 16000};
//        for (double freq : octaveFrequencies) {
//            //TODO use constants
//            double x = frequencyToX(freq, 1024, 44100, 2048, width);
//            if (x >= 0 && x <= width) {
//                gc.setStroke(Color.GRAY);
//                gc.strokeLine(x, 0, x, height);
//                gc.setFill(Color.WHITE);
//                gc.fillText(String.format("%.0f Hz", freq), x - 20, height - 10);
//            }
//        }
    }


    private double frequencyToX(double freq, int numBands, int sampleRate, int n, double canvasWidth) {
        double logMin = Math.log10(20); // Минимальная частота для визуализации
        double logMax = Math.log10(sampleRate / 2); // Максимальная частота (Nyquist)
        double logFreq = Math.log10(freq);
        double ratio = (logFreq - logMin) / (logMax - logMin);
        return ratio * canvasWidth;
    }

    public void clear() {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, visualizerCanvas.getWidth(), visualizerCanvas.getHeight());
    }
}
