package org.fife.csveditor.undo;

import org.fife.csveditor.CsvEditor;
import org.fife.csveditor.CsvTable;

import javax.swing.*;
import java.util.List;

/**
 * An undo event for columns being added.
 */
public class AddColumnsUndoableEdit extends AbstractCsvUndoableEdit {

    private List<String> columns;
    private boolean before;

    public AddColumnsUndoableEdit(CsvEditor app, List<String> columns, boolean before) {
        super(app);
        this.columns = columns;
        this.before = before;
    }

    @Override
    protected boolean isStateValid(boolean undo) {

        CsvTable table = getSelectedCsvTable();
        boolean valid = true;

        if (undo) {
            for (String column : columns) {
                if (table.getColumn(column) == null) {
                    valid = false;
                    break;
                }
            }
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
        getSelectedCsvTable().addColumns(columns.size(), before, false);
    }

    @Override
    public void undo() {

        if (!isStateValid(true)) { // Should never happen
            UIManager.getLookAndFeel().provideErrorFeedback(null);
            return;
        }

        super.undo();

        getSelectedCsvTable().removeColumns(columns);
    }
}
