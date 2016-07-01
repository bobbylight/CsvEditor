package org.fife.csveditor;

import org.fife.ui.app.AppAction;
import org.fife.ui.rtextfilechooser.RTextFileChooser;

import java.awt.event.ActionEvent;
import java.nio.file.Path;

class Actions {

    static final String OPEN_ACTION_KEY = "Actions.Open";
    static final String SAVE_ACTION_KEY = "Actions.Save";
    static final String SAVE_AS_ACTION_KEY = "Actions.SaveAs";
    static final String CLOSE_ACTION_KEY = "Actions.Close";

    static final String UNDO_ACTION_KEY = "Actions.Undo";
    static final String REDO_ACTION_KEY = "Actions.Redo";
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

    static class CloseAction extends AppAction<CsvEditor> {

        CloseAction(CsvEditor app) {
            super(app, "Action.Close");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            getApplication().getAppContent().closeCurrentTab();
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

    static class OpenRecentFileAction extends AppAction<CsvEditor> {

        private String recentFile;

        OpenRecentFileAction(CsvEditor app, String recentFile) {
            super(app);
            setName(recentFile);
            this.recentFile = recentFile;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            getApplication().openFile(recentFile);
        }
    }

    static class RedoAction extends AppAction<CsvEditor> {

        RedoAction(CsvEditor app) {
            super(app, "Action.Redo", "icons/redo.gif");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            CsvEditor app = getApplication();
            app.getAppContent().getSelectedCsvTable().redo();
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

    static class SaveAsAction extends AppAction<CsvEditor> {

        SaveAsAction(CsvEditor app) {
            super(app, "Action.SaveAs");
        }

        @Override
        public void actionPerformed(ActionEvent e) {

            CsvEditor app = getApplication();
            RTextFileChooser chooser = app.getFileChooser();

            AppContent appContent = app.getAppContent();
            Path origPath = appContent.getSelectedCsvTable().getFileData().getPath();
            if (origPath != null) {
                chooser.setSelectedFile(origPath.toFile());
            }

            int rc = chooser.showSaveDialog(app);
            if (rc == RTextFileChooser.APPROVE_OPTION) {
                appContent.getSelectedCsvTable().getFileData().setPath(chooser.getSelectedFile().toPath());
                if (!appContent.saveSelectedTab() && origPath == null) {
                    appContent.getSelectedCsvTable().getFileData().setPath(null);
                }
            }
        }
    }

    static class UndoAction extends AppAction<CsvEditor> {

        UndoAction(CsvEditor app) {
            super(app, "Action.Undo", "icons/undo.gif");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            CsvEditor app = getApplication();
            app.getAppContent().getSelectedCsvTable().undo();
        }
    }
}
