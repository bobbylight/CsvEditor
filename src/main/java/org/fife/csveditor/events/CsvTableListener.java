package org.fife.csveditor.events;

import java.util.EventListener;

/**
 * Listens for events on a {@link org.fife.csveditor.CsvTable}.
 */
public interface CsvTableListener extends EventListener {

    // TODO: Consider per-event type methods if more event types are created
    void tableChanged(CsvTableEvent e);
}
