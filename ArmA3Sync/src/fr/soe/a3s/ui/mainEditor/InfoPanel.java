package fr.soe.a3s.ui.mainEditor;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;

import fr.soe.a3s.service.ConfigurationService;
import fr.soe.a3s.ui.Facade;
import fr.soe.a3s.ui.UIConstants;

public class InfoPanel extends JPanel implements UIConstants{

	private Facade facade;
	private JLabel labelSelectedProfile;
	private ConfigurationService configurationService = new ConfigurationService();
	
	public InfoPanel(Facade facade) {
		this.facade = facade;
		this.facade.setInfoPanel(this);
		setPreferredSize(new Dimension(DEFAULT_WIDTH,30));
	    this.setLayout(new FlowLayout(FlowLayout.LEFT));
	    labelSelectedProfile = new JLabel();
	    this.add(labelSelectedProfile);
	}
	
	public void init(){
		String profileName = configurationService.getProfileName();
		labelSelectedProfile.setText("   Profile : " + profileName);
		labelSelectedProfile.setFont(new Font("Tohama", Font.BOLD, 12));
	}
	
}
