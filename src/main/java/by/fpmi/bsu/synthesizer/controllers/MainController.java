package by.fpmi.bsu.synthesizer.controllers;

import by.fpmi.bsu.synthesizer.SoundGenerator;
import by.fpmi.bsu.synthesizer.models.WaveformGenerator;
import by.fpmi.bsu.synthesizer.models.WaveformType;
import by.fpmi.bsu.synthesizer.setting.FilterSettings;
import by.fpmi.bsu.synthesizer.setting.SoundCommonSettings;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleGroup;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainController {

    public static MainController mainController;

    public static Map<String, SoundGenerator> soundGenerators = new HashMap<>();

    private final List<SoundGenerator> activeSoundGenerators = new ArrayList<>();

//    static {
//        soundGenerators.put("C3", new SoundGenerator(C3_FREQUENCY));
//        soundGenerators.put("C3_SHARP", new SoundGenerator(C3_SHARP_FREQUENCY));
//        soundGenerators.put("D3", new SoundGenerator(D3_SHARP_FREQUENCY));
//        soundGenerators.put("D3_SHARP", new SoundGenerator(D3_SHARP_FREQUENCY));
//        soundGenerators.put("E3", new SoundGenerator(E3_FREQUENCY));
//        soundGenerators.put("F3", new SoundGenerator(F3_FREQUENCY));
//        soundGenerators.put("F3_SHARP", new SoundGenerator(F3_SHARP_FREQUENCY));
//        soundGenerators.put("G3", new SoundGenerator(G3_FREQUENCY));
//        soundGenerators.put("G3_SHARP", new SoundGenerator(G3_SHARP_FREQUENCY));
//    }

    private SoundCommonSettings commonSettings = SoundCommonSettings.getInstance();

    private FilterSettings filterSettings = FilterSettings.getInstance();

    @FXML
    private Canvas waveformCanvas;

    @FXML
    private RadioButton sineRadioButton;

    @FXML
    private RadioButton squareRadioButton;

    @FXML
    private RadioButton sawtoothRadioButton;

    @FXML
    private RadioButton triangleRadioButton;

    @FXML
    private Slider frequencySlider;

    @FXML
    private Slider amplitudeSlider;

    //FILTER
    @FXML
    private CheckBox lowPassCheckBox;

    @FXML
    private Slider lowPassSlider;

    @FXML
    private CheckBox highPassCheckBox;

    @FXML
    private Slider highPassSlider;

    private WaveformGenerator waveformGenerator;

    private GraphicsContext gcWaveform;

    public void initialize() {
        mainController = this;

        waveformGenerator = new WaveformGenerator();

        ToggleGroup waveformToggleGroup = new ToggleGroup();
        sineRadioButton.setToggleGroup(waveformToggleGroup);
        squareRadioButton.setToggleGroup(waveformToggleGroup);
        sawtoothRadioButton.setToggleGroup(waveformToggleGroup);
        triangleRadioButton.setToggleGroup(waveformToggleGroup);

        sineRadioButton.setUserData(WaveformType.SINE);
        squareRadioButton.setUserData(WaveformType.SQUARE);
        sawtoothRadioButton.setUserData(WaveformType.SAWTOOTH);
        triangleRadioButton.setUserData(WaveformType.TRIANGLE);

        sineRadioButton.setSelected(true);

        // Обработчик изменения выбранной радиокнопки
        waveformToggleGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                WaveformType selectedType = (WaveformType) newVal.getUserData();
                waveformGenerator.setWaveformType(selectedType);
                commonSettings.setWaveformType(selectedType);
                updateWaveform();
            }
        });

        // Настройка слайдера для изменения частоты
        frequencySlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            waveformGenerator.setFrequency(newVal.doubleValue());
            updateWaveform();
        });

        amplitudeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            commonSettings.setAmplitude(newVal.doubleValue());
            updateWaveform();
        });

        lowPassCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> updateLowPassFilter());
        lowPassSlider.valueProperty().addListener((obs, oldVal, newVal) -> updateLowPassFilter());

        highPassCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> updateHighPassFilter());
        highPassSlider.valueProperty().addListener((obs, oldVal, newVal) -> updateHighPassFilter());

        gcWaveform = waveformCanvas.getGraphicsContext2D();
        clearCanvas(gcWaveform);

        updateWaveform();
        updateLowPassFilter();
        updateHighPassFilter();
    }

    private void clearCanvas(GraphicsContext gc) {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
    }


    private void updateWaveform() {
        int samplesToDraw = 800;
        waveformGenerator.drawWaveform(gcWaveform,
                waveformCanvas.getWidth(),
                waveformCanvas.getHeight(),
                samplesToDraw);
    }

    private void updateLowPassFilter() {
        boolean enabled = lowPassCheckBox.isSelected();
        double cutoff = lowPassSlider.getValue();
        filterSettings.setLowPassEnabled(enabled);
        filterSettings.setLowPassCutoff(cutoff);
    }

    private void updateHighPassFilter() {
        boolean enabled = highPassCheckBox.isSelected();
        double cutoff = highPassSlider.getValue();
        filterSettings.setHighPassEnabled(enabled);
        filterSettings.setHighPassCutoff(cutoff);
    }
}
