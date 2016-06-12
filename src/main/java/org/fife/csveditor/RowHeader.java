package org.fife.csveditor;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RowHeader extends JList implements TableModelListener {

    private CsvEditor app;
    private CsvTable table;
    private RowHeaderListModel model;
    private JPopupMenu contextMenu;
    private Listener listener;

    private static final Border CELL_BORDER = BorderFactory.createEmptyBorder(0,5,0,5);

    public RowHeader(CsvEditor app, CsvTable table) {

        this.app = app;
        this.table = table;
        model = new RowHeaderListModel();
        setModel(model);
        setFocusable(false);
        setFont(table.getFont());
        setFixedCellHeight(table.getRowHeight());
        setCellRenderer(new CellRenderer());
        setBorder(new RowHeaderBorder());
        setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        syncRowCount(); // Initialize to initial size of the table
        table.getModel().addTableModelListener(this);

        listener = new Listener();
        addMouseListener(listener);
    }

    @Override
    public void addSelectionInterval(int anchor, int lead) {
        super.addSelectionInterval(anchor, lead);
        int min = Math.min(anchor, lead);
        int max = Math.max(anchor, lead);
        table.setSelectedRows(min, max);
    }

    private JPopupMenu getContextMenu() {
        if (contextMenu == null) {
            contextMenu = new JPopupMenu();
            contextMenu.add(new JMenuItem(app.getAction(Actions.ADD_ROW_ABOVE_ACTION_KEY)));
            contextMenu.add(new JMenuItem(app.getAction(Actions.REMOVE_ROWS_ACTION_KEY)));
        }
        return contextMenu;
    }

    public void setSelectionInterval(int anchor, int lead) {
        super.setSelectionInterval(anchor, lead);
        int min = Math.min(anchor, lead);
        int max = Math.max(anchor, lead);
        // Table may be showing 0 bytes, but we're showing 1 row header
        if (max < table.getRowCount()) {
            table.setSelectedRows(min, max);
        }
    }

    private void syncRowCount() {
        if (table.getRowCount() != model.getSize()) {
            // Always keep 1 row, even if showing 0 bytes in editor
            model.setSize(Math.max(1, table.getRowCount()));
        }
    }

    @Override
    public void tableChanged(TableModelEvent e) {
        syncRowCount();
    }

    private class CellRenderer extends DefaultListCellRenderer {

        private static final long serialVersionUID = 1L;

        public CellRenderer() {
            setHorizontalAlignment(JLabel.RIGHT);
        }

        public Component getListCellRendererComponent(JList list, Object value,
                                                      int index, boolean selected, boolean hasFocus) {
            // Never paint cells as "selected."
            super.getListCellRendererComponent(list, value, index,
                    false, hasFocus);
            setBorder(CELL_BORDER);
//			setBackground(table.getBackground());
            return this;
        }

    }

    private class Listener extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.isPopupTrigger()) {
                showPopupMenu(e);
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (e.isPopupTrigger()) {
                showPopupMenu(e);
            }
        }

        private void showPopupMenu(MouseEvent e) {
            int row = e.getY() / getFixedCellHeight();
            app.getAppContent().selectRow(row);
            getContextMenu().show(RowHeader.this, e.getX(), e.getY());
        }
    }

    private static class RowHeaderListModel extends AbstractListModel {

        private static final long serialVersionUID = 1L;

        private int size;

        public Object getElementAt(int index) {
            return Integer.toString(index + 1);
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            int old = this.size;
            this.size = size;
            int diff = size - old;
            if (diff>0) {
                fireIntervalAdded(this, old, size-1);
            }
            else if (diff<0) {
                fireIntervalRemoved(this, size+1, old-1);
            }
        }

    }

    private class RowHeaderBorder extends EmptyBorder {

        public RowHeaderBorder() {
            super(0, 0, 0, 2);
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            x = x + width - this.right;
//	    	g.setColor(table.getBackground());
//	    	g.fillRect(x,y, width,height);
            g.setColor(table.getGridColor());
            g.drawLine(x,y, x,y+height);
        }

    }
}
