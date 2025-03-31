package by.fpmi.bsu.pianolane;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Synthesizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

public class Main {

    public static void main(String[] args) {
        Vector<Synthesizer> synthInfos;
        MidiDevice device = null;
        MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();
        for (int i = 0; i < infos.length; i++) {
            try {
                device = MidiSystem.getMidiDevice(infos[i]);
            } catch (MidiUnavailableException e) {
                // Handle or throw exception...
            }
            if (device instanceof Synthesizer) {
                for (var x : ((Synthesizer) device).getAvailableInstruments()) {
                    System.out.println(x);
                }
            }
        }
    }
}
