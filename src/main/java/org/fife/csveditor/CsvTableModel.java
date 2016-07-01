package org.fife.csveditor;

import javax.swing.table.DefaultTableModel;
import java.util.Vector;

/**
 * The table model used by  CSV editors.
 */
class CsvTableModel extends DefaultTableModel {

    CsvTableModel(String[][] rowData, String[] columnNames) {
        super(rowData, columnNames);
    }

    void removeColumn(int index) {

        dataVector.forEach(o -> {
            Vector row = (Vector)o;
            row.remove(index);
        });

        columnIdentifiers.remove(index);
    }
}
