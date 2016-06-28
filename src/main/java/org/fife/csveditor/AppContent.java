package org.fife.csveditor;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Vector;

class AppContent extends JPanel {

    private CsvEditor app;
    private JTabbedPane tabbedPane;

    AppContent(CsvEditor app) {

        this.app = app;

        TabbedPaneListener tpl = new TabbedPaneListener();
        tabbedPane = new JTabbedPane();
        tpl.install(tabbedPane);
        setLayout(new BorderLayout());
        add(tabbedPane);

        open(null);
    }

    void addArbitraryRows(boolean above) {

        int selectedIndex = tabbedPane.getSelectedIndex();
        if (selectedIndex == -1) {
            UIManager.getLookAndFeel().provideErrorFeedback(this);
            return;
        }

        CsvTable table = getSelectedCsvTable();
        int row = table.getSelectedRow();
        if (row == -1) {
            UIManager.getLookAndFeel().provideErrorFeedback(this);
            return;
        }
        if (!above) {
            row++;
        }

        AddRowsDialog dialog = new AddRowsDialog(app, above);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);

        int rowCount = dialog.getRowCount();
        if (rowCount > -1) {
            addRows(rowCount, dialog.getRowLocation());
        }
    }

    void addColumns(int count, boolean before) {

        int selectedIndex = tabbedPane.getSelectedIndex();
        if (selectedIndex == -1) {
            UIManager.getLookAndFeel().provideErrorFeedback(this);
            return;
        }

        CsvTable table = getSelectedCsvTable();
        int col = table.getSelectedColumn();
        if (col == -1) {
            UIManager.getLookAndFeel().provideErrorFeedback(this);
            return;
        }
        if (!before) {
            col++;
        }

        FileData fileData = table.getFileData();
        DefaultTableModel model = fileData.getModel();
        String newColumnName = "column" + (model.getColumnCount() + 1);

        // TODO: Create column and place in proper position, which is exceedingly difficult in Swing...
        model.addColumn(newColumnName);
    }

    void addRows(int count, boolean above) {

        int selectedIndex = tabbedPane.getSelectedIndex();
        if (selectedIndex == -1) {
            UIManager.getLookAndFeel().provideErrorFeedback(this);
            return;
        }

        CsvTable table = getSelectedCsvTable();
        int row = table.getSelectedRow();
        if (row == -1) {
            UIManager.getLookAndFeel().provideErrorFeedback(this);
            return;
        }
        if (!above) {
            row++;
        }

        FileData fileData = table.getFileData();
        DefaultTableModel model = fileData.getModel();
        int columnCount = model.getColumnCount();

        for (int i = 0; i < count; i++) {
            model.insertRow(row + i, new Object[columnCount]);
        }
        table.getSelectionModel().setSelectionInterval(row + count - 1, row + count - 1);
    }

    void closeCurrentTab() {
        closeTab(tabbedPane.getSelectedIndex());
    }

    private void closeTab(int index) {

        if (index < 0 || index >= tabbedPane.getTabCount()) {
            UIManager.getLookAndFeel().provideErrorFeedback(this);
            return;
        }

        // TODO: Check dirty state

        tabbedPane.removeTabAt(index);

        if (tabbedPane.getTabCount() == 0) {
            open(null);
        }
    }

    /**
     * Returns the selected tab's CSV editor, or {@code null} if no tabs are open.
     *
     * @return The selected CSV editor, or {@code null}.
     */
    CsvTable getSelectedCsvTable() {
        int selectedIndex = tabbedPane.getSelectedIndex();
        if (selectedIndex > -1) {
            JScrollPane sp = (JScrollPane) tabbedPane.getComponentAt(selectedIndex);
            return (CsvTable) sp.getViewport().getView();
        }
        return null;
    }

    /**
     * Opens a file.
     *
     * @param file The file to open.  If this is {@code null}, an empty CSV file is opened.
     */
    void open(Path file) {

        List<String[]> rows;

        if (file != null && Files.exists(file)) {
            CSVReader r;
            try {
                r = new CSVReader(Files.newBufferedReader(file, Charset.forName("UTF-8")));
                rows = r.readAll();
                r.close();
            } catch (IOException ioe) {
                app.displayException(ioe);
                return;
            }
        }
        else {
            rows = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                rows.add(new String[3]);
            }
        }

        String[][] arrayRowData = new String[rows.size()][];
        for (int i = 0; i < rows.size(); i++) {
            arrayRowData[i] = rows.get(i);
        }

        String[] columnNames = new String[rows.get(0).length];
        for (int i = 0; i < columnNames.length; i++) {
            columnNames[i] = "column" + (i + 1);
        }

        DefaultTableModel model = new DefaultTableModel(arrayRowData, columnNames);
        FileData fileData = new FileData(file);
        fileData.setModel(model);
        CsvTable table = new CsvTable(app, fileData);
        JScrollPane sp = new JScrollPane(table);
        sp.setRowHeaderView(new RowHeader(app, table));
        tabbedPane.addTab(fileData.getFileName(), sp);
        tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
    }

    private static List<String[]> getDataInProperFormat(DefaultTableModel model) {

        List<String[]> data = new ArrayList<>();

        Vector vecData = model.getDataVector();
        Enumeration e = vecData.elements();
        while (e.hasMoreElements()) {

            Vector rowVec = (Vector)e.nextElement();

            List<String> row = new ArrayList<>();
            for (Object aRowVec : rowVec) {
                String value = aRowVec == null ? "" : aRowVec.toString();
                row.add(value);
            }

            data.add(row.toArray(new String[row.size()]));
        }

        return data;
    }

    void removeSelectedRows() {

        int selectedIndex = tabbedPane.getSelectedIndex();
        if (selectedIndex == -1) {
            UIManager.getLookAndFeel().provideErrorFeedback(this);
            return;
        }

        CsvTable table = getSelectedCsvTable();

        FileData fileData = table.getFileData();
        DefaultTableModel model = fileData.getModel();
        int columnCount = table.getColumnCount();

        int minSelectionIndex = table.getSelectionModel().getMinSelectionIndex();
        int maxSelectionIndex = table.getSelectionModel().getMaxSelectionIndex();
        int minRow = minSelectionIndex;// / columnCount;
        int maxRow = maxSelectionIndex;// / columnCount;
        if (minRow == -1) {
            UIManager.getLookAndFeel().provideErrorFeedback(this);
            return;
        }
        for (int i = minRow; i <= maxRow; i++) {
            model.removeRow(minRow);
        }

        // Don't let them have a 0-row table
        if (table.getRowCount() == 0) {
            model.addRow(new Object[columnCount]);
        }
    }

    boolean saveSelectedTab() {

        FileData fileData = getSelectedCsvTable().getFileData();
        if (!fileData.isPreviouslySaved()) {
            app.getAction(Actions.SAVE_AS_ACTION_KEY).actionPerformed(null);
            return true;
        }

        CSVWriter w;
        try {

            w = new CSVWriter(Files.newBufferedWriter(fileData.getPath(), Charset.forName("UTF-8")));

            DefaultTableModel model = fileData.getModel();
            List<String[]> lines = getDataInProperFormat(model);
            w.writeAll(lines, false);
            w.close();

        } catch (IOException ioe) {
            app.displayException(ioe);
            return false;
        }

        return true;
    }

    void selectRow(int row) {

        CsvTable table = getSelectedCsvTable();
        if (table == null) {
            UIManager.getLookAndFeel().provideErrorFeedback(this);
            return;
        }
        table.setSelectedRows(row, row);
    }

    private class TabbedPaneListener extends MouseAdapter {

        void install(JTabbedPane tabbedPane) {
            tabbedPane.addMouseListener(this);
        }

        @Override
        public void mouseClicked(MouseEvent e) {

            if (SwingUtilities.isMiddleMouseButton(e)) {

                int clickedTab = tabbedPane.indexAtLocation(e.getX(), e.getY());
                if (clickedTab > -1) {
                    closeTab(clickedTab);
                }
            }
        }
    }
}