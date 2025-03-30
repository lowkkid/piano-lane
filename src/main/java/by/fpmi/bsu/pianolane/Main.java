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
        List<String> example = new ArrayList<>();
        example.add("SSSS");
        example.add("AAAA");

        System.out.println(example.toString());
    }
}
