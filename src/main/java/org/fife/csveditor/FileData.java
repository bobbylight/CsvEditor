package org.fife.csveditor;

import javax.swing.table.DefaultTableModel;
import java.nio.file.Path;

/**
 * Information about an open CSV file.
 */
public class FileData {

    private Path path;
    private boolean dirty;
    private DefaultTableModel model;

    public FileData(Path path) {
        this.path = path;
    }

    public DefaultTableModel getModel() {
        return model;
    }

    public Path getPath() {
        return path;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public void setModel(DefaultTableModel model) {
        this.model = model;
    }

    public void setPath(Path path) {
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
