package org.fife.csveditor;

import org.fife.csveditor.events.FileEvent;
import org.fife.csveditor.events.FileEventListener;
import org.fife.ui.RecentFilesMenu;
import org.fife.ui.app.MenuBar;

import javax.swing.*;
import java.util.Collections;
import java.util.List;

class CsvMenuBar extends MenuBar {

    private CsvEditor app;
    private RecentCsvsMenu recentCsvsMenu;

    CsvMenuBar(CsvEditor app) {

        this.app = app;
        Listener listener = new Listener();
        SwingUtilities.invokeLater(() -> { // Yucky
            app.getAppContent().addFileEventListener(listener);
        });

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
        recentCsvsMenu = new RecentCsvsMenu(Collections.emptyList());
        menu.add(recentCsvsMenu);

        menu.addSeparator();
        menu.add(createMenuItem(app.getAction(CsvEditor.EXIT_ACTION_KEY)));

        return menu;
    }

    private JMenu createEditMenu() {

        JMenu menu = createMenu(app.getResourceBundle(), "Menu.Edit");

        menu.add(createMenuItem(app.getAction(Actions.UNDO_ACTION_KEY)));
        menu.add(createMenuItem(app.getAction(Actions.REDO_ACTION_KEY)));
        menu.addSeparator();

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

    private class Listener implements FileEventListener {

        @Override
        public void fileClosed(FileEvent e) {
            // Ignore
        }

        @Override
        public void fileOpened(FileEvent e) {
            recentCsvsMenu.addFileToFileHistory(e.getPath().toAbsolutePath().toString());
        }
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
