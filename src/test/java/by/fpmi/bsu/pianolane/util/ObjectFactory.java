package by.fpmi.bsu.pianolane.util;

import static by.fpmi.bsu.pianolane.common.util.MidiUtil.createTrack;

import by.fpmi.bsu.pianolane.common.util.MidiUtil;
import by.fpmi.bsu.pianolane.midi.channel.model.Channel;
import by.fpmi.bsu.pianolane.midi.channel.ChannelCollection;
import by.fpmi.bsu.pianolane.midi.note.NoteEvent;
import by.fpmi.bsu.pianolane.midi.channel.model.DefaultChannel;

import by.fpmi.bsu.pianolane.midi.note.NoteMessage;
import by.fpmi.bsu.pianolane.midi.note.NoteMidiEvent;
import by.fpmi.bsu.pianolane.midi.note.NoteOffMessage;
import by.fpmi.bsu.pianolane.midi.note.NoteOnMessage;
import javax.sound.midi.Instrument;

public class ObjectFactory {

    public static final int CHANNEL_ID = 1;
    public static final int MIDI_NOTE = 48;
    public static final int VELOCITY = 100;
    public static final int TICK = 0;
    public static final int NOTE_LENGTH = 480;

    public static NoteMessage createNoteOffMessage() {
        return new NoteOffMessage(CHANNEL_ID, MIDI_NOTE);
    }

    public static NoteMessage createNoteOnMessage() {
        return new NoteOnMessage(CHANNEL_ID, MIDI_NOTE, VELOCITY);
    }

    public static NoteMidiEvent createNoteMidiEvent(NoteMessage noteMessage) {
        return new NoteMidiEvent(noteMessage, TICK);
    }

    public static NoteEvent createNoteEvent() throws Exception {
        return new NoteEvent(createTrack(), CHANNEL_ID, MIDI_NOTE, TICK, NOTE_LENGTH);
    }

    public static Instrument getInstrument() {
        return MidiUtil.getInstrumentById(0);
    }

    public static Channel createDefaultChannel() {
        Channel defaultChannel = new DefaultChannel(CHANNEL_ID, getInstrument());
        defaultChannel.addNote(MIDI_NOTE, TICK, NOTE_LENGTH);
        defaultChannel.addNote(MIDI_NOTE - 12, TICK, NOTE_LENGTH);
        defaultChannel.addNote(MIDI_NOTE + 12, TICK, NOTE_LENGTH);
        return defaultChannel;
    }

    public static ChannelCollection createChannelCollection() {
        Channel[] channels = new Channel[16];
        channels[0] = createDefaultChannel();
        channels[1] = createDefaultChannel();
        channels[2] = createDefaultChannel();

        return ChannelCollection.builder()
                .channels(channels)
                .build();
    }
}
