package org.fife.csveditor;

import org.fife.ui.OptionsDialog;
import org.fife.ui.rtextfilechooser.RTextFileChooserOptionPanel;

import java.util.Arrays;

class CsvEditorOptionsDialog extends OptionsDialog {

    CsvEditorOptionsDialog(CsvEditor app) {

        super(app);

        setOptionsPanels(Arrays.asList(
                new RTextFileChooserOptionPanel()
        ));
    }
}
