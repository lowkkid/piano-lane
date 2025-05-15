package by.fpmi.bsu.pianolane.ui.button;

import static javafx.stage.FileChooser.ExtensionFilter;
import by.fpmi.bsu.pianolane.ui.ExtendedFileChooser;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;

public abstract class FileOperationButton extends Button {

    protected final List<ExtensionFilter> extensionFilters = new ArrayList<>();

    public FileOperationButton() {
        super();
        setOnAction(event -> handleAction());
    }

    public FileOperationButton(String text) {
        super(text);
        setOnAction(event -> handleAction());
    }

    public FileOperationButton(String text, Node graphic) {
        super(text, graphic);
        setOnAction(event -> handleAction());
    }

    public void addExtensionFilter(ExtensionFilter filter) {
        extensionFilters.add(filter);
    }

    protected abstract void handleAction();

    protected abstract ExtendedFileChooser extendFileChooser();

    protected void showErrorMessage(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    protected File showFileChooser() {
        return extendFileChooser().show();
    }
}
