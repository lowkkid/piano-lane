package by.fpmi.bsu.synthesizer.model;

import lombok.extern.slf4j.Slf4j;

import static by.fpmi.bsu.synthesizer.newimpl.Constants.SAMPLE_RATE;

@Slf4j
public class Envelope {
    private enum Stage { ATTACK, DECAY, SUSTAIN, RELEASE, FINISHED }

    private final double attackTime;
    private final double decayTime;
    private final double sustainLevel;
    private final double releaseTime;

    private Stage stage = Stage.FINISHED;
    private int sampleIndex = 0;
    private double currentLevel = 0.0;
    private double levelAtReleaseStart;

    private int attackSamples, decaySamples, releaseSamples;

    public Envelope(double attack, double decay, double sustain, double release) {
        this.attackTime = attack;
        this.decayTime = decay;
        this.sustainLevel = sustain;
        this.releaseTime = release;

        attackSamples = (int) (attack * SAMPLE_RATE);
        decaySamples = (int) (decay * SAMPLE_RATE);
        releaseSamples = (int) (release * SAMPLE_RATE);
        noteOn();
    }

    public void noteOn() {
        stage = Stage.ATTACK;
        sampleIndex = 0;
    }

    public void noteOff() {
        this.levelAtReleaseStart = this.currentLevel;
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
                currentLevel = levelAtReleaseStart * (1.0 - releaseProgress);
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
