package org.fife.csveditor;

import org.fife.ui.app.MenuBar;

import javax.swing.*;

public class CsvMenuBar extends MenuBar {

    private CsvEditor app;

    public CsvMenuBar(CsvEditor app) {

        this.app = app;

        add(createFileMenu());
        add(createHelpMenu());
    }

    private JMenu createFileMenu() {

        JMenu menu = createMenu(app.getResourceBundle(), "Menu.File");

        menu.add(createMenuItem(app.getAction(Actions.OPEN_ACTION_KEY)));
        menu.add(createMenuItem(app.getAction(Actions.SAVE_ACTION_KEY)));

        menu.addSeparator();
        menu.add(createMenuItem(app.getAction(CsvEditor.EXIT_ACTION_KEY)));

        return menu;
    }

    private JMenu createHelpMenu() {
        JMenu menu = createMenu(app.getResourceBundle(), "Menu.Help");
        menu.add(createMenuItem(app.getAction(CsvEditor.HELP_ACTION_KEY)));
        menu.addSeparator();
        menu.add(createMenuItem(app.getAction(CsvEditor.ABOUT_ACTION_KEY)));
        return menu;
    }
}
