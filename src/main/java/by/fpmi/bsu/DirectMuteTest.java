package by.fpmi.bsu;

import javax.sound.midi.*;

public class DirectMuteTest {
    public static void main(String[] args) throws Exception {
        Synthesizer synth = MidiSystem.getSynthesizer();
        synth.open();
        Receiver receiver = synth.getReceiver();

        // Информация о системе
        System.out.println("Java version: " + System.getProperty("java.version"));
        System.out.println("OS: " + System.getProperty("os.name"));
        System.out.println("Synthesizer: " + synth.getClass().getName());

        // Играем ноту
        System.out.println("Playing note...");
        sendNoteOn(receiver, 0, 60, 80);
        Thread.sleep(1000);
        sendNoteOff(receiver, 0, 60);
        Thread.sleep(500);

        // Метод 1: Channel Volume (CC 7)
        System.out.println("Testing Channel Volume (CC 7)...");
        // Сохраняем текущую громкость
        int currentVolume = 100;

        // Устанавливаем громкость на 0
        System.out.println("Setting volume to 0");
        sendControlChange(receiver, 0, 7, 0);
        Thread.sleep(100);

        // Играем ноту (должна быть тишина)
        sendNoteOn(receiver, 0, 60, 80);
        Thread.sleep(1000);
        sendNoteOff(receiver, 0, 60);
        Thread.sleep(500);

        // Восстанавливаем громкость
        System.out.println("Restoring volume to " + currentVolume);
        sendControlChange(receiver, 0, 7, currentVolume);
        Thread.sleep(100);

//        // Играем ноту (должен быть звук)
//        sendNoteOn(receiver, 0, 60, 80);
//        Thread.sleep(1000);
//        sendNoteOff(receiver, 0, 60);
//        Thread.sleep(500);

//        // Метод 2: Channel Mute (CC 120 - All Sound Off)
//        System.out.println("Testing All Sound Off (CC 120)...");
//        sendNoteOn(receiver, 0, 60, 80);
//        Thread.sleep(500);
//        System.out.println("Sending All Sound Off");
//        sendControlChange(receiver, 0, 120, 0);
//        Thread.sleep(1000);
//
//        // Метод 3: Reset All Controllers (CC 121)
//        System.out.println("Testing Reset All Controllers (CC 121)...");
//        sendControlChange(receiver, 0, 121, 0);
//        Thread.sleep(100);
//        sendNoteOn(receiver, 0, 60, 80);
//        Thread.sleep(1000);
//        sendNoteOff(receiver, 0, 60);

        // Закрываем ресурсы
        receiver.close();
        synth.close();
        System.out.println("Test completed");
    }

    private static void sendNoteOn(Receiver receiver, int channel, int note, int velocity) throws InvalidMidiDataException {
        ShortMessage message = new ShortMessage();
        message.setMessage(ShortMessage.NOTE_ON, channel, note, velocity);
        receiver.send(message, -1);
    }

    private static void sendNoteOff(Receiver receiver, int channel, int note) throws InvalidMidiDataException {
        ShortMessage message = new ShortMessage();
        message.setMessage(ShortMessage.NOTE_OFF, channel, note, 0);
        receiver.send(message, -1);
    }

    private static void sendControlChange(Receiver receiver, int channel, int controller, int value) throws InvalidMidiDataException {
        ShortMessage message = new ShortMessage();
        message.setMessage(ShortMessage.CONTROL_CHANGE, channel, controller, value);
        receiver.send(message, -1);
    }
}