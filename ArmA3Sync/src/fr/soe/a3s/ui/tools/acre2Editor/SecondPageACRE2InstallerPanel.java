package fr.soe.a3s.ui.tools.acre2Editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import fr.soe.a3s.dao.FileAccessMethods;
import fr.soe.a3s.ui.Facade;
import fr.soe.a3s.ui.tools.WizardPanel;

public class SecondPageACRE2InstallerPanel extends WizardPanel {

	private JLabel labelOperationsPerformed;
	private JLabel labelCopyPlugin;
	private JLabel labelCopyPluginValue;
	private JLabel labelCopyUserconfig;
	private JLabel labelCopyUserconfigValue;
	private JLabel labelMessage1;
	private JLabel labelMessage2;
	private JButton buttonCopyPlugin;
	private String ts3Directory;
	private static String statusOK = "OK";
	private static String statusFail = "Fail";

	public SecondPageACRE2InstallerPanel(Facade facade, String title,
			String description, ImageIcon imageIcon) {
		super(facade, title, description, imageIcon);

		buttonFist.setText("Back");
		buttonSecond.setText("Close");
		getRootPane().setDefaultButton(buttonSecond);

		JPanel centerPanel = new JPanel();
		this.add(centerPanel, BorderLayout.CENTER);
		centerPanel.setBorder(BorderFactory
				.createEtchedBorder(BevelBorder.LOWERED));
		centerPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		Box vBox = Box.createVerticalBox();
		centerPanel.add(vBox);
		vBox.add(Box.createVerticalStrut(20));
		{
			labelOperationsPerformed = new JLabel(
					"The following operations have been performed:");
			Box hBox = Box.createHorizontalBox();
			hBox.add(labelOperationsPerformed);
			hBox.add(Box.createHorizontalGlue());
			vBox.add(hBox);
		}
		vBox.add(Box.createVerticalStrut(10));
		{
			labelCopyPlugin = new JLabel(
					"  - ACRE 2 plugin copied into TS3\\plugins directory. ");
			labelCopyPluginValue = new JLabel();
			Box hBox = Box.createHorizontalBox();
			hBox.add(labelCopyPlugin);
			hBox.add(Box.createHorizontalStrut(5));
			hBox.add(labelCopyPluginValue);
			hBox.add(Box.createHorizontalStrut(20));
			buttonCopyPlugin = new JButton("View");
			hBox.add(buttonCopyPlugin);
			hBox.add(Box.createHorizontalGlue());
			vBox.add(hBox);

		}
		vBox.add(Box.createVerticalStrut(20));
		{
			labelMessage1 = new JLabel();
			Box hBox = Box.createHorizontalBox();
			hBox.add(labelMessage1);
			hBox.add(Box.createHorizontalGlue());
			vBox.add(hBox);
		}
		vBox.add(Box.createVerticalStrut(10));
		{
			labelMessage2 = new JLabel();
			Box hBox = Box.createHorizontalBox();
			hBox.add(labelMessage2);
			hBox.add(Box.createHorizontalGlue());
			vBox.add(hBox);
			vBox.add(Box.createVerticalStrut(10));
		}
		buttonCopyPlugin.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				buttonViewPluginPerformed();
			}
		});
	}

	public void init(String ts3Directory, String acrePuginPath) {

		boolean copyPlugin = false;
		this.ts3Directory = ts3Directory;

		/* Copy plugin to TS3 */
		File sourceLocation = new File(acrePuginPath);
		File targetLocation = new File(ts3Directory + "/plugins/"
				+ sourceLocation.getName());
		try {
			FileAccessMethods.copyFile(sourceLocation, targetLocation);
			copyPlugin = true;
		} catch (IOException e) {
			e.printStackTrace();
			copyPlugin = false;
		}

		labelCopyPluginValue.setFont(new Font("Tohama", Font.BOLD, 12));
		if (copyPlugin) {
			labelCopyPluginValue.setText(statusOK);
			labelCopyPluginValue.setForeground(new Color(45, 125, 45));
		} else {
			labelCopyPluginValue.setText(statusFail);
			labelCopyPluginValue.setForeground(Color.RED);
		}

		if (copyPlugin) {
			String message1 = "You can now run your game with @ACRE 2.";
			String message2 = "Don't forget to run TS3 with administrator priviledges.";
			labelMessage1.setText(message1);
			labelMessage2.setText(message2);
			labelMessage2.setFont(new Font("Tohama", Font.BOLD, 11));
		}
	}

	@Override
	public void buttonFistPerformed() {
		facade.getFirstPageACRE2InstallerPanel().setVisible(true);
		this.dispose();
	}

	@Override
	public void buttonSecondPerformed() {
		this.dispose();
	}

	private void buttonViewPluginPerformed() {
		if (Desktop.isDesktopSupported()) {
			Desktop desktop = Desktop.getDesktop();
			if (desktop.isSupported(Desktop.Action.OPEN)) {
				try {
					desktop.open(new File(ts3Directory + "/plugins"));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
