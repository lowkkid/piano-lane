package by.fpmi.bsu.synthesizer.util;

import by.fpmi.bsu.synthesizer.models.WaveformType;

public class WaveGeneratorUtil {


    private WaveGeneratorUtil() {

    }

    public static double generateWaveSample(double time, double frequency, WaveformType waveformType) {
        double angularFrequency = 2 * Math.PI * frequency;
        return switch (waveformType) {
            case SINE -> Math.sin(angularFrequency * time);
            case SQUARE -> Math.signum(Math.sin(angularFrequency * time));
            case SAWTOOTH -> 2 * (time * frequency - Math.floor(0.5 + time * frequency));
            case TRIANGLE -> 2 * Math.abs(2 * (time * frequency - Math.floor(time * frequency + 0.5))) - 1;
        };
    }
}
