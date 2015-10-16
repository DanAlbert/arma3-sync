package fr.soe.a3s.ui.about;

import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.BevelBorder;

import fr.soe.a3s.main.Version;
import fr.soe.a3s.ui.Facade;
import fr.soe.a3s.ui.ImagePanel;
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
/**
 * About dialog
 * 
 */

public class AboutPanel extends JDialog implements ActionListener, UIConstants {
	private JButton jButtonClose;
	private JButton buttonCredits;
	private JTextArea jTextArea5;
	private JTextArea jTextArea4;
	private JTextArea jTextArea3;
	private JTextArea jTextArea2;
	private JTextArea jTextArea1;
	private ImagePanel jPanel2;
	private JPanel jPanel1;
	private final Facade facade;

	public AboutPanel(Facade facade) {
		super(facade.getMainPanel(), "About", true);

		this.facade = facade;
		getContentPane().setLayout(null);
		setResizable(false);
		this.setSize(345, 250);
		setIconImage(ICON);
		this.setLocation(
				(int) facade.getMainPanel().getLocation().getX()
						+ facade.getMainPanel().getWidth() / 2
						- this.getWidth() / 2,
				(int) facade.getMainPanel().getLocation().getY()
						+ facade.getMainPanel().getHeight() / 2
						- this.getHeight() / 2);

		{
			jButtonClose = new JButton();
			getContentPane().add(jButtonClose, "North");
			jButtonClose.setText("Close");
			jButtonClose.setBounds(248, 189, 76, 27);
			jButtonClose.addActionListener(this);
		}
		{
			jPanel1 = new JPanel();
			getContentPane().add(jPanel1);
			jPanel1.setBounds(15, 11, 309, 172);
			jPanel1.setLayout(null);
			jPanel1.setBorder(BorderFactory
					.createEtchedBorder(BevelBorder.LOWERED));
			jPanel1.setBackground(new java.awt.Color(255, 255, 255));
			{
				jPanel2 = new ImagePanel();
				jPanel1.add(jPanel2);
				jPanel2.setBounds(181, 38, 114, 116);
				jPanel2.setBackground(new java.awt.Color(255, 255, 255));
				ImageIcon imageIcon = new ImageIcon(PICTURE);
				Image myNewImage = imageIcon.getImage();
				jPanel2.setImage(myNewImage);
				jPanel2.repaint();
			}
			{
				jTextArea1 = new JTextArea();
				jPanel1.add(jTextArea1);
				jTextArea1.setText(APPLICATION_NAME);
				jTextArea1.setBounds(12, 7, 170, 35);
				jTextArea1.setFont(new java.awt.Font("Tahoma", 1, 28));
				jTextArea1.setEditable(false);
			}
			{
				jTextArea2 = new JTextArea();
				jPanel1.add(jTextArea2);
				jTextArea2.setText("Version " + Version.getName());
				jTextArea2.setBounds(12, 116, 145, 22);
				jTextArea2.setFont(new java.awt.Font("Tahoma", 0, 11));
				jTextArea2.setEditable(false);
			}
			{
				jTextArea3 = new JTextArea();
				jPanel1.add(jTextArea3);
				jTextArea3.setText("Build " + Version.getVersion() + " ("
						+ Version.getYear() + ")");
				jTextArea3.setBounds(12, 138, 145, 22);
				jTextArea3.setFont(new java.awt.Font("Tahoma", 0, 11));
				jTextArea3.setEditable(false);
			}
			{
				jTextArea4 = new JTextArea();
				jPanel1.add(jTextArea4);
				jTextArea4.setText("by the [S.o.E] Team,");
				jTextArea4.setBounds(12, 53, 145, 24);
				jTextArea4.setFont(new java.awt.Font("Tahoma", 1, 11));
				jTextArea4.setEditable(false);
			}
			{
				// jTextArea5 = new JTextArea();
				// jPanel1.add(jTextArea5);
				// jTextArea5.setText("www.sonsofexiled.fr");
				// jTextArea5.setBounds(12, 76, 133, 22);
				// jTextArea5.setFont(new java.awt.Font("Tahoma", 1, 11));
				// jTextArea5.setEditable(false);
				JLabel l = makeHyperLink("www.sonsofexiled.fr",
						"www.sonsofexiled.fr", 12, 76);
				jPanel1.add(l);
				l.setBounds(12, 76, 133, 22);
			}
		}
		{
			buttonCredits = new JButton();
			getContentPane().add(buttonCredits);
			buttonCredits.setText("Credits");
			buttonCredits.setBounds(156, 189, 79, 27);
			buttonCredits.addActionListener(this);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == jButtonClose) {
			dispose();
		}
		if (e.getSource() == buttonCredits) {
			CreditsPanel credits = new CreditsPanel(this.facade);
			credits.setVisible(true);
		}
	}

	public JLabel makeHyperLink(final String s, final String link, int x, int y) {
		final JLabel l = new JLabel(s);
		l.setText(String.format(
				"<HTML><FONT color = \"#0080FF\"><U>%s</U></FONT></HTML>", s));
		l.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseExited(MouseEvent arg0) {
				l.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				l.setText(String
						.format("<HTML><FONT color = \"#0080FF\"><U>%s</U></FONT></HTML>",
								s));
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				l.setCursor(new Cursor(Cursor.HAND_CURSOR));
				l.setText(String
						.format("<HTML><FONT color = \"#000099\"><U>%s</U></FONT></HTML>",
								s));
			}

			@Override
			public void mouseClicked(MouseEvent arg0) {
				try {
					URI uri = new URI(link);
					if (Desktop.isDesktopSupported())
						Desktop.getDesktop().browse(uri);
				} catch (Exception e) {
				}
			}
		});
		l.setToolTipText(String.format("go to %s", link));
		return l;
	}
}
