package org.fife.csveditor;

import org.fife.ui.app.AppAction;
import org.fife.ui.rtextfilechooser.RTextFileChooser;

import java.awt.event.ActionEvent;

class Actions {

    static final String OPEN_ACTION_KEY = "Actions.Open";
    static final String SAVE_ACTION_KEY = "Actions.Save";

    static final String ADD_ROW_ABOVE_ACTION_KEY = "Actions.AddRowAbove";
    static final String ADD_ROWS_ABOVE_ACTION_KEY = "Actions.AddRowsAbove";
    static final String REMOVE_ROWS_ACTION_KEY = "Actions.RemoveRows";
    static final String ADD_COLUMN_ACTION_KEY = "Actions.AddColumn";

    static final String OPTIONS_ACTION_KEY = "Actions.Options";

    static class AddColumnAction extends AppAction<CsvEditor> {

        private boolean before;

        AddColumnAction(CsvEditor app, boolean before) {
            super(app, "Action.AddColumn" + (before ? "Before" : "After"), "icons/add_obj.gif");
            this.before = before;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            getApplication().getAppContent().addColumns(1, before);
        }
    }

    static class AddRowAction extends AppAction<CsvEditor> {

        private boolean above;

        AddRowAction(CsvEditor app, boolean above) {
            super(app, "Action.AddRow" + (above ? "Above" : "Below"), "icons/add_obj.gif");
            this.above = above;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            CsvEditor app = getApplication();
            app.getAppContent().addRows(1, above);
        }
    }

    static class AddRowsAction extends AppAction<CsvEditor> {

        private boolean above;

        AddRowsAction(CsvEditor app, boolean above) {
            super(app, "Action.AddRows" + (above ? "Above" : "Below"));
            this.above = above;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            CsvEditor app = getApplication();
            app.getAppContent().addArbitraryRows(above);
        }
    }

    static class OpenAction extends AppAction<CsvEditor> {

        OpenAction(CsvEditor app) {
            super(app, "Action.Open", "icons/open.gif");
        }

        @Override
        public void actionPerformed(ActionEvent e) {

            CsvEditor app = getApplication();
            RTextFileChooser chooser = app.getFileChooser();
            int rc = chooser.showOpenDialog(app);

            if (rc == RTextFileChooser.APPROVE_OPTION) {
                app.openFile(chooser.getSelectedFile().getAbsolutePath());
            }
        }
    }

    static class OptionsAction extends AppAction<CsvEditor> {

        OptionsAction(CsvEditor app) {
            super(app, "Action.Options", "icons/options.gif");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            getApplication().getOptionsDialog().setVisible(true);
        }
    }

    static class RemoveRowsAction extends AppAction<CsvEditor> {

        RemoveRowsAction(CsvEditor app) {
            super(app, "Action.RemoveRows", "icons/delete.gif");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            getApplication().getAppContent().removeSelectedRows();
        }
    }

    static class SaveAction extends AppAction<CsvEditor> {

        SaveAction(CsvEditor app) {
            super(app, "Action.Save", "icons/save.gif");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            CsvEditor app = getApplication();
            app.getAppContent().saveSelectedTab();
        }
    }
}
