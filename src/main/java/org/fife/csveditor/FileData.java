package org.fife.csveditor;

import javax.swing.table.DefaultTableModel;
import java.nio.file.Path;

/**
 * Information about an open CSV file.
 */
class FileData {

    private Path path;
    private boolean dirty;
    private DefaultTableModel model;

    FileData(Path path) {
        this.path = path;
    }

    String getFileName() {
        return path != null ? path.toFile().getName() : "Untitled.csv";
    }

    DefaultTableModel getModel() {
        return model;
    }

    Path getPath() {
        return path;
    }

    boolean isDirty() {
        return dirty;
    }

    boolean isPreviouslySaved() {
        return path != null;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    void setModel(DefaultTableModel model) {
        this.model = model;
    }

    void setPath(Path path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "[FileData: " +
                "path=" + getPath() +
                ", dirty=" + isDirty() +
                "]";
    }
}
