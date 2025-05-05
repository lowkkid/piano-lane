package by.fpmi.bsu.pianolane.util;

import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LogUtil {

    public static String getAllTrackEvents(Track track) {
        List<String> events = new ArrayList<>(track.size());
        for (int i = 0; i < track.size(); i++) {
            MidiEvent event = track.get(i);
            MidiMessage message = event.getMessage();
            long tick = event.getTick();

            if (message instanceof ShortMessage sm) {
                int command = sm.getCommand();
                int channel = sm.getChannel();
                int data1 = sm.getData1();
                int data2 = sm.getData2();
                events.add(String.format("Tick: %4d | %s | Channel: %d | Data1: %d | Data2: %d\n",
                        tick, getCommandName(command), channel, data1, data2));
            } else if (message instanceof MetaMessage meta) {
                int type = meta.getType();
                events.add(String.format("Tick: %4d | META_EVENT (type %d)\n", tick, type));
            } else {
                events.add(String.format("Tick: %4d | OTHER EVENT: %s\n", tick, message.getClass().getSimpleName()));
            }
        }
        return events.toString();
    }

    public static void logAllTrackEvents(Track track) {
        List<String> events = new ArrayList<>(track.size());
        for (int i = 0; i < track.size(); i++) {
            MidiEvent event = track.get(i);
            MidiMessage message = event.getMessage();
            long tick = event.getTick();

            if (message instanceof ShortMessage sm) {
                int command = sm.getCommand();
                int channel = sm.getChannel();
                int data1 = sm.getData1();
                int data2 = sm.getData2();
                events.add(String.format("Tick: %4d | %s | Channel: %d | Data1: %d | Data2: %d\n",
                        tick, getCommandName(command), channel, data1, data2));
            } else if (message instanceof MetaMessage meta) {
                int type = meta.getType();
                events.add(String.format("Tick: %4d | META_EVENT (type %d)\n", tick, type));
            } else {
                events.add(String.format("Tick: %4d | OTHER EVENT: %s\n", tick, message.getClass().getSimpleName()));
            }
        }
        events.forEach(log::info);
    }

    public static String getCommandName(int command) {
        return switch (command) {
            case ShortMessage.NOTE_ON -> "NOTE_ON";
            case ShortMessage.NOTE_OFF -> "NOTE_OFF";
            case ShortMessage.PROGRAM_CHANGE -> "PROGRAM_CHANGE";
            case ShortMessage.CONTROL_CHANGE -> "CONTROL_CHANGE";
            default -> "OTHER";
        };
    }
}
