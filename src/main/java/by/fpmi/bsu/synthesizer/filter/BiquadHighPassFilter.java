package by.fpmi.bsu.synthesizer.filter;

/**
 * Implementation of a Biquad High-Pass Filter.
 * <p>
 * This class provides functionality to apply a high-pass Biquad filter
 * to audio signals or other sampled data. It extends the abstract
 * {@link BiquadFilter} class and defines the specific coefficient
 * calculations for high-pass filtering.
 * </p>
 */
public class BiquadHighPassFilter extends BiquadFilter {

    /**
     * Constructs a High-Pass Biquad filter with specified parameters.
     *
     * @param cutoffFrequency the cutoff frequency in Hertz.
     * @param sampleRate      the sampling rate in Hertz.
     * @param Q               the quality factor of the filter.
     */
    public BiquadHighPassFilter(double cutoffFrequency, double sampleRate, double Q) {
        super(cutoffFrequency, sampleRate, Q);
    }

    /**
     * <p>Calculates the filter coefficients for a High-Pass Biquad filter.</p>
     *<br>
     * <p>
     * The coefficients are calculated based on the standard Biquad filter
     * design equations for a high-pass filter. The calculated coefficients
     * are normalized by a0 to ensure proper filter behavior.
     * </p>
     *
     * @param cutoff     the cutoff frequency in Hertz.
     * @param sampleRate the sampling rate in Hertz.
     * @param Q          the quality factor of the filter.
     */
    @Override
    protected void calculateCoefficients(double cutoff, double sampleRate, double Q) {
        double omega = 2.0 * Math.PI * cutoff / sampleRate;
        double sinOmega = Math.sin(omega);
        double cosOmega = Math.cos(omega);
        double alpha = sinOmega / (2.0 * Q);

        double b0_un = (1.0 + cosOmega) / 2.0;
        double b1_un = -(1.0 + cosOmega);
        double b2_un = (1.0 + cosOmega) / 2.0;
        double a0_un = 1.0 + alpha;
        double a1_un = -2.0 * cosOmega;
        double a2_un = 1.0 - alpha;

        normalize(b0_un, b1_un, b2_un, a0_un, a1_un, a2_un);
    }
}
