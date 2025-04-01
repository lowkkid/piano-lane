package by.fpmi.bsu.synthesizer.models;

import by.fpmi.bsu.synthesizer.setting.SoundCommonSettings;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WaveformGenerator {

    private WaveformType waveformType;
    private double frequency;
    private double sampleRate;
    private SoundCommonSettings soundCommonSettings = SoundCommonSettings.getInstance();

    public WaveformGenerator() {
        this.waveformType = WaveformType.SINE;
        this.frequency = 440.0;
        this.sampleRate = 44100.0;
    }

    private double generateWaveformSample(double time) {
        double angularFrequency = 2 * Math.PI * frequency;
        return switch (waveformType) {
            case SINE -> Math.sin(angularFrequency * time);
            case SQUARE -> Math.signum(Math.sin(angularFrequency * time));
            case SAWTOOTH -> 2 * (time * frequency - Math.floor(0.5 + time * frequency));
            case TRIANGLE -> 2 * Math.abs(2 * (time * frequency - Math.floor(time * frequency + 0.5))) - 1;
        };
    }

    public void drawWaveform(GraphicsContext gc, double width, double height, int samplesToDraw) {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, width, height);

        gc.setStroke(Color.WHITE);
        gc.setLineWidth(2);

        double[] samples = new double[samplesToDraw];
        double centerY = height / 2;

        for (int x = 0; x < samplesToDraw; x++) {
            double time = x / sampleRate;
            samples[x] = soundCommonSettings.getAmplitude() / 100 * generateWaveformSample(time);
        }

        for (int x = 1; x < samplesToDraw - 1; x++) {
            double y1 = centerY - samples[x - 1] * centerY;
            double y2 = centerY - samples[x] * centerY;
            double y3 = centerY - samples[x + 1] * centerY;

            gc.beginPath();
            gc.moveTo(x - 1, y1);
            gc.quadraticCurveTo(x, y2, x + 1, y3);
            gc.stroke();
        }
    }
}
