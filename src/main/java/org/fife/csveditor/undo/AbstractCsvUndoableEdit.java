package org.fife.csveditor.undo;

import org.fife.csveditor.CsvEditor;
import org.fife.csveditor.CsvTable;

import javax.swing.undo.AbstractUndoableEdit;

/**
 * Base class for undo events in the application.
 */
abstract class AbstractCsvUndoableEdit extends AbstractUndoableEdit {

    protected CsvEditor app;

    AbstractCsvUndoableEdit(CsvEditor app) {
        this.app = app;
    }

    CsvTable getSelectedCsvTable() {
        return app.getAppContent().getSelectedCsvTable();
    }

    /**
     * Returns whether the active editor is in a valid state to undo or redo this edit.  If this method
     * returns {@code false}, then there is a bug in the application.
     *
     * @param undo Whether this is an undo operation (as opposed to a redo).
     * @return Whether this edit can safely be undone or redone.
     */
    protected abstract boolean isStateValid(boolean undo);
}
