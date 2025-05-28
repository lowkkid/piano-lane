package by.fpmi.bsu.pianolane.common.ui;

import by.fpmi.bsu.pianolane.common.util.enums.FileOperation;
import java.io.File;
import java.util.function.Consumer;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SaveButton extends FileOperationButton {

    private static final String ERROR_MESSAGE_HEADER = "Error while saving file";

    private Consumer<String> onSave;
    private String initialFileName = "Untitled";
    private String fileExtension = "";

    public SaveButton() {
        super();
    }

    public void setFileExtension(String value) {
        if (!value.startsWith(".")) {
            value = "." + value;
        }
        this.fileExtension = value;
    }

    @Override
    protected void handleAction() {
        File file = showFileChooser();
        if (file != null) {
            try {
                onSave.accept(ensureExtension(file.getAbsolutePath()));
            } catch (Exception e) {
                showErrorMessage(ERROR_MESSAGE_HEADER, e.getMessage());
            }
        }
    }

    @Override
    protected ExtendedFileChooser extendFileChooser() {
        return ExtendedFileChooser.builder()
                .fileOperation(FileOperation.SAVE)
                .window(getScene().getWindow())
                .withAllFilesExtensionFilter()
                .withExtensionFilters(extensionFilters)
                .initialFileName(initialFileName + fileExtension)
                .build();
    }

    private String ensureExtension(String path) {
        return path.endsWith(fileExtension) ? path : path + fileExtension;
    }
}
