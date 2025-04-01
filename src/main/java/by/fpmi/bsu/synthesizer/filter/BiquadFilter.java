package by.fpmi.bsu.synthesizer.filter;

/**
 * Abstract base class for Biquad filters.
 * <p>
 * This class provides the foundational structure and common functionalities
 * for various types of Biquad filters, including coefficient management,
 * state handling, and signal processing methods.
 * </p>
 */
public abstract class BiquadFilter {
    /** Filter coefficients. */
    protected double a1, a2, b0, b1, b2;

    /** Previous input samples. */
    protected double x1, x2;

    /** Previous output samples. */
    protected double y1, y2;

    /**
     * Constructs a Biquad filter with specified parameters.
     *
     * @param cutoffFrequency the cutoff frequency in Hertz.
     * @param sampleRate      the sampling rate in Hertz.
     * @param Q               the quality factor of the filter.
     */
    public BiquadFilter(double cutoffFrequency, double sampleRate, double Q) {
        calculateCoefficients(cutoffFrequency, sampleRate, Q);
        reset();
    }

    /**
     * <p>Calculates the filter coefficients.</p>
     * <br>
     * <p>
     * This method must be implemented by subclasses to define specific
     * filter characteristics (e.g., high-pass, low-pass).
     * </p>
     *
     * @param cutoff     the cutoff frequency in Hertz.
     * @param sampleRate the sampling rate in Hertz.
     * @param Q          the quality factor of the filter.
     */
    protected abstract void calculateCoefficients(double cutoff, double sampleRate, double Q);

    /**
     * Resets the internal state of the filter by clearing the previous input and output samples,
     * effectively resetting the filter to its initial state.
     */
    public void reset() {
        this.x1 = 0.0;
        this.x2 = 0.0;
        this.y1 = 0.0;
        this.y2 = 0.0;
    }

    /**
     * Applies the Biquad filter equation to the input sample
     * and updates the internal state accordingly.
     * @param in the input sample (normalized within the range <code>[-1.0, 1.0]</code>).
     * @return the filtered output sample.
     */
    public double filter(double in) {
        double out = b0 * in + b1 * x1 + b2 * x2 - a1 * y1 - a2 * y2;
        x2 = x1;
        x1 = in;
        y2 = y1;
        y1 = out;
        return out;
    }

    /**
     * Applies the filter to each sample in the input array
     * sequentially and returns an array of filtered output samples.
     * @param input an array of input samples (each normalized within the range <code>[-1.0, 1.0]</code>).
     * @return an array of filtered output samples.
     */
    public double[] filter(double[] input) {
        double[] output = new double[input.length];
        for(int i = 0; i < input.length; i++) {
            output[i] = filter(input[i]);
        }
        return output;
    }

    protected void normalize(double b0_un, double b1_un, double b2_un,
                             double a0_un, double a1_un, double a2_un) {
        this.b0 = b0_un / a0_un;
        this.b1 = b1_un / a0_un;
        this.b2 = b2_un / a0_un;
        this.a1 = a1_un / a0_un;
        this.a2 = a2_un / a0_un;
    }
}
