package by.fpmi.bsu.pianolane.common.ui;

import by.fpmi.bsu.pianolane.common.util.enums.FileOperation;
import java.io.File;
import java.util.function.Consumer;
import javafx.scene.Node;
import lombok.Setter;

@Setter
public class OpenButton extends FileOperationButton {

    private static final String ERROR_MESSAGE_HEADER = "Error while opening file";

    private Consumer<String> onOpen;

    public OpenButton() {
        super();
    }

    public OpenButton(String text) {
        super(text);
    }

    public OpenButton(String text, Node graphic) {
        super(text, graphic);
    }

    @Override
    protected void handleAction() {
        File file = showFileChooser();
        if (file != null) {
            try {
                onOpen.accept(file.getAbsolutePath());
            } catch (Exception e) {
                showErrorMessage(ERROR_MESSAGE_HEADER, e.getMessage());
            }
        }
    }

    @Override
    protected ExtendedFileChooser extendFileChooser() {
        return ExtendedFileChooser.builder()
                .fileOperation(FileOperation.OPEN)
                .window(getScene().getWindow())
                .withAllFilesExtensionFilter()
                .withExtensionFilters(extensionFilters)
                .build();
    }
}

