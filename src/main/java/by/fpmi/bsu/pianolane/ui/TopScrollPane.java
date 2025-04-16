package by.fpmi.bsu.pianolane.ui;

import java.util.Set;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

public class TopScrollPane extends ScrollPane {
    private ScrollBar horizontalScrollBar;
    private Node content;

    public TopScrollPane() {
        super();
        skinProperty().addListener((obs, oldSkin, newSkin) -> {
            if (newSkin != null) {
                initializeHorizontalScrollBar();
            }
        });
    }

    private void initializeHorizontalScrollBar() {
        // Получаем ScrollBar из skin
        Set<Node> nodes = lookupAll(".scroll-bar");
        for (Node node : nodes) {
            if (node instanceof ScrollBar) {
                ScrollBar scrollBar = (ScrollBar) node;
                if (scrollBar.getOrientation() == Orientation.HORIZONTAL) {
                    horizontalScrollBar = scrollBar;
                    break;
                }
            }
        }

        if (horizontalScrollBar != null) {
            // Сохраняем оригинальный контент
            content = getContent();

            // Создаем новый контейнер
            VBox newContainer = new VBox();
            newContainer.getChildren().addAll(horizontalScrollBar, content);

            // Удаляем оригинальный ScrollBar
            horizontalScrollBar.setManaged(false);
            horizontalScrollBar.setVisible(false);

            // Устанавливаем новый контейнер
            setContent(newContainer);

            // Настраиваем новый ScrollBar
            horizontalScrollBar.setManaged(true);
            horizontalScrollBar.setVisible(true);
            horizontalScrollBar.setPrefWidth(USE_COMPUTED_SIZE);
            horizontalScrollBar.setMaxWidth(Double.MAX_VALUE);

            // Привязываем значения
            horizontalScrollBar.valueProperty().bindBidirectional(this.hvalueProperty());
            horizontalScrollBar.minProperty().bind(this.hminProperty());
            horizontalScrollBar.maxProperty().bind(this.hmaxProperty());
            horizontalScrollBar.visibleAmountProperty().bind(this.hvalueProperty());
        }
    }
}