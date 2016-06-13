package org.fife.csveditor;

import org.fife.ui.EscapableDialog;
import org.fife.ui.PickyDocumentFilter;
import org.fife.ui.ResizableFrameContentPane;
import org.fife.ui.UIUtil;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AddRowsDialog extends EscapableDialog {

    private JTextField rowCountField;
    private JRadioButton aboveRadio;
    private JRadioButton belowRadio;
    private JButton okButton;
    private boolean escapePressed;

    public AddRowsDialog(CsvEditor app, boolean above) {

        super(app);
        Listener listener = new Listener();

        JPanel cp = new ResizableFrameContentPane(new BorderLayout());
        cp.setBorder(UIUtil.getEmpty5Border());

        JPanel formPanel = new JPanel(new SpringLayout());

        rowCountField = new JTextField(40);
        ((AbstractDocument)rowCountField.getDocument()).setDocumentFilter(new NumberDocumentFilter());
        JLabel rowFieldLabel = UIUtil.newLabel(app.getResourceBundle(), "Dialog.AddRows.RowCount");
        rowFieldLabel.setLabelFor(rowCountField);

        ButtonGroup bg = new ButtonGroup();
        aboveRadio = UIUtil.newRadio(app.getResourceBundle(), "Dialog.AddRows.Before", bg);
        aboveRadio.setSelected(above);
        belowRadio = UIUtil.newRadio(app.getResourceBundle(), "Dialog.AddRows.After", bg);
        belowRadio.setSelected(!above);
        Box radioPanel = Box.createHorizontalBox();
        radioPanel.add(aboveRadio);
        radioPanel.add(Box.createHorizontalStrut(5));
        radioPanel.add(belowRadio);
        radioPanel.add(Box.createHorizontalGlue());
        JLabel locationLabel = UIUtil.newLabel(app.getResourceBundle(), "Dialog.AddRows.Location");
        locationLabel.setLabelFor(aboveRadio);

        if (app.getComponentOrientation().isLeftToRight()) {
            formPanel.add(rowFieldLabel); formPanel.add(rowCountField);
            formPanel.add(locationLabel); formPanel.add(radioPanel);
        }
        else {
            formPanel.add(rowCountField); formPanel.add(rowFieldLabel);
            formPanel.add(radioPanel); formPanel.add(locationLabel);
        }
        UIUtil.makeSpringCompactGrid(formPanel, 2, 2, 5, 5, 10, 5);
        cp.add(formPanel, BorderLayout.NORTH);

        okButton = UIUtil.newButton(app.getResourceBundle(), "Button.OK");
        okButton.addActionListener(listener);
        getRootPane().setDefaultButton(okButton);
        JButton cancelButton = UIUtil.newButton(app.getResourceBundle(), "Button.Cancel");
        cp.add(UIUtil.createButtonFooter(okButton, cancelButton, 10), BorderLayout.SOUTH);

        setContentPane(cp);
        setTitle(app.getString("Dialog.AddRows.Title"));
        setModal(true);
        pack();
    }

    @Override
    public void escapePressed() {
        escapePressed = true;
    }

    public int getRowCount() {
        if (escapePressed || rowCountField.getText().isEmpty()) {
            return -1;
        }
        return Integer.parseInt(rowCountField.getText());
    }

    public boolean getRowLocation() {
        return aboveRadio.isSelected();
    }

    private class Listener implements ActionListener {

        public void actionPerformed(ActionEvent e) {

            Object source = e.getSource();

            if (okButton == source) {
                setVisible(false);
            }
        }
    }

    private static class NumberDocumentFilter extends PickyDocumentFilter {

        @Override
        protected String cleanseImpl(String text) {
            if (text == null) {
                return null;
            }
            return text.replaceAll("[^0-9]+", "");
        }
    }
}
