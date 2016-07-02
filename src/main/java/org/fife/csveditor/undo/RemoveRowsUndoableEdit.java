package org.fife.csveditor.undo;

import org.fife.csveditor.CsvEditor;
import org.fife.csveditor.CsvTable;

import javax.swing.*;
import java.util.List;
import java.util.Vector;

/**
 * An undo event for rows being removed.
 */
public class RemoveRowsUndoableEdit extends AbstractCsvUndoableEdit {

    private int row;
    private List<Vector<?>> removedRows;

    public RemoveRowsUndoableEdit(CsvEditor app, int row, List<Vector<?>> removedRows) {
        super(app);
        this.row = row;
        this.removedRows = removedRows;
    }

    @Override
    protected boolean isStateValid(boolean undo) {

        CsvTable table = getSelectedCsvTable();
        boolean valid = true;

        if (undo) {
            valid = row < table.getRowCount();
        }
        else {
            valid = (row + removedRows.size()) <= table.getRowCount();
        }
        return valid;
    }

    @Override
    public void redo() {

        if (!isStateValid(false)) { // Should never happen
            UIManager.getLookAndFeel().provideErrorFeedback(null);
            return;
        }

        super.redo();
        // Here we're just assuming we're sound, and the rows are the same.
        getSelectedCsvTable().removeRows(row, removedRows.size(), true, false);
    }

    @Override
    public void undo() {

        if (!isStateValid(true)) { // Should never happen
            UIManager.getLookAndFeel().provideErrorFeedback(null);
            return;
        }

        super.undo();
        getSelectedCsvTable().insertRows(row, removedRows, false);
    }
}
