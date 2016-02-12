package fr.soe.a3s.ui.repositoryEditor.dialogs;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;

import fr.soe.a3s.ui.Facade;
import fr.soe.a3s.ui.UIConstants;

/**
 * This code was edited or generated using CloudGarden's Jigloo SWT/Swing GUI
 * Builder, which is free for non-commercial use. If Jigloo is being used
 * commercially (ie, by a corporation, company or business for any purpose
 * whatever) then you should purchase a license for each developer using Jigloo.
 * Please visit www.cloudgarden.com for details. Use of Jigloo implies
 * acceptance of these licensing terms. A COMMERCIAL LICENSE HAS NOT BEEN
 * PURCHASED FOR THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED LEGALLY FOR
 * ANY CORPORATE OR COMMERCIAL PURPOSE.
 */
public class LoginPanel extends JDialog implements UIConstants {

	private final Facade facade;
	private JLabel labelLogin;
	private JTextField textFieldLogin;
	private JLabel labelPassword;
	private JPasswordField textFieldPassword;
	private JButton buttonOK, buttonCancel;
	
	private String login = null;
	private char[] password = null;

	public LoginPanel(Facade facade) {
		super(facade.getMainPanel(), "Repository", true);
		this.facade = facade;
		this.setResizable(false);
		this.setSize(300, 170);
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
			centerPanel.setLayout(null);
			centerPanel.setBorder(BorderFactory.createEtchedBorder());
			this.add(centerPanel, BorderLayout.CENTER);
			{
				labelLogin = new JLabel();
				centerPanel.add(labelLogin);
				labelLogin.setText("Login");
				labelLogin.setBounds(19, 20, 62, 25);
			}
			{
				textFieldLogin = new JTextField();
				centerPanel.add(textFieldLogin);
				textFieldLogin.setBounds(93, 20, 166, 25);
			}
			{
				labelPassword = new JLabel();
				centerPanel.add(labelPassword);
				labelPassword.setText("Password");
				labelPassword.setBounds(19, 54, 62, 25);
			}
			{
				textFieldPassword = new JPasswordField();
				centerPanel.add(textFieldPassword);
				textFieldPassword.setBounds(93, 53, 166, 25);
			}
		}
		buttonOK.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				buttonOKPerformed();
			}
		});
		buttonCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				menuExitPerformed();
			}
		});
		// Add Listeners
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				menuExitPerformed();
			}
		});
	}

	private void buttonOKPerformed() {
		
		login = textFieldLogin.getText();
		password = textFieldPassword.getPassword();
		this.dispose();
	}

	private void menuExitPerformed() {
		
		login = null;
		password = null;
		this.dispose();
	}

	public String getLogin() {
		return login;
	}

	public char[] getPassword() {
		return password;
	}
}
