package by.fpmi.bsu.synthesizer.settings;

import by.fpmi.bsu.synthesizer.newimpl.Waveform;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Getter
@Setter
@Slf4j
public class OscillatorSettings {


    private boolean isEnabled;
    private Waveform waveform = Waveform.SINE;
    private int unison = 1;
    private double detune = 0; // 0.0 to 1.0
    private double level = 0.5; // 0.0 to 1.0
    private double phase = 0; // 0.0 to 1.0
    private double attack = 0;  // 0.0 to 0.4
    private double decay = 0;   // 0.0 to 0.4
    private double sustain = 1.0; // 0.0 to 1.0
    private double release = 0; // 0.0 to 0.4

    public OscillatorSettings(boolean isEnabled) {
        this.isEnabled = isEnabled;
        log.debug("OscillatorSettings initialized with isEnabled = {}", isEnabled);
    }

    public synchronized boolean isEnabled() {
        log.debug("Getting oscillator enabled state: {}", isEnabled);
        return isEnabled;
    }

    public synchronized void setEnabled(boolean enabled) {
        log.debug("Setting oscillator enabled state from {} to {}", isEnabled, enabled);
        this.isEnabled = enabled;
    }

    public synchronized Waveform getWaveform() {
        log.debug("Getting oscillator waveform: {}", waveform);
        return waveform;
    }

    public synchronized void setWaveform(Waveform waveform) {
        log.debug("Setting oscillator waveform from {} to {}", this.waveform, waveform);
        this.waveform = waveform;
    }

    public synchronized int getUnison() {
        log.debug("Getting oscillator unison: {}", unison);
        return unison;
    }

    public synchronized void setUnison(int unison) {
        log.debug("Setting oscillator unison from {} to {}", this.unison, unison);
        this.unison = unison;
    }

    public synchronized void increaseUnison() {
        log.debug("Increasing oscillator unison from {}", unison);
        this.unison++;
    }

    public synchronized void decreaseUnison() {
        log.debug("Decreasing oscillator unison from {}", unison);
        this.unison--;
    }

    public synchronized double getDetune() {
        log.debug("Getting oscillator detune: {}", detune);
        return detune;
    }

    public synchronized void setDetune(double detune) {
        log.debug("Setting oscillator detune from {} to {}", this.detune, detune);
        this.detune = detune;
    }

    public synchronized double getLevel() {
        log.debug("Getting oscillator level: {}", level);
        return level;
    }

    public synchronized void setLevel(double level) {
        log.debug("Setting oscillator level from {} to {}", this.level, level);
        this.level = level;
    }

    public synchronized double getPhase() {
        log.debug("Getting oscillator phase: {}", phase);
        return phase;
    }

    public synchronized void setPhase(double phase) {
        log.debug("Setting oscillator phase from {} to {}", this.phase, phase);
        this.phase = phase;
    }

    public synchronized double getAttack() {
        log.debug("Getting oscillator attack: {}", attack);
        return attack;
    }

    public synchronized void setAttack(double attack) {
        log.debug("Setting oscillator attack from {} to {}", this.attack, attack);
        this.attack = attack;
    }

    public synchronized double getDecay() {
        log.debug("Getting oscillator decay: {}", decay);
        return decay;
    }

    public synchronized void setDecay(double decay) {
        log.debug("Setting oscillator decay from {} to {}", this.decay, decay);
        this.decay = decay;
    }

    public synchronized double getSustain() {
        log.debug("Getting oscillator sustain: {}", sustain);
        return sustain;
    }

    public synchronized void setSustain(double sustain) {
        log.debug("Setting oscillator sustain from {} to {}", this.sustain, sustain);
        this.sustain = sustain;
    }

    public synchronized double getRelease() {
        log.debug("Getting oscillator release: {}", release);
        return release;
    }

    public synchronized void setRelease(double release) {
        log.debug("Setting oscillator release from {} to {}", this.release, release);
        this.release = release;
    }
}