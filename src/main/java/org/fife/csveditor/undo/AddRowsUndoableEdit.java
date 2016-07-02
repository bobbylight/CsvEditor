package org.fife.csveditor.undo;

import org.fife.csveditor.CsvEditor;
import org.fife.csveditor.CsvTable;

import javax.swing.*;
import java.util.List;

/**
 * An undo event for rows being added.
 */
public class AddRowsUndoableEdit extends AbstractCsvUndoableEdit {

    private int row;
    private boolean above;
    private int count;

    public AddRowsUndoableEdit(CsvEditor app, int row, boolean above, int count) {
        super(app);
        this.row = row;
        this.above = above;
        this.count = count;
    }

    @Override
    protected boolean isStateValid(boolean undo) {

        CsvTable table = getSelectedCsvTable();
        boolean valid = true;

        if (undo) {
            int start = row;
            if (!above) {
                start++;
            }
            valid = (start + count) <= table.getRowCount();
        }
        else {
            valid = row < table.getRowCount();
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
        getSelectedCsvTable().addRows(row, count, above, false);
    }

    @Override
    public void undo() {

        if (!isStateValid(true)) { // Should never happen
            UIManager.getLookAndFeel().provideErrorFeedback(null);
            return;
        }

        super.undo();
        getSelectedCsvTable().removeRows(row, count, above, false);
    }
}
