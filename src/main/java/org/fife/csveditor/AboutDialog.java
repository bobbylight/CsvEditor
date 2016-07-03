package org.fife.csveditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SpringLayout;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.fife.ui.EscapableDialog;
import org.fife.ui.RScrollPane;
import org.fife.ui.ResizableFrameContentPane;
import org.fife.ui.SelectableLabel;
import org.fife.ui.UIUtil;
import org.fife.ui.rtextfilechooser.Utilities;

class AboutDialog extends EscapableDialog {

    private static final long serialVersionUID = 1L;

    private CsvEditor parent;
    private Listener listener;
    private SelectableLabel memoryField;


    AboutDialog(CsvEditor parent) {

        super(parent);
        this.parent = parent;
        listener = new Listener();
        ResourceBundle msg = parent.getResourceBundle();
        ComponentOrientation o = parent.getComponentOrientation();

        JPanel cp = new ResizableFrameContentPane(new BorderLayout());

        Box box = Box.createVerticalBox();

        JPanel top = new JPanel(new BorderLayout(15, 0));
        top.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        Color topBG = UIManager.getColor("TextField.background");
        top.setOpaque(true);
        top.setBackground(topBG);
        top.setBorder(new TopBorder());

        JLabel linkLabel = new JLabel(new ImageIcon(getClass().getResource("csv-128.png")));
        top.add(linkLabel, BorderLayout.LINE_START);

        JPanel topText = new JPanel(new BorderLayout());
        topText.setOpaque(false);
        top.add(topText);

        // Don't use a Box, as some JVM's won't have the resulting component
        // honor its opaque property.
        JPanel box2 = new JPanel();
        box2.setOpaque(false);
        box2.setLayout(new BoxLayout(box2, BoxLayout.Y_AXIS));
        topText.add(box2, BorderLayout.NORTH);

        JLabel label = UIUtil.newLabel(msg, "Dialog.About.MainLabel");
        label.setOpaque(true);
        label.setBackground(topBG);
        Font labelFont = label.getFont();
        label.setFont(labelFont.deriveFont(Font.BOLD, 20));
        addLeftAligned(label, box2);
        box2.add(Box.createVerticalStrut(5));

        String buildDate = parent.getBuildDate().replace("<", "&lt;");
        String desc = parent.getString("Dialog.About.MainDesc", parent.getVersionString(), buildDate);
        SelectableLabel textArea = new SelectableLabel(desc);
        textArea.addHyperlinkListener(listener);
        box2.add(textArea);
        box2.add(Box.createVerticalGlue());

        box.add(top);
        box.add(Box.createVerticalStrut(5));

        JPanel temp = new JPanel(new SpringLayout());
        SelectableLabel javaField = new SelectableLabel(System.getProperty("java.home"));
        memoryField = new SelectableLabel();
        JLabel javaLabel = UIUtil.newLabel(msg, "Dialog.About.JavaHome", javaField);
        JLabel memoryLabel = UIUtil.newLabel(msg, "Dialog.About.Memory", memoryField);

        if (o.isLeftToRight()) {
            temp.add(javaLabel);        temp.add(javaField);
            temp.add(memoryLabel);      temp.add(memoryField);
        }
        else {
            temp.add(javaField);        temp.add(javaLabel);
            temp.add(memoryField);      temp.add(memoryLabel);
        }
        UIUtil.makeSpringCompactGrid(temp, 2, 2, 5,5, 15,5);
        box.add(temp);

        box.add(Box.createVerticalGlue());

        cp.add(box, BorderLayout.NORTH);

        JButton okButton = UIUtil.newButton(msg, "Button.OK");
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });
        JPanel buttons = (JPanel)UIUtil.createButtonFooter(okButton);
        buttons.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(5, 8, 5, 8),
                buttons.getBorder()));
        cp.add(buttons, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(okButton);
        setTitle(parent.getString("Dialog.About.Title"));
        setContentPane(cp);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setModal(true);
        pack();

    }


    private JPanel addLeftAligned(Component toAdd, Container addTo) {
        JPanel temp = new JPanel(new BorderLayout());
        temp.setOpaque(false); // For ones on white background.
        temp.add(toAdd, BorderLayout.LINE_START);
        addTo.add(temp);
        return temp;
    }


    private String getMemoryInfo() {
        long curMemory = Runtime.getRuntime().totalMemory() -
                Runtime.getRuntime().freeMemory();
        return Utilities.getFileSizeStringFor(curMemory, false);
    }


    public Dimension getPreferredSize() {
        Dimension d = super.getPreferredSize();
        d.width = Math.max(d.width, 600); // Looks better with a little width.
        return d;
    }


    public void setVisible(boolean visible) {
        if (visible) {
            memoryField.setText(getMemoryInfo());
        }
        super.setVisible(visible);
    }


    /**
     * Dialog showing used libraries and credits.
     */
    private class CreditsDialog extends EscapableDialog {

        private static final long serialVersionUID = 1L;

        CreditsDialog() {

            super(AboutDialog.this);
            JPanel cp = new ResizableFrameContentPane(new BorderLayout());
            cp.setBorder(UIUtil.getEmpty5Border());
            ResourceBundle msg = parent.getResourceBundle();

            SelectableLabel label = new SelectableLabel(parent.getString(
                    "Dialog.Credits.Content"));
            label.addHyperlinkListener(listener);
            cp.add(label);

            JButton okButton = UIUtil.newButton(msg, "Button.OK");
            okButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    setVisible(false);
                }
            });
            Container buttons = UIUtil.createButtonFooter(okButton);
            cp.add(buttons, BorderLayout.SOUTH);

            setContentPane(cp);
            setTitle(parent.getString("Dialog.Credits.Title"));
            setModal(true);
            pack();
            setLocationRelativeTo(AboutDialog.this);

        }

    }


    /**
     * Dialog showing the license for this application.
     */
    private class LicenseDialog extends EscapableDialog {

        private static final long serialVersionUID = 1L;

        public LicenseDialog() {

            super(AboutDialog.this);
            JPanel cp = new ResizableFrameContentPane(new BorderLayout());
            cp.setBorder(UIUtil.getEmpty5Border());
            ResourceBundle msg = parent.getResourceBundle();

            SelectableLabel desc = new SelectableLabel(parent.getString(
                    "Dialog.License.Desc"));
            desc.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
            cp.add(desc, BorderLayout.NORTH);

            JTabbedPane tabPane = new JTabbedPane();
            cp.add(tabPane);

            JTextArea textArea = new JTextArea(25, 80);
//            Font font = RTextArea.getDefaultFont();
//            if (font instanceof FontUIResource) { // Substance!  argh!!!
//                font = new Font(font.getFamily(), font.getStyle(), font.getSize());
//            }
//            textArea.setFont(font);
            System.out.println(textArea.getFont());
            loadText(textArea, "/org/fife/zquest/ui/jzquest.license.txt");
            textArea.setEditable(false);
            RScrollPane sp = new RScrollPane(textArea);
            tabPane.addTab(parent.getString("Dialog.License.App"), sp);

            textArea = new JTextArea(25, 80);
//            textArea.setFont(font);
            loadText(textArea, "/org/fife/zquest/jzclib.license.txt");
            textArea.setEditable(false);
            sp = new RScrollPane(textArea);
            tabPane.addTab(parent.getString("Dialog.License.Lib"), sp);

            JButton okButton = UIUtil.newButton(msg, "Button.OK");
            okButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    setVisible(false);
                }
            });
            Container buttons = UIUtil.createButtonFooter(okButton);
            cp.add(buttons, BorderLayout.SOUTH);

            setContentPane(cp);
            setTitle(parent.getString("Dialog.License.Title"));
            setModal(true);
            pack();
            setLocationRelativeTo(AboutDialog.this);

        }

        private void loadText(JTextArea textArea, String res) {
            InputStream in = getClass().getResourceAsStream(res);
            try {
                BufferedReader r = new BufferedReader(new InputStreamReader(in));
                textArea.read(r, null);
                r.close();
            } catch (IOException ioe) {
                textArea.setText(ioe.getMessage());
            }
        }

    }


    /**
     * Listens for events in this dialog.
     */
    private class Listener implements HyperlinkListener {

        private void handleLocalLink(URL url) {

            String str = url.toString();
            String command = str.substring(str.lastIndexOf('/')+1);

            if ("credits".equals(command)) {
                new CreditsDialog().setVisible(true);
            }
            else if ("license".equals(command)) {
                new LicenseDialog().setVisible(true);
            }

        }


        @Override
        public void hyperlinkUpdate(HyperlinkEvent e) {
            if (e.getEventType()==HyperlinkEvent.EventType.ACTIVATED) {
                URL url = e.getURL();
                System.out.println("'" + url.getProtocol() + "'");
                if ("file".equals(url.getProtocol())) {
                    handleLocalLink(url);
                    return;
                }
                if (!UIUtil.browse(url.toString())) {
                    UIManager.getLookAndFeel().provideErrorFeedback(AboutDialog.this);
                }
            }
        }

    }


    /**
     * The border of the "top section" of the About dialog.
     */
    private static class TopBorder extends AbstractBorder {

        private static final long serialVersionUID = 1L;

        public Insets getBorderInsets(Component c) {
            return getBorderInsets(c, new Insets(0, 0, 0, 0));
        }

        public Insets getBorderInsets(Component c, Insets insets) {
            insets.top = insets.left = insets.right = 5;
            insets.bottom = 6;
            return insets;
        }

        public void paintBorder(Component c, Graphics g, int x, int y,
                                int width, int height) {
            Color color = UIManager.getColor("controlShadow");
            if (color==null) {
                color = SystemColor.controlShadow;
            }
            g.setColor(color);
            g.drawLine(x,y+height-1, x+width,y+height-1);
        }

    }

}