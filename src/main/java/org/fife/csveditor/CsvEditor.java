package org.fife.csveditor;


import org.fife.help.HelpDialog;
import org.fife.ui.*;
import org.fife.ui.app.AbstractGUIApplication;
import org.fife.ui.rtextfilechooser.FileChooserOwner;
import org.fife.ui.rtextfilechooser.RTextFileChooser;
import org.fife.ui.rtextfilechooser.filters.ExtensionFileFilter;

import javax.swing.*;
import java.nio.file.Paths;

public class CsvEditor extends AbstractGUIApplication<CsvEditorPrefs> implements FileChooserOwner {

    private AppContent appContent;
    private RTextFileChooser fileChooser;
    private CsvEditorOptionsDialog optionsDialog;

    private static final String VERSION = "0.1.0";

    public CsvEditor() {
        super("csveditor.jar");
        // TODO: Fix no 0-arg constructor in
        SwingUtilities.invokeLater(() -> {
            setTitle(getString("App.Title"));
        });
    }

    @Override
    public JDialog createAboutDialog() {
        return new AboutDialog(this);
    }

    @Override
    protected void createActions(CsvEditorPrefs prefs) {

        addAction(Actions.OPEN_ACTION_KEY, new Actions.OpenAction(this));
        addAction(Actions.SAVE_ACTION_KEY, new Actions.SaveAction(this));
        addAction(Actions.SAVE_AS_ACTION_KEY, new Actions.SaveAsAction(this));
        addAction(Actions.CLOSE_ACTION_KEY, new Actions.CloseAction(this));
        addAction(EXIT_ACTION_KEY, new ExitAction<>(this, "Action.Exit"));

        addAction(Actions.ADD_ROW_ABOVE_ACTION_KEY, new Actions.AddRowAction(this, true));
        addAction(Actions.ADD_ROWS_ABOVE_ACTION_KEY, new Actions.AddRowsAction(this, true));
        addAction(Actions.REMOVE_ROWS_ACTION_KEY, new Actions.RemoveRowsAction(this));
        addAction(Actions.ADD_COLUMN_ACTION_KEY, new Actions.AddColumnAction(this, true));
        addAction(Actions.OPTIONS_ACTION_KEY, new OptionsAction<>(this, "Action.Options"));

        addAction(HELP_ACTION_KEY, new HelpAction<>(this, "Action.Help", "/org/fife/csveditor/icons/help.gif"));
        addAction(ABOUT_ACTION_KEY, new AboutAction<>(this, "Action.About"));
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

    public String getBuildDate() {
        return "<unknown>"; // TODO
    }

    @Override
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
        if (optionsDialog == null) {
            optionsDialog = new CsvEditorOptionsDialog(this);
        }
        return optionsDialog;
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