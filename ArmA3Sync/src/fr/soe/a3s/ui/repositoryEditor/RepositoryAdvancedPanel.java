package fr.soe.a3s.ui.repositoryEditor;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import fr.soe.a3s.constant.TimeOutValues;
import fr.soe.a3s.ui.Facade;
import fr.soe.a3s.ui.UIConstants;


/**
* This code was edited or generated using CloudGarden's Jigloo
* SWT/Swing GUI Builder, which is free for non-commercial
* use. If Jigloo is being used commercially (ie, by a corporation,
* company or business for any purpose whatever) then you
* should purchase a license for each developer using Jigloo.
* Please visit www.cloudgarden.com for details.
* Use of Jigloo implies acceptance of these licensing terms.
* A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
* THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
* LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
*/
public class RepositoryAdvancedPanel extends JDialog implements UIConstants{

	private final Facade facade;
	private JButton buttonOK, buttonCancel;
	private JTextField textFieldConnectionTimeout;
	private JTextField textFiledReadTimeout;
	private JLabel labelReadTimeout;
	private JLabel labelConnectionTimeout;
	private JPanel advancedPanel;
	
	public RepositoryAdvancedPanel(Facade facade){
		super(facade.getMainPanel(), "Advanced configuration", true);
		this.facade = facade;
		this.setResizable(false);
		this.setSize(350, 160);
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
		}
		{
			JPanel centerPanel = new JPanel();
			GridLayout grid1 = new GridLayout(1, 1);
			centerPanel.setLayout(grid1);
			this.add(centerPanel, BorderLayout.CENTER);
			{
				advancedPanel = new JPanel();
				advancedPanel.setLayout(null);
				advancedPanel.setBorder(BorderFactory.createTitledBorder(
						BorderFactory.createEtchedBorder(), "Connection"));
			}
			Box vertBox = Box.createVerticalBox();
			vertBox.add(Box.createVerticalStrut(5));
			vertBox.add(advancedPanel);
			{
				labelConnectionTimeout = new JLabel();
				advancedPanel.add(labelConnectionTimeout);
				labelConnectionTimeout.setText("Connection timeout (milliseconds):");
				labelConnectionTimeout.setBounds(18, 23, 202, 22);
			}
			{
				labelReadTimeout = new JLabel();
				advancedPanel.add(labelReadTimeout);
				labelReadTimeout.setText("Read timeout (milliseconds):");
				labelReadTimeout.setBounds(18, 51, 202, 25);
			}
			{
				textFieldConnectionTimeout = new JTextField();
				advancedPanel.add(textFieldConnectionTimeout);
				textFieldConnectionTimeout.setBounds(232, 23, 65, 23);
			}
			{
				textFiledReadTimeout = new JTextField();
				advancedPanel.add(textFiledReadTimeout);
				textFiledReadTimeout.setBounds(232, 52, 65, 23);
			}
			centerPanel.add(vertBox);
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
	}
	
	public void init(){
		textFieldConnectionTimeout.setText(Integer.toString(TimeOutValues.CONNECTION_TIME_OUT.getValue()));
		textFiledReadTimeout.setText(Integer.toString(TimeOutValues.READ_TIME_OUT.getValue()));
	}
	
	private void buttonOKPerformed() {
		this.setVisible(false);
	}
	
	private void menuExitPerformed() {
		this.setVisible(false);
	}
	
	public JTextField getTextFiledConnectionTimeout(){
		return textFieldConnectionTimeout;
	}
	
	public JTextField getTextFiledReadTimeout(){
		return textFiledReadTimeout;
	}
}
