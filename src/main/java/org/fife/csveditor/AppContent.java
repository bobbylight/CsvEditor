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

public class AppContent extends JPanel {

    private CsvEditor app;
    private List<FileData> fileData;
    private JTabbedPane tabbedPane;

    public AppContent(CsvEditor app) {

        this.app = app;
        fileData = new ArrayList<>();

        TabbedPaneListener tpl = new TabbedPaneListener();
        tabbedPane = new JTabbedPane();
        tpl.install(tabbedPane);
        setLayout(new BorderLayout());
        add(tabbedPane);

        open(Paths.get("test.csv"));
    }

    public void addColumns(int count, boolean before) {

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

        FileData fileData = this.fileData.get(selectedIndex);
        DefaultTableModel model = fileData.getModel();
        model.addColumn("column" + (model.getColumnCount() + 1));
        table.moveColumn(table.getColumnCount() - 1, col);
    }

    public void addRows(int count, boolean above) {

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

        FileData fileData = this.fileData.get(selectedIndex);
        DefaultTableModel model = fileData.getModel();
        int columnCount = model.getColumnCount();

        for (int i = 0; i < count; i++) {
            model.insertRow(row + i, new Object[columnCount]);
        }
        table.getSelectionModel().setSelectionInterval(row + count - 1, row + count - 1);
    }

    /**
     * Returns the selected tab's CSV editor, or {@code null} if no tabs are open.
     *
     * @return The selected CSV editor, or {@code null}.
     */
    public CsvTable getSelectedCsvTable() {
        int selectedIndex = tabbedPane.getSelectedIndex();
        if (selectedIndex > -1) {
            JScrollPane sp = (JScrollPane) tabbedPane.getComponentAt(selectedIndex);
            return (CsvTable) sp.getViewport().getView();
        }
        return null;
    }

    public void open(Path file) {

        List<String[]> rows;

        if (Files.exists(file)) {
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
            String[] columnHeaders = { "column1", "column2", "column3" };
            rows.add(columnHeaders);
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
        CsvTable table = new CsvTable(app, model);
        JScrollPane sp = new JScrollPane(table);
        sp.setRowHeaderView(new RowHeader(app, table));
        tabbedPane.addTab(file.toFile().getName(), sp);
        tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);

        FileData fileData = new FileData(file);
        fileData.setModel(model);
        this.fileData.add(fileData);
    }

    private static List<String[]> getDataInProperFormat(DefaultTableModel model) {

        List<String[]> data = new ArrayList<>();

        Vector vecData = model.getDataVector();
        Enumeration e = vecData.elements();
        while (e.hasMoreElements()) {

            Vector rowVec = (Vector)e.nextElement();

            List<String> row = new ArrayList<>();
            for (int i = 0; i < rowVec.size(); i++) {
                String value = rowVec.get(i) == null ? "" : rowVec.get(i).toString();
                row.add(value);
            }

            data.add(row.toArray(new String[row.size()]));
        }

        return data;
    }

    public void removeSelectedRows() {

        int selectedIndex = tabbedPane.getSelectedIndex();
        if (selectedIndex == -1) {
            UIManager.getLookAndFeel().provideErrorFeedback(this);
            return;
        }

        CsvTable table = getSelectedCsvTable();

        FileData fileData = this.fileData.get(selectedIndex);
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

    public void saveSelectedTab() {

        int selectedIndex = tabbedPane.getSelectedIndex();
        FileData fileData = this.fileData.get(selectedIndex);

        CSVWriter w;
        try {

            w = new CSVWriter(Files.newBufferedWriter(fileData.getPath(), Charset.forName("UTF-8")));

            DefaultTableModel model = fileData.getModel();
            List<String[]> lines = getDataInProperFormat(model);
            w.writeAll(lines, false);
            w.close();

        } catch (IOException ioe) {
            app.displayException(ioe);
        }
    }

    public void selectRow(int row) {

        CsvTable table = getSelectedCsvTable();
        if (table == null) {
            UIManager.getLookAndFeel().provideErrorFeedback(this);
        }
        table.setSelectedRows(row, row);
    }

    private class TabbedPaneListener extends MouseAdapter {

        public void install(JTabbedPane tabbedPane) {
            tabbedPane.addMouseListener(this);
        }

        @Override
        public void mouseClicked(MouseEvent e) {

            if (SwingUtilities.isMiddleMouseButton(e)) {

                int clickedTab = tabbedPane.indexAtLocation(e.getX(), e.getY());
                if (clickedTab > -1) {
                    tabbedPane.removeTabAt(clickedTab);
                }
            }
        }
    }
}