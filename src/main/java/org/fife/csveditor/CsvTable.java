package org.fife.csveditor;

import org.apache.commons.lang3.StringUtils;
import org.fife.csveditor.undo.AddColumnsUndoableEdit;
import org.fife.csveditor.undo.CellUndoableEdit;
import org.fife.ui.UIUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.text.AbstractDocument;
import javax.swing.text.JTextComponent;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

/**
 * An editor for a CSV file.
 */
public class CsvTable extends JTable {

    private CsvEditor app;
    private FileData fileData;
    private Listener listener;
    private JPopupMenu popupMenu;
    private UndoManager undoManager;

    CsvTable(CsvEditor app, FileData fileData) {

        super(fileData.getModel());
        this.app = app;
        this.fileData = fileData;

        setCellSelectionEnabled(true);
        setRowSelectionAllowed(true);
        getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        setDefaultEditor(Object.class, new CellEditor());
        setFillsViewportHeight(true);

        listener = new Listener();
        undoManager = new UndoManager();

        JTableHeader header = getTableHeader();
        header.addMouseListener(listener);
    }

    public void addColumns(int count, boolean before) {
        addColumns(count, before, true);
    }

    public void addColumns(int count, boolean before, boolean undoable) {

        int col = getSelectedColumn();
        if (col == -1) {
            col = getColumnCount() - 1;
        }
        if (!before) {
            col++;
        }

        FileData fileData = getFileData();
        DefaultTableModel model = fileData.getModel();
        List<String> columnNames = new ArrayList<>();

        for (int i = 0; i < count; i++) {

            String newColumnName = "column" + (model.getColumnCount() + 1);

            // TODO: Create column and place in proper position, which is exceedingly difficult in Swing...
            model.addColumn(newColumnName);
            columnNames.add(newColumnName);
        }

        changeSelection(0, getColumnCount() - count, false, false);
        changeSelection(getRowCount() - 1, columnModel.getColumnCount() - 1, false, true);

        if (undoable) {
            undoManager.addEdit(new AddColumnsUndoableEdit(app, columnNames, before));
        }
    }

    public void addRows(int count, boolean above) {

        int row = getSelectedRow();
        if (row == -1) {
            UIManager.getLookAndFeel().provideErrorFeedback(this);
            return;
        }
        if (!above) {
            row++;
        }

        FileData fileData = getFileData();
        DefaultTableModel model = fileData.getModel();
        int columnCount = model.getColumnCount();

        for (int i = 0; i < count; i++) {
            model.insertRow(row + i, new Object[columnCount]);
        }
        getSelectionModel().setSelectionInterval(row + count - 1, row + count - 1);

        // TODO: Add undo event!!  Follow pattern in addColumns() above
    }

    public FileData getFileData() {
        return fileData;
    }

    public void redo() {
        if (undoManager.canRedo()) {
            undoManager.redo();
        }
        else {
            UIManager.getLookAndFeel().provideErrorFeedback(this);
        }
    }

    public void removeColumns(List<String> columns) {

        for (String columnName : columns) {

            TableColumn column = getColumn(columnName);
            int modelIndex = column.getModelIndex();
            ((CsvTableModel)getModel()).removeColumn(modelIndex);

            getColumnModel().removeColumn(column);
        }
    }

    public void setSelectedRows(int min, int max) {
        if (min < 0 || min >= getRowCount() ||
                max < 0 || max >= getRowCount()) {
            throw new IllegalArgumentException();
        }
        System.out.println("Setting selected rows to: " + min + " - " + max);
        changeSelection(min, 0, false, false);
        changeSelection(max, columnModel.getColumnCount() - 1, false, true);
    }

    /**
     * Sets a value in this table.  Overridden to add this edit to the undo stack.
     *
     * @param value The new value for the cell.
     * @param row The row.
     * @param column  The column.
     * @see #setValueAt(Object, int, int, boolean)
     */
    @Override
    public void setValueAt(Object value, int row, int column) {
        setValueAt(value, row, column, true);
    }

    /**
     * Sets a value in this table, optionally updating the undo stack.
     *
     * @param value The new value for the cell.
     * @param row The row.
     * @param column  The column.
     * @param undoable Whether or not to update the undo stack.
     * @see #setValueAt(Object, int, int)
     */
    public void setValueAt(Object value, int row, int column, boolean undoable) {

        String prevValue = (String)getValueAt(row, column);
        String newValue = value == null ? null : value.toString(); // Always a String, this is just for javac

        if (!StringUtils.equals(prevValue, newValue)) {
            super.setValueAt(value, row, column);
            if (undoable) {
                undoManager.addEdit(new CellUndoableEdit(app, row, column, prevValue, newValue));
            }
        }
    }

    private void showPopupMenu(JTableHeader source, int x, int y) {

        int index = source.columnAtPoint(new Point(x, y));

        System.out.println("Selecting column " + index);
        changeSelection(0, index, false, false);
        changeSelection(getRowCount(), index, false, true);
        if (popupMenu == null) {
            popupMenu = new JPopupMenu();
            popupMenu.add(UIUtil.newMenuItem(app.getAction(Actions.ADD_COLUMN_ACTION_KEY)));
        }

        popupMenu.show(this, x, y);
    }

    public void undo() {
        if (undoManager.canUndo()) {
            undoManager.undo();
        }
        else {
            UIManager.getLookAndFeel().provideErrorFeedback(this);
        }
    }

    public void updateUI() {
        super.updateUI();
        UIUtil.possiblyFixGridColor(this);
    }

    private static class CellEditor extends DefaultCellEditor implements FocusListener {

        private static final long serialVersionUID = 1L;

        private boolean editable;

        private CellEditor() {
            super(new JTextField());
            AbstractDocument doc = (AbstractDocument)((JTextComponent)editorComponent).getDocument();
            getComponent().addFocusListener(this);
            editable = true;
        }

        public void focusGained(FocusEvent e) {
            JTextField textField = (JTextField)getComponent();
            textField.selectAll();
        }

        public void focusLost(FocusEvent e) {
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean selected, int row, int column) {
            if (editable) {
                return super.getTableCellEditorComponent(table, value, selected, row, column);
            }
            return null;
        }

        @Override
        public boolean isCellEditable(EventObject e) {
            if (e instanceof KeyEvent) {
                return startWithKeyEvent((KeyEvent) e);
            }
            return super.isCellEditable(e);
        }

        public void setEditable(boolean editable) {
            this.editable = editable;
        }

        private boolean startWithKeyEvent(KeyEvent e) {
            if ((e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0) {
                return false;
            }
            // Could also check for a list of function key strokes here
            //return !excludes.contains(KeyStroke.getKeyStrokeForEvent(e));
            return true;
        }

        public boolean stopCellEditing() {
            return super.stopCellEditing();
        }

    }

    private class Listener extends MouseAdapter {

        public void mouseClicked(MouseEvent e) {
            if (e.getSource() instanceof JTableHeader && e.isPopupTrigger()) {
                showPopupMenu((JTableHeader)e.getSource(), e.getX(), e.getY());
            }
        }

        public void mouseReleased(MouseEvent e) {
            if (e.getSource() instanceof JTableHeader && e.isPopupTrigger()) {
                showPopupMenu((JTableHeader)e.getSource(), e.getX(), e.getY());
            }
        }
    }
}