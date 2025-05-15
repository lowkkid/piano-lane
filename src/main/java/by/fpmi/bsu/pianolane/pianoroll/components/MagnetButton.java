package by.fpmi.bsu.pianolane.pianoroll.components;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import lombok.Setter;

public class MagnetButton extends Button {

    private static final String ONE_BEAT_ITEM_TEXT = "1 beat";
    private static final String HALF_BEAT_ITEM_TEXT = "1/2 beat";
    private static final String QUARTER_BEAT_ITEM_TEXT = "1/4 beat";

    private final List<MenuItem> menuItems = new ArrayList<>();
    @Setter
    private Consumer<Integer> gridDivider;
    private boolean isMenuShowing = false;

    public MagnetButton() {
        super();
        initialize();
    }

    private void initialize() {
        addMenuItem(ONE_BEAT_ITEM_TEXT, event -> gridDivider.accept(1));
        addMenuItem(HALF_BEAT_ITEM_TEXT, event -> gridDivider.accept(2));
        addMenuItem(QUARTER_BEAT_ITEM_TEXT, event -> gridDivider.accept(4));
        menuItems.forEach(item -> item.getStyleClass().add("magnet-menu-item"));

        ContextMenu magnetMenu = new ContextMenu();
        magnetMenu.getStyleClass().add("magnet-menu");
        magnetMenu.getItems().addAll(menuItems);

        setOnAction(event -> {
            if (isMenuShowing) {
                magnetMenu.hide();
                isMenuShowing = false;
            } else {
                magnetMenu.show(this, Side.BOTTOM, 0, 0);
                isMenuShowing = true;
            }
        });
        magnetMenu.setOnHidden(event -> isMenuShowing = false);
    }

    private void addMenuItem(String text, EventHandler<ActionEvent> action) {
        MenuItem menuItem = new MenuItem(text);
        menuItem.setOnAction(action);
        menuItems.add(menuItem);
    }
}
