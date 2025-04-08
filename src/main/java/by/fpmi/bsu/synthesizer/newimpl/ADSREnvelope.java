package by.fpmi.bsu.synthesizer.newimpl;

public class ADSREnvelope {
    private enum Stage { ATTACK, DECAY, SUSTAIN, RELEASE, FINISHED }

    private final double attackTime;
    private final double decayTime;
    private final double sustainLevel;
    private final double releaseTime;
    private final double sampleRate;

    private Stage stage = Stage.FINISHED;
    private int sampleIndex = 0;
    private double currentLevel = 0.0;

    private int attackSamples, decaySamples, releaseSamples;

    public ADSREnvelope(double attack, double decay, double sustain, double release, double sampleRate) {
        this.attackTime = attack;
        this.decayTime = decay;
        this.sustainLevel = sustain;
        this.releaseTime = release;
        this.sampleRate = sampleRate;

        attackSamples = (int) (attack * sampleRate);
        decaySamples = (int) (decay * sampleRate);
        releaseSamples = (int) (release * sampleRate);
    }

    public void noteOn() {
        stage = Stage.ATTACK;
        sampleIndex = 0;
    }

    public void noteOff() {
        stage = Stage.RELEASE;
        sampleIndex = 0;
    }

    public boolean isFinished() {
        return stage == Stage.FINISHED;
    }

    public float nextSample() {
        switch (stage) {
            case ATTACK -> {
                if (attackSamples == 0) {
                    stage = Stage.DECAY;
                    return nextSample();
                }
                currentLevel = (double) sampleIndex / attackSamples;
                sampleIndex++;
                if (sampleIndex >= attackSamples) {
                    stage = Stage.DECAY;
                    sampleIndex = 0;
                }
            }
            case DECAY -> {
                if (decaySamples == 0) {
                    currentLevel = sustainLevel;
                    stage = Stage.SUSTAIN;
                    return (float) currentLevel;
                }
                double decayProgress = (double) sampleIndex / decaySamples;
                currentLevel = 1.0 - decayProgress * (1.0 - sustainLevel);
                sampleIndex++;
                if (sampleIndex >= decaySamples) {
                    stage = Stage.SUSTAIN;
                }
            }
            case SUSTAIN -> {
                currentLevel = sustainLevel;
            }
            case RELEASE -> {
                if (releaseSamples == 0) {
                    currentLevel = 0.0;
                    stage = Stage.FINISHED;
                    return 0.0f;
                }
                double releaseProgress = (double) sampleIndex / releaseSamples;
                currentLevel *= 1.0 - releaseProgress;
                sampleIndex++;
                if (sampleIndex >= releaseSamples) {
                    currentLevel = 0.0;
                    stage = Stage.FINISHED;
                }
            }
            case FINISHED -> {
                currentLevel = 0.0;
            }
        }
        return (float) currentLevel;
    }
}
