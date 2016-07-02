package org.fife.csveditor;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import org.fife.csveditor.events.FileEvent;
import org.fife.csveditor.events.FileEventListener;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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

/**
 * The haert of the application.  This is essentially the "controller," with all the main business logic.
 */
public class AppContent extends JPanel {

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
        table.addColumns(count, before);
    }

    void addFileEventListener(FileEventListener l) {
        listenerList.add(FileEventListener.class, l);
    }

    void addRows(int count, boolean above) {

        int selectedIndex = tabbedPane.getSelectedIndex();
        if (selectedIndex == -1) {
            UIManager.getLookAndFeel().provideErrorFeedback(this);
            return;
        }

        getSelectedCsvTable().addRows(count, above);
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

        CsvTable csvTable = getCsvTable(index);
        tabbedPane.removeTabAt(index);
        fireFileEvent(FileEvent.Type.CLOSED, csvTable.getFileData().getPath());

        if (tabbedPane.getTabCount() == 0) {
            open(null);
        }
    }

    private void fireFileEvent(FileEvent.Type type, Path path) {

        FileEvent e = new FileEvent(app, type, path);

        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == FileEventListener.class) {
                FileEventListener l = (FileEventListener)listeners[i+1];
                if (type == FileEvent.Type.OPENED) {
                    l.fileOpened(e);
                }
                else {
                    l.fileClosed(e);
                }
            }
        }
    }

    /**
     * Returns the CSV editor at the specified index.
     *
     * @param index The index of the CSV editor.
     * @return The editor.
     * @see #getSelectedCsvTable()
     */
    private CsvTable getCsvTable(int index) {
        JScrollPane sp = (JScrollPane) tabbedPane.getComponentAt(index);
        return (CsvTable) sp.getViewport().getView();
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

    /**
     * Returns the selected tab's CSV editor, or {@code null} if no tabs are open.
     *
     * @return The selected CSV editor, or {@code null}.
     * @see #getCsvTable(int)
     */
    public CsvTable getSelectedCsvTable() {
        int selectedIndex = tabbedPane.getSelectedIndex();
        if (selectedIndex > -1) {
            return getCsvTable(selectedIndex);
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

        if (file != null) {
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
                promptToCreateFile(file);
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

        CsvTableModel model = new CsvTableModel(arrayRowData, columnNames);
        FileData fileData = new FileData(file);
        fileData.setModel(model);
        CsvTable table = new CsvTable(app, fileData);
        JScrollPane sp = new JScrollPane(table);
        sp.setRowHeaderView(new RowHeader(app, table));
        tabbedPane.addTab(fileData.getFileName(), sp);
        tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);

        // If only a single, unsaved file was open, remove it
        if (tabbedPane.getTabCount() == 2 && !getCsvTable(0).getFileData().isPreviouslySaved()) {
            closeTab(0);
        }

        // Notify anybody who cares that a new file was opened.
        if (file != null) {
            fireFileEvent(FileEvent.Type.OPENED, file);
        }
    }

    private void promptToCreateFile(Path file) {
        JOptionPane.showMessageDialog(this, "This file does not exist", "Unimplemented Feature", JOptionPane.ERROR_MESSAGE);
    }

    void removeFileEventListener(FileEventListener l) {
        listenerList.remove(FileEventListener.class, l);
    }

    void removeSelectedRows() {

        int selectedIndex = tabbedPane.getSelectedIndex();
        if (selectedIndex == -1) {
            UIManager.getLookAndFeel().provideErrorFeedback(this);
            return;
        }

        getSelectedCsvTable().removeSelectedRows();
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