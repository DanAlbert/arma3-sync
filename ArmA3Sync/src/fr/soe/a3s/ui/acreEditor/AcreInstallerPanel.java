package fr.soe.a3s.ui.acreEditor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import fr.soe.a3s.ui.Facade;
import fr.soe.a3s.ui.UIConstants;

public abstract class AcreInstallerPanel extends JDialog implements UIConstants{

	protected Facade facade;
	protected JButton buttonFist,buttonSecond;
	
	public AcreInstallerPanel(Facade facade){
		super(facade.getMainPanel(), "ACRE installer wizard", false);
		this.facade = facade;
		this.setMinimumSize(new Dimension(DEFAULT_WIDTH, 500));
		setIconImage(ICON);
		this.setLocation(
				(int) facade.getMainPanel().getLocation().getX()
						+ facade.getMainPanel().getWidth() / 2
						- this.getWidth() / 2,
				(int) facade.getMainPanel().getLocation().getY()
						+ facade.getMainPanel().getHeight() / 2
						- this.getHeight() / 2);
		this.setLayout(new BorderLayout());
		{
			JPanel topPanel = new JPanel();
			this.add(topPanel, BorderLayout.NORTH);
			topPanel.setLayout(null);
			topPanel.setBackground(new java.awt.Color(255, 255, 255));
			topPanel.setPreferredSize(new java.awt.Dimension(484, 55));
			{
				ImagePanel acreImagePanel = new ImagePanel();
				acreImagePanel.setBackground(new java.awt.Color(255, 255, 255));
				ImageIcon imageIcon = new ImageIcon(
						java.lang.ClassLoader
								.getSystemResource("resources/pictures/system/acre48x48.png"));
				Image myNewImage = imageIcon.getImage();
				acreImagePanel.setImage(myNewImage);
				topPanel.add(acreImagePanel);
				acreImagePanel.setBounds(6, 6, 28, 42);
				acreImagePanel.repaint();
			}
			{
				JLabel labelDescription = new JLabel();
				labelDescription.setText("Install or update ACRE for ArmA 3 and TS3");
				topPanel.add(labelDescription);
				labelDescription.setBounds(40, 6, 300, 42);
			}
		}
		{
			JPanel controlPanel = new JPanel();
			buttonFist = new JButton();
			buttonFist.setPreferredSize(new Dimension(80,25));
			buttonSecond = new JButton();
			buttonSecond.setPreferredSize(new Dimension(80,25));
			FlowLayout flowLayout = new FlowLayout(FlowLayout.RIGHT);
			controlPanel.setLayout(flowLayout);
			controlPanel.add(buttonFist);
			controlPanel.add(buttonSecond);
			this.add(controlPanel, BorderLayout.SOUTH);
		}
		buttonFist.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				buttonFistPerformed();
			}
		});
		buttonSecond.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				buttonSecondPerformed();
			}
		});
		// Add Listeners
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				menuExitPerformed();
			}
		});
	}
	
	public abstract void buttonFistPerformed();
	
	public abstract void buttonSecondPerformed();
	
	private void menuExitPerformed() {
		this.dispose();
	}
	
	class ImagePanel extends JPanel {

		private Image image = null;

		public void setImage(Image image) {
			this.image = image;
		}

		public void paintComponent(Graphics g) {
			super.paintComponent(g); // paint background
			if (image != null) { // there is a picture: draw it
				int height = this.getSize().height;
				int width = this.getSize().width;
				// g.drawImage(image, 0, 0, this); //use image size

				g.drawImage(image, 0, 0, width, height, this);
			}
		}
	}
}
