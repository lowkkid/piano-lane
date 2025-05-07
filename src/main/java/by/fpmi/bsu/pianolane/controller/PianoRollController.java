package by.fpmi.bsu.pianolane.controller;

import by.fpmi.bsu.pianolane.observer.MidiNoteDeleteObserver;
import by.fpmi.bsu.pianolane.observer.NoteResizedObserver;
import by.fpmi.bsu.pianolane.observer.VelocityChangedObserver;
import by.fpmi.bsu.pianolane.ui.pianoroll.MidiNote;
import by.fpmi.bsu.pianolane.ui.pianoroll.MidiNoteContainer;
import by.fpmi.bsu.pianolane.ui.pianoroll.Note;
import by.fpmi.bsu.pianolane.model.Channel;
import by.fpmi.bsu.pianolane.ui.GridPane;
import by.fpmi.bsu.pianolane.model.ChannelCollection;
import by.fpmi.bsu.pianolane.ui.pianoroll.Velocity;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

import static by.fpmi.bsu.pianolane.util.CopyUtil.copy;
import static by.fpmi.bsu.pianolane.util.GlobalInstances.SEQUENCER;
import static by.fpmi.bsu.pianolane.util.MathUtil.uiToMidiNoteLength;
import static by.fpmi.bsu.pianolane.util.constants.DefaultValues.NORMALIZED_DEFAULT_VELOCITY_VALUE;

@Component
@Slf4j
@Scope("prototype")
public class PianoRollController {

    @FXML
    public AnchorPane headerContent;
    public HBox leftToolbox;
    @FXML
    private ScrollPane headerScrollPane;
    @FXML
    private AnchorPane pianoRollTopPanel;
    @FXML
    private Button magnetButton;
    @FXML
    public ScrollPane notesHorizontalScrollPane;
    @FXML
    public ScrollPane notesVerticalScrollPane;
    public AnchorPane velocityContainer;
    @FXML
    private ScrollPane velocityHorizontalScrollPane;
    @FXML
    private GridPane gridPane;
    @FXML
    private GridPane velocityPane;
    @FXML
    private Pane keyboardPane;
    @FXML
    private Button closeButton;
    @FXML
    private SplitPane splitPane;

    private static double NEW_NOTE_WIDTH = 50;
    private static final double GRID_QUARTER_NOTE_WIDTH = 50;
    private static int GRID_DIVISION_FACTOR = 1;
    private static final Supplier<Double> GRID_CELL_WIDTH = () -> GRID_QUARTER_NOTE_WIDTH / (double) GRID_DIVISION_FACTOR;
    private static final Supplier<Double> TICKS_PER_COLUMN = () -> 480 / (double) GRID_DIVISION_FACTOR;

    private static final int NUM_KEYS = 60;        // 5 octaves (60 keys)
    private static final double KEY_HEIGHT = 30;   // Высота клавиш
    private final String[] NOTES = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};

    // Параметры сетки
    private final double cellHeight = 30;  // Высота клетки (соответствует одной клавише)
    private final int numColumns = 96;     // Количество колонок (тактов)


    private Rectangle playheadLine;
    private Timeline playheadTimeline;

    private MainController mainController;

    private Integer channelId;
    private Channel channel;
    private final ChannelCollection channelCollection = ChannelCollection.getInstance();
    private final MidiNoteContainer midiNoteContainer = MidiNoteContainer.getInstance();

    private final List<VelocityChangedObserver> velocityChangedObservers = new ArrayList<>();
    private final List<NoteResizedObserver> noteResizedObservers = new ArrayList<>();
    private final List<MidiNoteDeleteObserver> midiNoteDeleteObservers = new ArrayList<>();


    public PianoRollController(Integer channelId) {
        this.channelId = channelId;
    }


    @Autowired
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    private boolean isMenuShowing = false;

    private void initializeMagnetButton() {
        // Создаем контекстное меню для кнопки магнита
        ContextMenu magnetMenu = new ContextMenu();

        // Создаем пункты меню
        MenuItem oneBeaItem = new MenuItem("1 beat");
        MenuItem halfBeatItem = new MenuItem("1/2 beat");
        MenuItem quarterBeatItem = new MenuItem("1/4 beat");

        // Добавляем слушатели событий
        oneBeaItem.setOnAction(event -> {
            GRID_DIVISION_FACTOR = 1;
            showEighthNoteLines(false);
            showSixteenthNoteLines(false);
        });

        halfBeatItem.setOnAction(event -> {
            GRID_DIVISION_FACTOR = 2;
            showEighthNoteLines(true);
            showSixteenthNoteLines(false);
        });

        quarterBeatItem.setOnAction(event -> {
            GRID_DIVISION_FACTOR = 4;
            showEighthNoteLines(true);
            showSixteenthNoteLines(true);
        });

        // Применяем CSS-стиль для всего меню
        magnetMenu.getStyleClass().add("magnet-menu");

        // Добавляем CSS-класс для каждого пункта
        oneBeaItem.getStyleClass().add("magnet-menu-item");
        halfBeatItem.getStyleClass().add("magnet-menu-item");
        quarterBeatItem.getStyleClass().add("magnet-menu-item");

        // Добавляем пункты в меню
        magnetMenu.getItems().addAll(oneBeaItem, halfBeatItem, quarterBeatItem);

        // Привязываем меню к кнопке с логикой переключения
        magnetButton.setOnAction(event -> {
            if (isMenuShowing) {
                magnetMenu.hide();
                isMenuShowing = false;
            } else {
                magnetMenu.show(magnetButton, Side.BOTTOM, 0, 0);
                isMenuShowing = true;
            }
        });

        // Обновляем состояние при скрытии меню
        magnetMenu.setOnHidden(event -> isMenuShowing = false);
    }

    private void initializeTopPanel() {
        pianoRollTopPanel.setPrefHeight(50); // Общая высота верхней панели

        // Удаляем отступы у ScrollPane для полного заполнения
        headerScrollPane.setPadding(new Insets(0));

        // Заставляем содержимое занимать всю доступную высоту и ширину
        headerScrollPane.setFitToHeight(true);
        headerScrollPane.setFitToWidth(true);

        headerContent.setMinHeight(15);
        leftToolbox.setMinHeight(15);// Минимальная высота

        // Скрываем полосы прокрутки
        headerScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        headerScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        // Удаляем фон и рамку ScrollPane (если есть)
        headerScrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-border-width: 0;");
    }

    private ChangeListener<Number> notesScrollListener;

    private void initializeScrollSynchronization() {
        // Создаем слушатель только для notesHorizontalScrollPane
        notesScrollListener = (obs, oldVal, newVal) -> {
            // Вычисляем скорректированное значение с учетом смещения в 10 пикселей
            double notesTotalWidth = notesHorizontalScrollPane.getContent().getBoundsInLocal().getWidth() -
                    notesHorizontalScrollPane.getViewportBounds().getWidth();
            double pixelsMoved = notesTotalWidth * newVal.doubleValue();

            double headerTotalWidth = headerScrollPane.getContent().getBoundsInLocal().getWidth() -
                    headerScrollPane.getViewportBounds().getWidth();

            // Вычитаем 10 пикселей из позиции прокрутки
            double adjustedPixelsMoved = pixelsMoved;
            double adjustedHValue = adjustedPixelsMoved / headerTotalWidth;

            // Ограничиваем значение в диапазоне [0, 1]
            adjustedHValue = Math.min(1.0, Math.max(0.0, adjustedHValue));

            // Устанавливаем скорректированное значение
            headerScrollPane.setHvalue(adjustedHValue);

        };

        // Добавляем слушатель только к notesHorizontalScrollPane
        notesHorizontalScrollPane.hvalueProperty().addListener(notesScrollListener);

        // Обработка случая, когда содержимое ScrollPane изменяется
        notesHorizontalScrollPane.viewportBoundsProperty().addListener((obs, oldVal, newVal) -> {
            // Пересчитываем синхронизацию при изменении размера viewport
            double currentHValue = notesHorizontalScrollPane.getHvalue();
            notesScrollListener.changed(notesHorizontalScrollPane.hvalueProperty(), currentHValue, currentHValue);
        });
        headerScrollPane.addEventFilter(ScrollEvent.ANY, Event::consume);
    }

    public void initialize() {
        initializeTopPanel();
        initializeMagnetButton();
        splitPane.setDividerPositions(0.8);
        gridPane.setChannelId(channelId);
        drawKeyboard();
        drawGrid();
        initializeOptionalGridDivision();
        initPlayhead();

        closeButton.setOnAction(event -> mainController.closePianoRoll());

        gridPane.addEventHandler(MouseEvent.MOUSE_CLICKED, this::handleGridClick);


        //TODO: after loading listeners for velo change or note delete are NOT WORKING!!!
        log.info("Fetching previously written notes for channel {}", channelId);
        List<MidiNote> previouslyWrittenNotes = midiNoteContainer.getAllNotesForChannel(channelId);
        log.info("Fetched notes: {}", previouslyWrittenNotes);
        previouslyWrittenNotes.forEach(this::setupMidiNoteListeners);
        gridPane.getChildren().addAll(previouslyWrittenNotes.stream().map(MidiNote::getNote).toList());
        List<Velocity> previousVelocities = previouslyWrittenNotes.stream().map(MidiNote::getVelocity).toList();
        previousVelocities.forEach(velocity -> velocity.setParentPane(velocityPane));
        velocityPane.getChildren().addAll(previousVelocities);
        channel = channelCollection.getChannel(channelId);

        notesHorizontalScrollPane.setVvalue(0);
        notesHorizontalScrollPane.setVmax(0);
        velocityHorizontalScrollPane.setVvalue(0);
        velocityHorizontalScrollPane.setVmax(0);

        // Bind horizontal scrolling between the two panes
        notesHorizontalScrollPane.hvalueProperty().bindBidirectional(
                velocityHorizontalScrollPane.hvalueProperty());

        // Instead of the existing scroll handlers, use this simpler approach:
        notesHorizontalScrollPane.setOnScroll(event -> {
            if (event.getDeltaY() != 0) {
                double newValue = notesVerticalScrollPane.getVvalue() - event.getDeltaY() / notesVerticalScrollPane.getHeight();
                notesVerticalScrollPane.setVvalue(Math.min(Math.max(newValue, 0), 1));
                event.consume();
            }
        });

        addRightStripe();
        subscribeToMidiNoteDeleteEvent(channel);
        subscribeToNoteResizedEvent(channel);
        subscribeToVelocityChangedEvent(channel);
        Platform.runLater(this::initializeScrollSynchronization);
    }

    private void initializeOptionalGridDivision() {
        switch (GRID_DIVISION_FACTOR) {
            case 1:
                showEighthNoteLines(false);
                showSixteenthNoteLines(false);
                break;
            case 2:
                showEighthNoteLines(true);
                showSixteenthNoteLines(false);
                break;
            case 4:
                showEighthNoteLines(true);
                showSixteenthNoteLines(true);
                break;
            default:
                log.warn("Got unsupported value for GRID_DIVISION_FACTOR: {} ,when initializing PianoRoll", GRID_DIVISION_FACTOR);
                break;
        }
    }

    // Отрисовка клавиатуры снизу вверх: нота с i=0 отрисовывается внизу
    private void drawKeyboard() {
        for (int i = 0; i < NUM_KEYS; i++) {
            int noteIndex = i % 12;
            int octaveNumber = (i / 12) + 1;
            boolean isBlackKey = NOTES[noteIndex].contains("#");

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

    private final List<Rectangle> eighthNoteLines = new ArrayList<>();
    private final List<Rectangle> sixteenthNoteLines = new ArrayList<>();

    private void drawGrid() {
        double width = GRID_QUARTER_NOTE_WIDTH * numColumns;
        double height = cellHeight * NUM_KEYS;
        gridPane.setPrefSize(width, height);
        velocityPane.setPrefWidth(width);
        headerContent.setPrefWidth(width - 10);

        // Очищаем предыдущие коллекции перед новой отрисовкой
        eighthNoteLines.clear();
        sixteenthNoteLines.clear();

        // Основные линии (четверти)
        for (int i = 0; i <= numColumns; i++) {
            // Каждые 4 колонки считаем новой границей такта.
            boolean isMeasureLine = (i % 4 == 0);

            // Делаем более толстую линию, если это граница такта.
            double lineThickness = isMeasureLine ? 1.5 : 1;

            // Рисуем вертикальную линию от верха до низа сетки.
            Rectangle columnLine = new Rectangle(i * GRID_QUARTER_NOTE_WIDTH, 0, lineThickness, height);
            columnLine.setFill(isMeasureLine ? Color.BLACK : Color.GRAY);
            gridPane.getChildren().add(columnLine);
            velocityPane.getChildren().add(copy(columnLine));
            if (isMeasureLine) drawBarNumber(i / 4, i * GRID_QUARTER_NOTE_WIDTH);

            // Добавляем линии для 1/8 и 1/16, если текущая позиция не совпадает с четвертью
            if (i < numColumns) {
                // Линия 1/8 (посередине между четвертями)
                double eighthPosition = i * GRID_QUARTER_NOTE_WIDTH + GRID_QUARTER_NOTE_WIDTH / 2;
                Rectangle eighthLine = new Rectangle(eighthPosition, 0, 1, height);
                eighthLine.setFill(Color.gray(0.6, 0.7)); // Более светлый серый
                eighthLine.setVisible(false); // По умолчанию невидимы
                gridPane.getChildren().add(eighthLine);
                eighthNoteLines.add(eighthLine);

                Rectangle velocityEightLine = copy(eighthLine);
                velocityPane.getChildren().add(velocityEightLine);
                eighthNoteLines.add(velocityEightLine);

                // Линии 1/16 (через каждую 1/4 от четверти)
                for (int j = 1; j <= 3; j++) {
                    if (j == 2) continue; // Пропускаем среднюю линию, так как это уже 1/8

                    double sixteenthPosition = i * GRID_QUARTER_NOTE_WIDTH + (GRID_QUARTER_NOTE_WIDTH / 4) * j;
                    Rectangle sixteenthLine = new Rectangle(sixteenthPosition, 0, 1, height);
                    sixteenthLine.setFill(Color.gray(0.7, 0.7)); // Еще более светлый серый
                    sixteenthLine.setVisible(false); // По умолчанию невидимы
                    gridPane.getChildren().add(sixteenthLine);
                    sixteenthNoteLines.add(sixteenthLine);

                    Rectangle velocitySixteenthLine = copy(eighthLine);
                    velocityPane.getChildren().add(velocitySixteenthLine);
                    eighthNoteLines.add(velocitySixteenthLine);
                }
            }
        }

        // Горизонтальные линии (клавиши) — оставляем как было
        for (int r = 0; r <= NUM_KEYS; r++) {
            double y = (NUM_KEYS - r) * cellHeight;
            Rectangle rowLine = new Rectangle(0, y, width, 1);
            rowLine.setFill(Color.GRAY);
            gridPane.getChildren().add(rowLine);
        }
    }

    // Методы для управления видимостью линий
    public void showEighthNoteLines(boolean show) {
        for (Rectangle line : eighthNoteLines) {
            line.setVisible(show);
        }
    }

    public void showSixteenthNoteLines(boolean show) {
        for (Rectangle line : sixteenthNoteLines) {
            line.setVisible(show);
        }
    }

    private double barNumbersOffsetX = 7.0;

    private void drawBarNumber(Integer number, double x) {
        Label label = new Label(number.toString());

        label.setTextFill(Color.WHITE);
        label.setFont(Font.font("System", FontWeight.NORMAL, 12));

        AnchorPane.setLeftAnchor(label, x + barNumbersOffsetX);
        AnchorPane.setBottomAnchor(label, 2.0);

        headerContent.getChildren().add(label);
    }

    private void addRightStripe() {
        // Create the vertical stripe
        Rectangle stripe = new Rectangle();
        stripe.setWidth(10);
        stripe.setFill(Color.web("#2b2b2b")); // Dark gray

        // Set the constraints in the AnchorPane
        AnchorPane.setRightAnchor(stripe, 0.0);
        AnchorPane.setTopAnchor(stripe, 20.0); // Starting below the separator band
        AnchorPane.setBottomAnchor(stripe, 0.0);

        // Adjust velocityHorizontalScrollPane to make room for stripe
        AnchorPane.setRightAnchor(velocityHorizontalScrollPane, 13.0);

        // Add stripe to the velocityContainer
        velocityContainer.getChildren().add(stripe);
    }


    // Обработка клика по сетке: рассчитываем позицию с учетом переворота оси Y
    private void handleGridClick(MouseEvent event) {
        if (event.isConsumed() || event.getButton() != MouseButton.PRIMARY || event.getEventType() != MouseEvent.MOUSE_CLICKED) {
            return;
        }

        if (event.getTarget() instanceof Note) {
            return;
        }

        double x = event.getX();
        double y = event.getY();
        int col = (int) (x / GRID_CELL_WIDTH.get());
        int row = NUM_KEYS - 1 - (int) (y / cellHeight);
        double noteX = col * GRID_CELL_WIDTH.get();
        double noteY = (NUM_KEYS - 1 - row) * cellHeight;

        addNote(noteX, noteY, col, row);
    }


    private void addNote(double x, double y, int col, int row) {
        int midiNote = row + 24;
        int startTick = (int) (col * TICKS_PER_COLUMN.get());
        int noteDuration = uiToMidiNoteLength(NEW_NOTE_WIDTH);

        Integer noteId = channel.addNote(midiNote, startTick, noteDuration);

        MidiNote note = MidiNote.builder()
                .id(noteId)
                .noteParent(gridPane)
                .velocityParent(velocityPane)
                .commonCoordinateX(x)
                .noteCoordinateY(y)
                .noteWidth(NEW_NOTE_WIDTH)
                .noteHeight(cellHeight)
                .velocityHeightPercentage(NORMALIZED_DEFAULT_VELOCITY_VALUE)
                .build();
        midiNoteContainer.addNote(channelId, note);
        setupMidiNoteListeners(note);
    }

    private void setupMidiNoteListeners(MidiNote midiNote) {
        setupMidiNoteDeleteListener(midiNote);
        setupNoteResizeListener(midiNote.getNote());
        setupVelocityChangeListener(midiNote.getVelocity());
    }

    private void initPlayhead() {
        playheadLine = new Rectangle(0, 0, 2, cellHeight * NUM_KEYS);
        playheadLine.setFill(Color.RED);
    }

    protected void drawPlayhead() {
        gridPane.getChildren().add(playheadLine);
    }

    protected void removePlayhead() {
        gridPane.getChildren().remove(playheadLine);
    }

    protected void startPlayhead() {
        if (playheadTimeline != null) {
            playheadTimeline.stop();
        }
        drawPlayhead();

        playheadTimeline = new Timeline(new KeyFrame(Duration.millis(10), ev -> {
            if (SEQUENCER.isRunning()) {
                long tickPos = SEQUENCER.getTickPosition();
                double newX = (tickPos / TICKS_PER_COLUMN.get()) * GRID_CELL_WIDTH.get();
                playheadLine.setX(newX);
            } else {
                playheadTimeline.stop();
                playheadLine.setX(0);
                removePlayhead();
            }
        }));
        playheadTimeline.setCycleCount(Timeline.INDEFINITE);
        playheadTimeline.play();
    }

    private void setupMidiNoteDeleteListener(MidiNote midiNote) {
        Note note = midiNote.getNote();
        Velocity velocity = midiNote.getVelocity();
        note.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                System.out.println("Note clicked with RMB");
                gridPane.getChildren().remove(note);
                velocityPane.getChildren().remove(velocity);
                midiNoteContainer.removeNote(channelId, midiNote);
                notifyMidiNoteDeleteEventObservers(midiNote.getId());
            } else if (event.getButton() == MouseButton.PRIMARY) {
                log.info("Note clicked with LMB");
                PianoRollController.NEW_NOTE_WIDTH = note.getWidth();
            }
        });
    }

    private void setupVelocityChangeListener(Velocity velocity) {
        AtomicBoolean dragging = new AtomicBoolean(false);

        final DoubleProperty initialY = new SimpleDoubleProperty();
        final DoubleProperty initialHeight = new SimpleDoubleProperty();

        velocity.getHandle().setOnMousePressed(e -> {
            dragging.set(true);
            initialY.set(e.getSceneY());
            initialHeight.set(velocity.getHeightPercentage());
            e.consume();
        });

        velocity.getHandle().setOnMouseDragged(e -> {
            if (dragging.get()) {
                double deltaY = -(e.getSceneY() - initialY.get()) / velocityPane.getHeight();
                double newPercentage = Math.min(1.0, Math.max(0.1, initialHeight.get() + deltaY));
                velocity.setHeightPercentage(newPercentage);
                e.consume();
            }
        });

        velocity.getHandle().setOnMouseReleased(e -> {
            dragging.set(false);
            notifyVelocityChangedEventObservers(velocity.getNoteId(), velocity.getVelocityValue());
            e.consume();
        });
    }

    private void setupNoteResizeListener(Note note) {
        note.setOnMouseDragged(event -> {
            if (note.isResizing()) {
                double newWidth = event.getX() - note.getX();
                double cellWidth = GRID_CELL_WIDTH.get();
                newWidth = Math.round(newWidth / cellWidth) * cellWidth;
                newWidth = Math.max(newWidth, cellWidth);
                note.setWidth(newWidth);
                event.consume();
            }
        });

        note.setOnMouseReleased(event -> {
            note.setResizing(false);
            event.consume();
            notifyResizeEventObservers(note.getNoteId(), (int) note.getWidth());
        });
    }

    public void subscribeToMidiNoteDeleteEvent(MidiNoteDeleteObserver...midiNoteDeleteObservers) {
        this.midiNoteDeleteObservers.addAll(List.of(midiNoteDeleteObservers));
    }

    private void notifyMidiNoteDeleteEventObservers(Integer deletedNoteId) {
        for (MidiNoteDeleteObserver observer : midiNoteDeleteObservers) {
            observer.onNoteDeleted(deletedNoteId);
        }
    }

    public void subscribeToNoteResizedEvent(NoteResizedObserver noteResizedObserver) {
        noteResizedObservers.add(noteResizedObserver);
    }

    private void notifyResizeEventObservers(Integer noteId, Integer newWidth) {
        for (NoteResizedObserver observer : noteResizedObservers) {
            observer.onNoteResized(noteId, newWidth);
        }
    }

    public void subscribeToVelocityChangedEvent(VelocityChangedObserver velocityChangedObserver) {
        velocityChangedObservers.add(velocityChangedObserver);
    }

    private void notifyVelocityChangedEventObservers(Integer noteId, Integer newVelocity) {
        System.out.println("UPDATING VELOCITY WITH " + newVelocity);
        for (VelocityChangedObserver observer : velocityChangedObservers) {
            observer.onVelocityChanged(noteId, newVelocity);
        }
    }
}
