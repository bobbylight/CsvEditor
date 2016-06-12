package org.fife.csveditor;


import org.fife.help.HelpDialog;
import org.fife.ui.CustomizableToolBar;
import org.fife.ui.OptionsDialog;
import org.fife.ui.SplashScreen;
import org.fife.ui.StatusBar;
import org.fife.ui.app.AbstractGUIApplication;
import org.fife.ui.rtextfilechooser.RTextFileChooser;
import org.fife.ui.rtextfilechooser.filters.ExtensionFileFilter;

import javax.swing.*;
import java.nio.file.Paths;

public class CsvEditor extends AbstractGUIApplication<CsvEditorPrefs> {

    private AppContent appContent;
    private RTextFileChooser fileChooser;

    private static final String VERSION = "1.0.0";

    public CsvEditor() {
        super("csveditor.jar");
        // TODO: Fix no 0-arg constructor in
        SwingUtilities.invokeLater(() -> {
            setTitle(getString("App.Title"));
        });
    }

    @Override
    protected void createActions(CsvEditorPrefs prefs) {

        addAction(Actions.OPEN_ACTION_KEY, new Actions.OpenAction(this));
        addAction(Actions.SAVE_ACTION_KEY, new Actions.SaveAction(this));
        addAction(EXIT_ACTION_KEY, new ExitAction<>(this, "Action.Exit"));

        addAction(Actions.ADD_ROW_ABOVE_ACTION_KEY, new Actions.AddRowAction(this, true));
        addAction(Actions.REMOVE_ROWS_ACTION_KEY, new Actions.RemoveRowsAction(this));
        addAction(Actions.ADD_COLUMN_ACTION_KEY, new Actions.AddColumnAction(this, true));
        addAction(Actions.OPTIONS_ACTION_KEY, new Actions.OptionsAction(this));

        addAction(HELP_ACTION_KEY, new HelpAction<>(this, "Action.Help", "/org/fife/csveditor/icons/help.gif"));
        addAction(ABOUT_ACTION_KEY, new HelpAction<>(this, "Action.About"));
    }

    @Override
    protected JMenuBar createMenuBar(CsvEditorPrefs prefs) {
        return new CsvMenuBar(this);
    }

    @Override
    protected SplashScreen createSplashScreen() {
        return null;
    }

    @Override
    protected StatusBar createStatusBar(CsvEditorPrefs prefs) {
        return new StatusBar();
    }

    @Override
    protected CustomizableToolBar createToolBar(CsvEditorPrefs prefs) {
        return null;
    }

    public AppContent getAppContent() {
        return appContent;
    }

    public RTextFileChooser getFileChooser() {

        if (fileChooser == null) {

            fileChooser = new RTextFileChooser(false);
            fileChooser.setFileFilter(new ExtensionFileFilter(getString("FileFilter.Csv"), "csv"));
        }

        return fileChooser;
    }

    @Override
    public HelpDialog getHelpDialog() {
        return null;
    }

    @Override
    public OptionsDialog getOptionsDialog() {
        return new OptionsDialog(this);
    }

    @Override
    protected String getPreferencesClassName() {
        return null;
    }

    @Override
    public String getResourceBundleClassName() {
        return "org.fife.csveditor.resources";
    }

    @Override
    public String getVersionString() {
        return VERSION;
    }

    @Override
    public void openFile(String fileName) {
        appContent.open(Paths.get(fileName));
    }

    @Override
    public void preferences() {

    }

    @Override
    protected void preDisplayInit(CsvEditorPrefs prefs, SplashScreen splashScreen) {

        appContent = new AppContent(this);

        setContentPane(appContent);
    }

    @Override
    protected void preMenuBarInit(CsvEditorPrefs prefs, SplashScreen splashScreen) {

    }

    @Override
    protected void preStatusBarInit(CsvEditorPrefs prefs, SplashScreen splashScreen) {

    }

    @Override
    protected void preToolBarInit(CsvEditorPrefs prefs, SplashScreen splashScreen) {

    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace(); // Never happens
            }
            new CsvEditor();
        });
    }
}