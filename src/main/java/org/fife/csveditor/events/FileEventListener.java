package org.fife.csveditor.events;

import java.util.EventListener;

/**
 * Notified when files are opened and closed in the application.
 */
public interface FileEventListener extends EventListener {

    void fileClosed(FileEvent e);

    void fileOpened(FileEvent e);
}
