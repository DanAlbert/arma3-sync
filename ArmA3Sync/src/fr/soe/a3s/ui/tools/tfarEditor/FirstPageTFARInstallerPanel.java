package fr.soe.a3s.ui.tools.tfarEditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;

import fr.soe.a3s.service.LaunchService;
import fr.soe.a3s.ui.Facade;
import fr.soe.a3s.ui.tools.WizardPanel;

/**
 * This code was edited or generated using CloudGarden's Jigloo SWT/Swing GUI Builder, which is free
 * for non-commercial use. If Jigloo is being used commercially (ie, by a corporation, company or
 * business for any purpose whatever) then you should purchase a license for each developer using
 * Jigloo. Please visit www.cloudgarden.com for details. Use of Jigloo implies acceptance of these
 * licensing terms. A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR THIS MACHINE, SO JIGLOO OR THIS
 * CODE CANNOT BE USED LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
 */
public class FirstPageTFARInstallerPanel extends WizardPanel {

    private java.awt.Component box$Filler_IL8;

    public FirstPageTFARInstallerPanel(Facade facade) {
        super(facade, "TFAR installer wizard", "Install or update TFAR for ArmA 3 and TS3",
                new ImageIcon(TFAR_BIG));

        buttonFist.setText("Proceed");
        buttonSecond.setText("Cancel");
        getRootPane().setDefaultButton(buttonFist);
        this.facade.setFirstPageTFARInstallerPanel(this);

        imagePanel.setBounds(6, 4, 48, 48);
        labelDescription.setBounds(60, 6, 300, 42);
        {
            JPanel centerPanel = new JPanel();
            this.add(centerPanel, BorderLayout.CENTER);
            centerPanel.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));
            centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
            {
                JPanel locationsPanel = new JPanel();
                locationsPanel.setBorder(BorderFactory.createTitledBorder(
                        BorderFactory.createEtchedBorder(), "Installation directories"));
                locationsPanel.setLayout(new BorderLayout());
                centerPanel.add(locationsPanel);
                Box vBox = Box.createVerticalBox();
                locationsPanel.add(vBox, BorderLayout.NORTH);
                {
                    JPanel arma3InstallationDirectoryLabelPanel = new JPanel();
                    arma3InstallationDirectoryLabelPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
                    JLabel labelArmA3installationDirectory = new JLabel("ArmA 3");
                    arma3InstallationDirectoryLabelPanel.add(labelArmA3installationDirectory);
                    vBox.add(arma3InstallationDirectoryLabelPanel);
                }
                {
                    JPanel arma3InstallationDirectoryPanel = new JPanel();
                    arma3InstallationDirectoryPanel.setLayout(new BorderLayout());
                    textFieldArmA3InstallationDirectory = new JTextField();
                    buttonSelectArmA3InstallationDirectory = new JButton("Select");
                    buttonSelectArmA3InstallationDirectory.setPreferredSize(new Dimension(70, 25));
                    textFieldArmA3InstallationDirectory.setEditable(false);
                    textFieldArmA3InstallationDirectory.setBackground(Color.WHITE);
                    arma3InstallationDirectoryPanel.add(textFieldArmA3InstallationDirectory,
                            BorderLayout.CENTER);
                    arma3InstallationDirectoryPanel.add(buttonSelectArmA3InstallationDirectory,
                            BorderLayout.EAST);
                    vBox.add(arma3InstallationDirectoryPanel);
                }
                vBox.add(Box.createVerticalStrut(5));
                {
                    JPanel ts3InstallationDirectoryLabelPanel = new JPanel();
                    ts3InstallationDirectoryLabelPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
                    JLabel ts3InstallationDirectoryLabel = new JLabel("TS3");
                    ts3InstallationDirectoryLabelPanel.add(ts3InstallationDirectoryLabel);
                    vBox.add(ts3InstallationDirectoryLabelPanel);
                }
                {
                    JPanel ts3InstallationDirectoryPanel = new JPanel();
                    ts3InstallationDirectoryPanel.setLayout(new BorderLayout());
                    textFieldTS3InstallationDirectory = new JTextField();
                    buttonSelectTS3InstallationDirectory = new JButton("Select");
                    buttonSelectTS3InstallationDirectory.setPreferredSize(new Dimension(70, 25));
                    textFieldTS3InstallationDirectory.setEditable(false);
                    textFieldTS3InstallationDirectory.setBackground(Color.WHITE);
                    ts3InstallationDirectoryPanel.add(textFieldTS3InstallationDirectory,
                            BorderLayout.CENTER);
                    ts3InstallationDirectoryPanel.add(buttonSelectTS3InstallationDirectory,
                            BorderLayout.EAST);
                    vBox.add(ts3InstallationDirectoryPanel);
                }
                vBox.add(Box.createVerticalStrut(5));
                {
                    JPanel acrePluginInstallationDirectoryLabelPanel = new JPanel();
                    acrePluginInstallationDirectoryLabelPanel.setLayout(new FlowLayout(
                            FlowLayout.LEFT));
                    JLabel acreInstallationDirectoryLabel = new JLabel("@task_force_radio\\plugins");
                    acrePluginInstallationDirectoryLabelPanel.add(acreInstallationDirectoryLabel);
                    vBox.add(acrePluginInstallationDirectoryLabelPanel);
                }
                {
                    JPanel acreInstallationDirectoryPanel = new JPanel();
                    acreInstallationDirectoryPanel.setLayout(new BorderLayout());
                    textFieldPluginInstallationDirectory = new JTextField();
                    buttonSelectPluginInstallationDirectory = new JButton("Select");
                    buttonSelectPluginInstallationDirectory.setPreferredSize(new Dimension(70, 25));
                    textFieldPluginInstallationDirectory.setEditable(false);
                    textFieldPluginInstallationDirectory.setBackground(Color.WHITE);
                    acreInstallationDirectoryPanel.add(textFieldPluginInstallationDirectory,
                            BorderLayout.CENTER);
                    acreInstallationDirectoryPanel.add(buttonSelectPluginInstallationDirectory,
                            BorderLayout.EAST);
                    vBox.add(acreInstallationDirectoryPanel);
                }
                vBox.add(Box.createVerticalStrut(5));
                {
                    JPanel acreUserconfigInstallationDirectoryLabelPanel = new JPanel();
                    acreUserconfigInstallationDirectoryLabelPanel.setLayout(new FlowLayout(
                            FlowLayout.LEFT));
                    JLabel acreInstallationDirectoryLabel = new JLabel(
                            "@task_force_radio\\userconfig");
                    acreUserconfigInstallationDirectoryLabelPanel
                            .add(acreInstallationDirectoryLabel);
                    vBox.add(acreUserconfigInstallationDirectoryLabelPanel);
                }
                {
                    JPanel acreInstallationDirectoryPanel = new JPanel();
                    acreInstallationDirectoryPanel.setLayout(new BorderLayout());
                    textFieldUserconfigInstallationDirectory = new JTextField();
                    buttonSelectUserconfigInstallationDirectory = new JButton("Select");
                    buttonSelectUserconfigInstallationDirectory.setPreferredSize(new Dimension(70,
                            25));
                    textFieldUserconfigInstallationDirectory.setEditable(false);
                    textFieldUserconfigInstallationDirectory.setBackground(Color.WHITE);
                    acreInstallationDirectoryPanel.add(textFieldUserconfigInstallationDirectory,
                            BorderLayout.CENTER);
                    acreInstallationDirectoryPanel.add(buttonSelectUserconfigInstallationDirectory,
                            BorderLayout.EAST);
                    vBox.add(acreInstallationDirectoryPanel);
                }

                vBox.add(Box.createVerticalStrut(20));
            }
            {
                JPanel systemDescriptionPanel = new JPanel();
                systemDescriptionPanel.setBorder(BorderFactory.createTitledBorder(
                        BorderFactory.createEtchedBorder(), "System specifications"));
                centerPanel.add(systemDescriptionPanel);
                systemDescriptionPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
                Box vBox = Box.createVerticalBox();
                systemDescriptionPanel.add(vBox);
                {
                    labelOS = new JLabel("OS: ");
                    labelOSValue = new JLabel();
                    Box hBox = Box.createHorizontalBox();
                    hBox.add(labelOS);
                    hBox.add(labelOSValue);
                    hBox.add(Box.createHorizontalGlue());
                    vBox.add(hBox);
                    vBox.add(Box.createVerticalStrut(10));
                }
                {
                    labelTS3 = new JLabel("TS3: ");
                    labelTS3Value = new JLabel();
                    Box hBox = Box.createHorizontalBox();
                    hBox.add(labelTS3);
                    hBox.add(labelTS3Value);
                    hBox.add(Box.createHorizontalGlue());
                    vBox.add(hBox);
                    vBox.add(Box.createVerticalStrut(10));
                }
                {
                    labelPlugin = new JLabel("TFAR plugins: ");
                    labelPluginValue = new JLabel();
                    Box hBox = Box.createHorizontalBox();
                    hBox.add(labelPlugin);
                    hBox.add(labelPluginValue);
                    hBox.add(Box.createHorizontalGlue());
                    vBox.add(hBox);
                }
                {
                    box$Filler_IL8 = Box.createVerticalStrut(10);
                    vBox.add(box$Filler_IL8);
                    box$Filler_IL8.setPreferredSize(new java.awt.Dimension(506, 10));
                }
                vBox.add(Box.createVerticalStrut(10));
            }
        }
        buttonSelectArmA3InstallationDirectory.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                buttonSelectArmA3InstallationDirectoryPerformed();
            }
        });
        buttonSelectTS3InstallationDirectory.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buttonSelectTS3InstallationDirectoryPerformed();
            }
        });
        buttonSelectPluginInstallationDirectory.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buttonSelectTFARPluginInstallationDirectoryPerformed();
            }
        });
        buttonSelectUserconfigInstallationDirectory.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buttonSelectTFARUserconfigInstallationDirectoryPerformed();
            }
        });
    }

    public void init() {

        determinePaths();
        determineSystemSpecs();
    }

    private void determinePaths() {

        /* ArmA 3 directory */
        String arma3ExePath = configurationService.getLauncherOptions().getArma3ExePath();

        if (arma3ExePath != null) {
            String arma3InstallationDirectoryPath = (new File(arma3ExePath)).getParentFile()
                    .getAbsolutePath();
            textFieldArmA3InstallationDirectory.setText(arma3InstallationDirectoryPath);
        }

        /* TS3 directory */
        String ts3InstallationDirectoryPath = configurationService.getAcreTS3installationFodler();

        if (ts3InstallationDirectoryPath != null) {
            if (new File(ts3InstallationDirectoryPath).exists()) {
                textFieldTS3InstallationDirectory.setText(ts3InstallationDirectoryPath);
            }
        }

        /* @TFAR plugin directory */
        String tfarPluginPath = configurationService.getTfarPluginPath();

        if (tfarPluginPath != null) {
            if (new File(tfarPluginPath).exists()) {
                textFieldPluginInstallationDirectory.setText(tfarPluginPath);
            }
        }
        else {
            String tfarInstallationDirectoryPath = addonService.getTFARinstallationFolder();
            if (tfarInstallationDirectoryPath != null) {
                String path = tfarInstallationDirectoryPath + "/@task_force_radio/plugins";
                if (new File(path).exists()) {
                    path = new File(path).getAbsolutePath();
                    textFieldPluginInstallationDirectory.setText(path);
                }
            }
        }

        /* @TFAR userconfig directory */
        String tfarUserconfigPath = configurationService.getTfarUserconfigPath();

        if (tfarUserconfigPath != null) {
            if (new File(tfarUserconfigPath).exists()) {
                textFieldUserconfigInstallationDirectory.setText(tfarUserconfigPath);
            }
        }
        else {
            String tfarInstallationDirectoryPath = addonService.getTFARinstallationFolder();
            if (tfarInstallationDirectoryPath != null) {
                String path = tfarInstallationDirectoryPath + "/@task_force_radio/userconfig";
                if (new File(path).exists()) {
                    path = new File(path).getAbsolutePath();
                    textFieldUserconfigInstallationDirectory.setText(path);
                }
            }
        }
    }

    private void determineSystemSpecs() {

        /* OS */
        determineOS();

        /* TS3 version */
        determineTS3();

        /* TFAR plugin */
        boolean is64bit = false;
        boolean is32bit = false;
        if (textFieldPluginInstallationDirectory.getText().isEmpty()) {
            labelPluginValue.setText("Unknown");
        }
        else if (textFieldTS3InstallationDirectory.getText().isEmpty()) {
            labelPluginValue.setText("Unknown");
        }
        else {
            is64bit = configurationService.isTS364bit(textFieldTS3InstallationDirectory.getText());
            if (is64bit) {
                labelPluginValue.setText("task_force_radio_win64.dll");
            }
            else {
                labelPluginValue.setText("task_force_radio_win32.dll");
            }
        }
    }

    private void buttonSelectArmA3InstallationDirectoryPerformed() {

        JFileChooser fc = null;
        String arma3Path = configurationService.determineArmA3Path();
        if (arma3Path == null) {
            fc = new JFileChooser();
        }
        else {
            File arma3Folder = new File(arma3Path);
            if (arma3Folder.exists()) {
                fc = new JFileChooser(arma3Path);
            }
            else {
                fc = new JFileChooser();
            }
        }

        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnVal = fc.showOpenDialog(FirstPageTFARInstallerPanel.this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            String path = file.getAbsolutePath();
            textFieldArmA3InstallationDirectory.setText(path);
        }
        else {
            textFieldArmA3InstallationDirectory.setText("");
        }
        determineSystemSpecs();
    }

    private void buttonSelectTS3InstallationDirectoryPerformed() {

        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnVal = fc.showOpenDialog(FirstPageTFARInstallerPanel.this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            String path = file.getAbsolutePath();
            textFieldTS3InstallationDirectory.setText(path);
        }
        else {
            textFieldTS3InstallationDirectory.setText("");
        }
        configurationService.setTfarTS3installationFodler(textFieldTS3InstallationDirectory
                .getText());
        determineSystemSpecs();
    }

    private void buttonSelectTFARPluginInstallationDirectoryPerformed() {

        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnVal = fc.showOpenDialog(FirstPageTFARInstallerPanel.this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            String path = file.getAbsolutePath();
            textFieldPluginInstallationDirectory.setText(path);
        }
        else {
            textFieldPluginInstallationDirectory.setText("");
        }
        configurationService.setTfarPluginPath(textFieldPluginInstallationDirectory.getText());

        determineSystemSpecs();
    }

    private void buttonSelectTFARUserconfigInstallationDirectoryPerformed() {

        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnVal = fc.showOpenDialog(FirstPageTFARInstallerPanel.this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            String path = file.getAbsolutePath();
            textFieldUserconfigInstallationDirectory.setText(path);
        }
        else {
            textFieldUserconfigInstallationDirectory.setText("");
        }
        configurationService.setTfarUserconfigPath(textFieldUserconfigInstallationDirectory
                .getText());

        determineSystemSpecs();
    }

    @Override
    public void buttonFistPerformed() {

        String message = "";

        String osName = System.getProperty("os.name");
        if (!osName.contains("Windows")) {
            message = "This feature is not supported for your system";
            JOptionPane.showMessageDialog(this, message, "TFAR installer",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (textFieldArmA3InstallationDirectory.getText().isEmpty()) {
            message = "ArmA 3 installation directory is missing.";
        }
        else if (textFieldTS3InstallationDirectory.getText().isEmpty()) {
            message = "TS3 installation directory is missing.";
        }
        else if (textFieldPluginInstallationDirectory.getText().isEmpty()) {
            message = "TFAR plugin directory is missing.";
        }
        else if (textFieldUserconfigInstallationDirectory.getText().isEmpty()) {
            message = "TFAR userconfig directory is missing.";
        }
        else if (labelTS3Value.equals("Unknown")) {
            message = "Can't determine between TS3 32/64 bit.";
        }

        String ts3PluginsFoler = textFieldTS3InstallationDirectory.getText() + "/plugins";
        if (!new File(ts3PluginsFoler).exists()) {
            message = "TS3 installation directory is missing \\plugins folder";
        }

        String acrePlugin = textFieldPluginInstallationDirectory.getText() + "/"
                + labelPluginValue.getText();
        if (!new File(acrePlugin).exists()) {
            if (labelPluginValue.getText().equals("Unknown")) {
                message = "TFAR plugin is missing.";
            }
            else {
                message = "TFAR plugin " + labelPluginValue.getText() + " is missing.";
            }
        }

        LaunchService launchService = new LaunchService();
        boolean isTS3Running = launchService.isTS3Running();
        if (isTS3Running) {
            message = "TS3 is running and must be close to proceed.";
        }

        if (!message.isEmpty()) {
            JOptionPane.showMessageDialog(this, message, "TFAR installer",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        this.setVisible(false);
        SecondPageTFARInstallerPanel secondPageInstallerPanel = new SecondPageTFARInstallerPanel(
                facade, title, description, imageIcon);
        secondPageInstallerPanel.init(textFieldArmA3InstallationDirectory.getText(),
                textFieldTS3InstallationDirectory.getText(),
                textFieldUserconfigInstallationDirectory.getText(),
                textFieldPluginInstallationDirectory.getText());
        secondPageInstallerPanel.setVisible(true);
        secondPageInstallerPanel.setFocusable(true);
    }

    @Override
    public void buttonSecondPerformed() {
        this.dispose();
    }
}
