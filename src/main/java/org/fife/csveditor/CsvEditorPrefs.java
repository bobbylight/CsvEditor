package org.fife.csveditor;

import org.fife.ui.app.GUIApplicationPrefs;

class CsvEditorPrefs extends GUIApplicationPrefs<CsvEditor> {

    @Override
    public GUIApplicationPrefs<CsvEditor> load() {

        return this;
    }

    @Override
    public GUIApplicationPrefs<CsvEditor> populate(CsvEditor app) {

        return this;
    }

    @Override
    public void save() {

    }

    @Override
    protected void setDefaults() {

    }
}
