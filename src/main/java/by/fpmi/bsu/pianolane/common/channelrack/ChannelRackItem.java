package by.fpmi.bsu.pianolane.common.channelrack;

import static by.fpmi.bsu.pianolane.common.ui.ElementsFactory.createKnob;
import static by.fpmi.bsu.pianolane.common.ui.ElementsFactory.createRoundGreenCheckbox;

import by.fpmi.bsu.pianolane.common.ui.KnobControl;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChannelRackItem extends HBox {

    private int channelId;
    private CheckBox isEnabled;
    private Label instrumentName;
    private KnobControl panKnob;
    private KnobControl volumeKnob;
    private StackPane stepPane = new StackPane();

    private final String defaultInstrumentNameStyle =
            "-fx-border-color: transparent; -fx-border-width: 1;"
                    + "-fx-text-fill: white; -fx-font-size: 14; -fx-label-padding:  3px;";

    private final String hoveredInstrumentNameStyle =
            "-fx-border-color: white; -fx-border-width: 1;"
                    + "-fx-text-fill: white; -fx-font-size: 14; -fx-label-padding: 3px";

    public ChannelRackItem(int channelId, String instrumentName) {
        super(10);
        this.channelId = channelId;
        setAlignment(Pos.CENTER_LEFT);
        setStyle("-fx-background-color: #3b3b3b; -fx-padding: 5;");

        // Индикатор
        isEnabled = createRoundGreenCheckbox();

        StackPane checkContainer = new StackPane(isEnabled);
        checkContainer.setMinWidth(24);
        checkContainer.setMaxWidth(24);
        checkContainer.setPrefWidth(24);

        panKnob = createKnob(10, Color.WHITE, 64, 0, 127);
        volumeKnob = createKnob(10, Color.WHITE, 100, 0, 127);

        // Название инструмента
        this.instrumentName = new Label(instrumentName);
        this.instrumentName.setStyle(defaultInstrumentNameStyle);
        this.instrumentName.setPrefWidth(120);
        this.instrumentName.setMinWidth(120);
        this.instrumentName.setMaxWidth(120);

        // Панель шагов - занимает все оставшееся пространство
        stepPane.setPrefHeight(30);
        stepPane.setPrefWidth(400); // Увеличена предпочтительная ширина
        stepPane.setMinWidth(300);  // Минимальная ширина
        stepPane.setStyle("-fx-background-color: #2a2a2a;");

        getChildren().addAll(checkContainer, volumeKnob, panKnob, this.instrumentName, stepPane);

        // Установка приоритетов роста - только stepPane может расти
        HBox.setHgrow(checkContainer, Priority.NEVER);
        HBox.setHgrow(volumeKnob, Priority.NEVER);
        HBox.setHgrow(panKnob, Priority.NEVER);
        HBox.setHgrow(this.instrumentName, Priority.NEVER);
        HBox.setHgrow(stepPane, Priority.NEVER); // Разрешаем расти и занимать все доступное пространство

        addInstrumentNameListeners();
    }

    private void addInstrumentNameListeners() {
        instrumentName.setOnMouseEntered(event ->
                instrumentName.setStyle(hoveredInstrumentNameStyle)
        );
        instrumentName.setOnMouseExited(event ->
                instrumentName.setStyle(defaultInstrumentNameStyle)
        );
    }

    public void setEnabled(boolean enabled) {
        isEnabled.setSelected(enabled);
    }

    public boolean isEnabled() {
        return isEnabled.isSelected();
    }
}