package org.fife.csveditor;

import org.fife.ui.UIUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.text.AbstractDocument;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.*;
import java.util.EventObject;

public class CsvTable extends JTable {

    private CsvEditor app;
    private Listener listener;
    private JPopupMenu popupMenu;

    CsvTable(CsvEditor app, DefaultTableModel model) {

        super(model);
        this.app = app;

        setCellSelectionEnabled(true);
        setRowSelectionAllowed(true);
        getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        setDefaultEditor(Object.class, new CellEditor());

        listener = new Listener();

        JTableHeader header = getTableHeader();
        header.addMouseListener(listener);
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

    public void updateUI() {
        super.updateUI();
        UIUtil.possiblyFixGridColor(this);
    }

    private static class CellEditor extends DefaultCellEditor implements FocusListener {

        private static final long serialVersionUID = 1L;

        private boolean editable;

        public CellEditor() {
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