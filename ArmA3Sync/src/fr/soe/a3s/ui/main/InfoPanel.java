package fr.soe.a3s.ui.main;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;

import fr.soe.a3s.service.ConfigurationService;
import fr.soe.a3s.ui.Facade;
import fr.soe.a3s.ui.UIConstants;

public class InfoPanel extends JPanel implements UIConstants {

	private final Facade facade;
	private JLabel labelSelectedProfile;
	private final ConfigurationService configurationService = new ConfigurationService();

	public InfoPanel(Facade facade) {
		this.facade = facade;
		this.facade.setInfoPanel(this);
		this.setLayout(new BorderLayout());
		{
			labelSelectedProfile = new JLabel();
			Font boldFont = labelSelectedProfile.getFont()
					.deriveFont(Font.BOLD);
			labelSelectedProfile.setFont(boldFont);
			Box hBox = Box.createHorizontalBox();
			hBox.add(Box.createHorizontalStrut(10));
			hBox.add(labelSelectedProfile);
			this.add(hBox, BorderLayout.CENTER);
		}
	}

	public void init() {

		String profileName = configurationService.getProfileName();
		labelSelectedProfile.setText("Profile: " + profileName);
	}
}
