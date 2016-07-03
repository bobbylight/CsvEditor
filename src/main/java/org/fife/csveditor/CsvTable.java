package org.fife.csveditor;

import org.apache.commons.lang3.StringUtils;
import org.fife.csveditor.events.CsvTableEvent;
import org.fife.csveditor.events.CsvTableListener;
import org.fife.csveditor.undo.AddColumnsUndoableEdit;
import org.fife.csveditor.undo.AddRowsUndoableEdit;
import org.fife.csveditor.undo.CellUndoableEdit;
import org.fife.csveditor.undo.RemoveRowsUndoableEdit;
import org.fife.ui.UIUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.text.AbstractDocument;
import javax.swing.text.JTextComponent;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;
import java.util.Vector;

/**
 * An editor for a CSV file.
 */
// Smart JTable resizing taken from:
// http://stackoverflow.com/questions/15234691/enabling-auto-resize-of-jtable-only-if-it-fit-viewport/15240806#15240806
public class CsvTable extends JTable {

    private CsvEditor app;
    private FileData fileData;
    private boolean dirty;
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
        setAutoResizeMode(AUTO_RESIZE_OFF);
        setRowHeight(20);

        Listener listener = new Listener();
        undoManager = new UndoManager();

        JTableHeader header = getTableHeader();
        header.addMouseListener(listener);
    }

    void addColumns(int count, boolean before) {
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

    public void addCsvTableListener(CsvTableListener l) {
        listenerList.add(CsvTableListener.class, l);
    }

    void addRows(int count, boolean above) {
        addRows(getSelectedRow(), count, above);
    }

    void addRows(int row, int count, boolean above) {
        addRows(row, count, above, true);
    }

    public void addRows(int row, int count, boolean above, boolean undoable) {

        if (row < 0) { // e.g. -1 from getSelectedRow() with no row
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
        changeSelection(row, 0, false, false);
        changeSelection(row + count - 1, columnModel.getColumnCount() - 1, false, true);
        setDirty(true);

        if (undoable) {
            undoManager.addEdit(new AddRowsUndoableEdit(app, row, above, count));
        }
    }

    @Override
    public void doLayout() {

        TableColumn resizingColumn = null;

        if (tableHeader != null) {
            resizingColumn = tableHeader.getResizingColumn();
        }

        //  Viewport size changed. May need to increase columns widths
        if (resizingColumn == null) {
            setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
            super.doLayout();
        }

        //  Specific column resized. Reset preferred widths
        else {

            TableColumnModel tcm = getColumnModel();
            for (int i = 0; i < tcm.getColumnCount(); i++) {
                TableColumn tc = tcm.getColumn(i);
                tc.setPreferredWidth( tc.getWidth() );
            }

            // Columns don't fill the viewport, invoke default layout
            if (tcm.getTotalColumnWidth() < getParent().getWidth()) {
                setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
            }
            super.doLayout();
        }

        setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    }

    private void fireEvent(CsvTableEvent.Type type) {

        CsvTableEvent e = new CsvTableEvent(this, type);

        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();

        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == CsvTableListener.class) {
                ((CsvTableListener)listeners[i+1]).tableChanged(e);
            }
        }
    }

    FileData getFileData() {
        return fileData;
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        return getPreferredSize().width < getParent().getWidth();
    }

    public void insertRows(int row, List<Vector<?>> rowData, boolean undoable) {

        // This method currently isn't called except as part of an undo or redo
        assert !undoable;

        CsvTableModel model = (CsvTableModel)getModel();
        for (int i = 0; i < rowData.size(); i++) {
            model.insertRow(row + i, rowData.get(i));
        }
        changeSelection(row, 0, false, false);
        changeSelection(row + rowData.size() - 1, columnModel.getColumnCount() - 1, false, true);

        if (undoable) {
            throw new RuntimeException("Undoability of insertRows() is not yet implemented");
        }
    }

    public boolean isDirty() {
        return dirty;
    }

    void redo() {
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
        setDirty(true);
    }

    public void removeCsvTableListener(CsvTableListener l) {
        listenerList.remove(CsvTableListener.class, l);
    }

    public void removeRows(int row, int count, boolean above, boolean undoable) {

        if (row < 0) { // e.g. -1 from getSelectedRow() with no row
            UIManager.getLookAndFeel().provideErrorFeedback(this);
            return;
        }
        if (!above) {
            row++;
        }

        List<Vector<?>> removedRows = null;
        if (undoable) {
            removedRows = new ArrayList<>();
        }

        CsvTableModel model = (CsvTableModel)getModel();
        for (int i = 0; i < count; i++) {
            if (undoable) {
                removedRows.add((Vector<?>)model.getDataVector().get(row));
            }
            model.removeRow(row);
        }

        // Don't let them have a 0-row table
        if (getRowCount() == 0) {
            model.addRow(new Object[getColumnCount()]);
        }
        setDirty(true);

        if (undoable) {
            undoManager.addEdit(new RemoveRowsUndoableEdit(app, row, removedRows));
        }
    }

    void removeSelectedRows() {
        int minSelectionIndex = getSelectionModel().getMinSelectionIndex();
        int maxSelectionIndex = getSelectionModel().getMaxSelectionIndex();
        removeRows(minSelectionIndex, maxSelectionIndex - minSelectionIndex + 1, true, true);
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
        fireEvent(CsvTableEvent.Type.DIRTY_STATE_CHANGED);
    }

    void setSelectedRows(int min, int max) {
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
            setDirty(true);
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

    void undo() {
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