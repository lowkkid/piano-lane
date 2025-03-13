package by.fpmi.bsu.pianolane;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

import javax.sound.midi.*;
import java.util.ArrayList;
import java.util.List;

public class PianoRollController {

    @FXML private Pane gridPane;
    @FXML private Pane keyboardPane;
    @FXML private Button playButton; // Добавьте кнопку в FXML с onAction="#handlePlayButton"

    private final int NUM_KEYS = 60;        // 5 октав (60 клавиш)
    private final double KEY_HEIGHT = 30;   // Высота клавиш
    private final String[] NOTES = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};

    // Параметры сетки
    private final double cellWidth = 50;   // Ширина клетки (по времени)
    private final double cellHeight = 30;  // Высота клетки (соответствует одной клавише)
    private final int numColumns = 48;     // Количество колонок (тактов)

    // Список добавленных нот (прямоугольников)
    private final List<Rectangle> notes = new ArrayList<>();

    // Плейхед – вертикальная линия, показывающая текущий момент воспроизведения
    private Rectangle playheadLine;
    private Timeline playheadTimeline;

    public void initialize() {
        drawKeyboard();
        drawGrid();
        initPlayhead();

        // При клике по сетке добавляем ноту
        gridPane.addEventHandler(MouseEvent.MOUSE_CLICKED, this::handleGridClick);
    }

    // Инициализация плейхеда (вертикальная красная линия)
    private void initPlayhead() {
        playheadLine = new Rectangle(0, 0, 2, gridPane.getPrefHeight());
        playheadLine.setFill(Color.RED);
        gridPane.getChildren().add(playheadLine);
    }

    // Отрисовка клавиатуры. Здесь ноты отрисовываются снизу вверх:
    // i = 0 соответствует самой низкой ноте, i = NUM_KEYS-1 – самой высокой.
    private void drawKeyboard() {
        for (int i = 0; i < NUM_KEYS; i++) {
            int noteIndex = i % 12;
            int octaveNumber = (i / 12) + 1;
            boolean isBlackKey = NOTES[noteIndex].contains("#");

            // Вычисляем координату Y так, чтобы i = 0 оказался снизу
            double y = (NUM_KEYS - 1 - i) * KEY_HEIGHT;
            Rectangle key = new Rectangle(0, y, 120, KEY_HEIGHT);
            key.getStyleClass().add(isBlackKey ? "black-key" : "white-key");

            Text noteLabel = new Text(
                    5,
                    y + KEY_HEIGHT - 5,
                    NOTES[noteIndex] + octaveNumber);
            noteLabel.setFont(new Font(12));
            noteLabel.setFill(isBlackKey ? Color.WHITE : Color.BLACK);

            keyboardPane.getChildren().addAll(key, noteLabel);
        }
    }

    // Отрисовка сетки. Вертикальные линии отрисовываются как раньше,
    // горизонтальные – с переворотом: нижняя граница соответствует нижней клетке.
    private void drawGrid() {
        double width = cellWidth * numColumns;
        double height = cellHeight * NUM_KEYS;
        gridPane.setPrefSize(width, height);

        // Вертикальные линии (колонки)
        for (int i = 0; i < numColumns; i++) {
            boolean isBeat = i % 4 == 0;
            Rectangle columnLine = new Rectangle(i * cellWidth, 0, 1, height);
            columnLine.setFill(isBeat ? Color.DARKGRAY : Color.GRAY);
            gridPane.getChildren().add(columnLine);
        }

        // Горизонтальные линии. Рисуем от нижней границы (y = height) к верхней (y = 0)
        for (int r = 0; r <= NUM_KEYS; r++) {
            double y = (NUM_KEYS - r) * cellHeight;
            Rectangle rowLine = new Rectangle(0, y, width, 1);
            rowLine.setFill(Color.GRAY);
            gridPane.getChildren().add(rowLine);
        }
    }

    // Обработка клика по сетке: вычисляем колонку по X и "перевёрнутый" ряд по Y.
    private void handleGridClick(MouseEvent event) {
        double x = event.getX();
        double y = event.getY();
        int col = (int) (x / cellWidth);
        // Поскольку визуально ось Y перевёрнута, вычисляем:
        int row = NUM_KEYS - 1 - (int) (y / cellHeight);
        // Для размещения ноты используем обратное преобразование:
        double noteX = col * cellWidth;
        double noteY = (NUM_KEYS - 1 - row) * cellHeight;
        addNote(noteX, noteY);
    }

    // Добавление ноты в сетку (просто отрисовываем прямоугольник)
    private void addNote(double x, double y) {
        Rectangle note = new Rectangle(x, y, cellWidth, cellHeight);
        note.setFill(Color.BLUE);
        note.setStroke(Color.BLACK);
        gridPane.getChildren().add(note);
        notes.add(note);
    }

    // Обработчик нажатия кнопки воспроизведения (укажите в FXML onAction="#handlePlayButton")
    @FXML
    private void handlePlayButton(ActionEvent event) {
        playNotes();
    }

    // Воспроизведение нот через Java Sound MIDI.
    // Для каждой добавленной ноты вычисляем момент (по X) и высоту (по Y с учетом переворота)
    private void playNotes() {
        try {
            // Создаем MIDI-последовательность с PPQ-резолюцией (480 тиков на четверть ноту)
            Sequence sequence = new Sequence(Sequence.PPQ, 480);
            Track track = sequence.createTrack();

            int ticksPerColumn = 480;
            int noteDuration = 480;

            // Для каждой ноты:
            for (Rectangle rect : notes) {
                // Вычисляем колонку по X
                int col = (int) (rect.getX() / cellWidth);
                // Вычисляем ряд. Так как ноты отрисованы перевернуто, получаем:
                int gridRow = NUM_KEYS - 1 - (int)(rect.getY() / cellHeight);
                // Преобразуем номер строки в MIDI-номер (с небольшим оффсетом, например, 24)
                int midiNote = gridRow + 24;
                int startTick = col * ticksPerColumn;

                // NOTE ON событие (канал 0, скорость 100)
                ShortMessage noteOn = new ShortMessage();
                noteOn.setMessage(ShortMessage.NOTE_ON, 0, midiNote, 100);
                MidiEvent noteOnEvent = new MidiEvent(noteOn, startTick);
                track.add(noteOnEvent);

                // NOTE OFF событие через noteDuration
                ShortMessage noteOff = new ShortMessage();
                noteOff.setMessage(ShortMessage.NOTE_OFF, 0, midiNote, 0);
                MidiEvent noteOffEvent = new MidiEvent(noteOff, startTick + noteDuration);
                track.add(noteOffEvent);
            }

            // Добавляем событие конца трека
            ShortMessage endMsg = new ShortMessage();
            endMsg.setMessage(ShortMessage.CONTROL_CHANGE, 0, 123, 0);
            MidiEvent endEvent = new MidiEvent(endMsg, sequence.getTickLength());
            track.add(endEvent);

            // Открываем Sequencer, устанавливаем последовательность и начинаем воспроизведение
            Sequencer sequencer = MidiSystem.getSequencer();
            sequencer.open();
            sequencer.setSequence(sequence);
            sequencer.setTempoInBPM(120);
            sequencer.start();

            // Запускаем анимацию плейхеда, которая обновляет его положение каждые 50 мс
            playheadTimeline = new Timeline(new KeyFrame(Duration.millis(10), ev -> {
                if (sequencer.isRunning()) {
                    long tickPos = sequencer.getTickPosition();
                    double newX = (tickPos / (double) ticksPerColumn) * cellWidth;
                    playheadLine.setX(newX);
                } else {
                    playheadTimeline.stop();
                    playheadLine.setX(0);
                }
            }));
            playheadTimeline.setCycleCount(Timeline.INDEFINITE);
            playheadTimeline.play();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
