package org.fife.csveditor.undo;

import org.fife.csveditor.CsvEditor;
import org.fife.csveditor.CsvTable;

import javax.swing.*;

/**
 * An undo event for a single cell being modified.
 */
public class CellUndoableEdit extends AbstractCsvUndoableEdit {

    private int row;
    private int col;
    private String oldValue;
    private String newValue;

    public CellUndoableEdit(CsvEditor app, int row, int col, String oldValue, String newValue) {
        super(app);
        this.row = row;
        this.col = col;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    @Override
    protected boolean isStateValid(boolean undo) {
        CsvTable table = getSelectedCsvTable();
        return row >= 0 && row < table.getRowCount() && col >= 0 && col < table.getColumnCount();
    }

    @Override
    public void redo() {

        if (!isStateValid(false)) { // Should never happen
            UIManager.getLookAndFeel().provideErrorFeedback(null);
            return;
        }

        super.redo();
        setCellValue(row, col, newValue);
    }

    private void setCellValue(int row, int col, String value) {
        CsvTable csvTable = getSelectedCsvTable();
        csvTable.setValueAt(value, row, col, false);
        csvTable.setRowSelectionInterval(row, row);
        csvTable.setColumnSelectionInterval(col, col);
    }

    @Override
    public void undo() {

        if (!isStateValid(true)) { // Should never happen
            UIManager.getLookAndFeel().provideErrorFeedback(null);
            return;
        }

        super.undo();
        setCellValue(row, col, oldValue);
    }
}
