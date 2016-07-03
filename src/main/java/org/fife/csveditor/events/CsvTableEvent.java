package org.fife.csveditor.events;

import org.fife.csveditor.CsvTable;

import java.util.EventObject;

/**
 * The event type fired by {@link org.fife.csveditor.CsvTable} for various reasons.
 */
public class CsvTableEvent extends EventObject {

    private Type type;

    public CsvTableEvent(CsvTable source, Type type) {
        super(source);
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public enum Type {
        DIRTY_STATE_CHANGED
    }
}
