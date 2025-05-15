package by.fpmi.bsu.pianolane.common.ui;

import static javafx.stage.FileChooser.ExtensionFilter;

import by.fpmi.bsu.pianolane.common.util.enums.FileOperation;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ExtendedFileChooser {

    private final FileChooser fileChooser;
    private final Window window;
    private final FileOperation fileOperation;

    public File show() {
        return switch (fileOperation) {
            case OPEN -> fileChooser.showOpenDialog(window);
            case SAVE -> fileChooser.showSaveDialog(window);
        };
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private static final ExtensionFilter ALL_FILES_FILTER
                = new ExtensionFilter("All files", "*.*");
        private static final File HOME_DIRECTORY = new File(System.getProperty("user.home"));

        private final List<ExtensionFilter> extensionFilters = new ArrayList<>();
        private boolean withAllFilesExtensionFilter = false;
        private Window window;
        private FileOperation fileOperation;
        private File initialDirectory;
        private String initialFileName;

        public Builder withExtensionFilters(List<ExtensionFilter> extensionFilter) {
            extensionFilters.addAll(extensionFilter);
            return this;
        }

        public Builder withAllFilesExtensionFilter() {
            withAllFilesExtensionFilter = true;
            return this;
        }

        public Builder window(Window window) {
            this.window = window;
            return this;
        }

        public Builder fileOperation(FileOperation fileOperation) {
            this.fileOperation = fileOperation;
            return this;
        }

        public Builder initialDirectory(File initialDirectory) {
            this.initialDirectory = initialDirectory;
            return this;
        }

        public Builder initialFileName(String initialFileName) {
            this.initialFileName = initialFileName;
            return this;
        }

        public ExtendedFileChooser build() {
            Objects.requireNonNull(fileOperation, "You must specify a file operation when creating file chooser");
            Objects.requireNonNull(window, "You must specify a window when creating file chooser");
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().addAll(extensionFilters);
            if (withAllFilesExtensionFilter) {
                fileChooser.getExtensionFilters().add(ALL_FILES_FILTER);
            }
            fileChooser.setInitialDirectory(Objects.requireNonNullElse(initialDirectory, HOME_DIRECTORY));
            fileChooser.setInitialFileName(initialFileName);
            return new ExtendedFileChooser(fileChooser, window, fileOperation);
        }
    }
}
