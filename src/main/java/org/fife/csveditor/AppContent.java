package org.fife.csveditor;

import com.opencsv.CSVReader;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

public class AppContent extends JPanel {

    private CsvEditor app;
    private JTabbedPane tabbedPane;

    public AppContent(CsvEditor app) {

        this.app = app;

        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Hello", new JPanel());

        setLayout(new BorderLayout());
        add(tabbedPane);
    }

    public void open(Path file) {

        CSVReader r;
        try {

            r = new CSVReader(Files.newBufferedReader(file, Charset.forName("UTF-8")));

            StringBuilder sb = new StringBuilder();
            String[] row;
            while ((row = r.readNext()) != null) {
                if (row.length > 0) sb.append(row[0]);
            }

            r.close();

            tabbedPane.addTab(file.toFile().getName(), new JLabel(sb.toString()));

        } catch (IOException ioe) {
            app.displayException(ioe);
        }
    }
}