package fr.soe.a3s.ui.mainEditor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import fr.soe.a3s.service.ConfigurationService;
import fr.soe.a3s.ui.Facade;
import fr.soe.a3s.ui.UIConstants;

public class AutoRestartPanel extends JDialog implements UIConstants {

    private final Facade facade;

    private JButton buttonOK;

    private JButton buttonCancel;

    private JRadioButton radioBoxAutoRestartOnTerminate;

    private JRadioButton radioButtonAutoRestartAfterDelay;

    private JRadioButton radioButtonAutoRestartArTime;

    /* Service */
    private final ConfigurationService configurationService = new ConfigurationService();

    private JTextField textFieldDelayHour;

    public AutoRestartPanel(Facade facade) {
        super(facade.getMainPanel(), "Auto-restart options", true);
        this.facade = facade;
        this.setResizable(false);
        this.setSize(405, 400);
        setIconImage(ICON);
        this.setLocation((int) facade.getMainPanel().getLocation().getX()
                + facade.getMainPanel().getWidth() / 2 - this.getWidth() / 2, (int) facade
                .getMainPanel().getLocation().getY()
                + facade.getMainPanel().getHeight() / 2 - this.getHeight() / 2);
        this.setLayout(new BorderLayout());
        {
            JPanel controlPanel = new JPanel();
            buttonOK = new JButton("OK");
            buttonCancel = new JButton("Cancel");
            buttonOK.setPreferredSize(buttonCancel.getPreferredSize());
            FlowLayout flowLayout = new FlowLayout(FlowLayout.RIGHT);
            controlPanel.setLayout(flowLayout);
            controlPanel.add(buttonOK);
            controlPanel.add(buttonCancel);
            this.add(controlPanel, BorderLayout.SOUTH);
            JPanel sidePanel1 = new JPanel();
            this.add(sidePanel1, BorderLayout.EAST);
            JPanel sidePanel2 = new JPanel();
            this.add(sidePanel2, BorderLayout.WEST);
            JPanel sidePanel3 = new JPanel();
            this.add(sidePanel3, BorderLayout.NORTH);
        }
        {
            JPanel centerPanel = new JPanel();
            GridLayout grid1 = new GridLayout(1, 1);
            centerPanel.setLayout(grid1);
            this.add(centerPanel, BorderLayout.CENTER);
            // this.add(centerPanel, BorderLayout.CENTER);

            // centerPanel.setBorder(BorderFactory
            // .createTitledBorder(BorderFactory.createEtchedBorder(),
            // "Addons selection"));
            centerPanel.setBorder(BorderFactory.createEtchedBorder());
            Box vBox = Box.createVerticalBox();
            vBox.add(Box.createVerticalStrut(10));
            {
                Box hBox = Box.createHorizontalBox();
                radioBoxAutoRestartOnTerminate = new JRadioButton(
                        "Auto-restart on process terminate");
                hBox.add(radioBoxAutoRestartOnTerminate);
                radioBoxAutoRestartOnTerminate.setFocusable(false);
                hBox.add(Box.createHorizontalGlue());
                vBox.add(hBox);
            }
            vBox.add(Box.createVerticalStrut(20));
            {
                Box hBox = Box.createHorizontalBox();
                radioButtonAutoRestartAfterDelay = new JRadioButton("Auto-restart after delay");
                radioButtonAutoRestartAfterDelay.setFocusable(false);
                hBox.add(radioButtonAutoRestartAfterDelay);
                textFieldDelayHour = new JTextField();
                textFieldDelayHour.setSize(new Dimension(20, 20));
                hBox.add(textFieldDelayHour);
                vBox.add(hBox);
            }
            vBox.add(Box.createVerticalStrut(20));
            {
                Box hBox = Box.createHorizontalBox();
                radioButtonAutoRestartArTime = new JRadioButton("Auto-restart at");
                radioButtonAutoRestartArTime.setFocusable(false);
                hBox.add(radioButtonAutoRestartArTime);
                vBox.add(hBox);
            }
            vBox.add(Box.createVerticalStrut(20));
            centerPanel.add(vBox);
        }
    }

}
