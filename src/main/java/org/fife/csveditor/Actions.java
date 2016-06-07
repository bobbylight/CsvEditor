package org.fife.csveditor;

import org.fife.ui.app.AppAction;
import org.fife.ui.rtextfilechooser.RTextFileChooser;

import java.awt.event.ActionEvent;

class Actions {

    static final String OPEN_ACTION_KEY = "Actions.Open";


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
}