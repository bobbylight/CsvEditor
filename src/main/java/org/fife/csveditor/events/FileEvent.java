package org.fife.csveditor.events;

import java.nio.file.Path;
import java.util.EventObject;

/**
 * Event fired when a file is opened or closed.
 */
public class FileEvent extends EventObject {

    private Type type;
    private Path path;

    public FileEvent(Object source, Type type, Path path) {
        super(source);
        this.type = type;
        this.path = path;
    }

    public Path getPath() {
        return path;
    }

    public Type getType() {
        return type;
    }

    /**
     * Denotes the action taken on a file.
     */
    public enum Type {
        OPENED,
        CLOSED
    }
}
