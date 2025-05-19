package by.fpmi.bsu.pianolane.util;

import static by.fpmi.bsu.pianolane.util.MidiUtil.createTrack;

import by.fpmi.bsu.pianolane.model.Channel;
import by.fpmi.bsu.pianolane.model.ChannelCollection;
import by.fpmi.bsu.pianolane.wrappers.NoteEvent;
import by.fpmi.bsu.pianolane.model.DefaultChannel;

import by.fpmi.bsu.pianolane.wrappers.NoteMessage;
import by.fpmi.bsu.pianolane.wrappers.NoteMidiEvent;
import by.fpmi.bsu.pianolane.wrappers.NoteOffMessage;
import by.fpmi.bsu.pianolane.wrappers.NoteOnMessage;
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
        //        Map<Integer, NoteEvent> noteEvents = new HashMap<>();
//        AtomicInteger notesSequence = new AtomicInteger();
//        for (int i = 0; i < 5; i++) {
//            NoteEvent noteEvent = createNoteEvent();
//            Integer key = notesSequence.getAndIncrement();
//            noteEvents.put(key, noteEvent);
//        }
//
//        return DefaultChannel.builder()
//                .noteEvents(noteEvents)
//                .notesSequence(notesSequence)
//                .channelId(CHANNEL_ID)
//                .muted(false)
//                .soloed(false)
//                .build();
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
