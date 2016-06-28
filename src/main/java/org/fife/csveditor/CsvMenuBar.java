package org.fife.csveditor;

import org.fife.ui.RecentFilesMenu;
import org.fife.ui.UIUtil;
import org.fife.ui.app.MenuBar;

import javax.swing.*;
import java.util.Collections;
import java.util.List;

public class CsvMenuBar extends MenuBar {

    private CsvEditor app;

    public CsvMenuBar(CsvEditor app) {

        this.app = app;

        add(createFileMenu());
        add(createEditMenu());
        add(createHelpMenu());
    }

    private JMenu createFileMenu() {

        JMenu menu = createMenu(app.getResourceBundle(), "Menu.File");

        menu.add(createMenuItem(app.getAction(Actions.OPEN_ACTION_KEY)));
        menu.add(createMenuItem(app.getAction(Actions.SAVE_ACTION_KEY)));
        menu.add(createMenuItem(app.getAction(Actions.SAVE_AS_ACTION_KEY)));

        menu.addSeparator();
        menu.add(createMenuItem(app.getAction(Actions.CLOSE_ACTION_KEY)));

        menu.addSeparator();
        menu.add(new RecentCsvsMenu(Collections.emptyList()));

        menu.addSeparator();
        menu.add(createMenuItem(app.getAction(CsvEditor.EXIT_ACTION_KEY)));

        return menu;
    }

    private JMenu createEditMenu() {

        JMenu menu = createMenu(app.getResourceBundle(), "Menu.Edit");

        menu.add(createMenuItem(app.getAction(Actions.ADD_ROW_ABOVE_ACTION_KEY)));
        menu.add(createMenuItem(app.getAction(Actions.REMOVE_ROWS_ACTION_KEY)));

        menu.addSeparator();
        menu.add(createMenuItem(app.getAction(Actions.OPTIONS_ACTION_KEY)));

        return menu;
    }

    private JMenu createHelpMenu() {
        JMenu menu = createMenu(app.getResourceBundle(), "Menu.Help");
        menu.add(createMenuItem(app.getAction(CsvEditor.HELP_ACTION_KEY)));
        menu.addSeparator();
        menu.add(createMenuItem(app.getAction(CsvEditor.ABOUT_ACTION_KEY)));
        return menu;
    }

    private class RecentCsvsMenu extends RecentFilesMenu {

        RecentCsvsMenu(List<String> initialContents) {
            super(app.getString("Menu.File.RecentFiles"), initialContents);
        }

        @Override
        protected Action createOpenAction(String recentFile) {
            return new Actions.OpenRecentFileAction(app, recentFile);
        }
    }
}
