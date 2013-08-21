package fr.soe.a3s.ui.mainEditor;

import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;
import javax.swing.JButton;

import javax.swing.JDialog;
import javax.swing.JLabel;

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
public class JoinServerPanel extends JDialog implements UIConstants {

	private Facade facade;
	private JLabel label1;
	private JButton buttonOK;
	private JLabel label2;

	public JoinServerPanel(Facade facade) {
		super(facade.getMainPanel(), "Join server", false);
		this.facade = facade;
		this.setResizable(false);
		this.setSize(305, 130);
		setIconImage(ICON);
		this.setLocation(
				(int) facade.getMainPanel().getLocation().getX()
						+ facade.getMainPanel().getWidth() / 2
						- this.getWidth() / 2,
				(int) facade.getMainPanel().getLocation().getY()
						+ facade.getMainPanel().getHeight() / 2
						- this.getHeight() / 2);
		this.setLayout(null);
		{
			label1 = new JLabel();
			getContentPane().add(label1);
			label1.setText("This feature is currently disable due to an ArmA 3 bug.");
			label1.setBounds(12, 13, 292, 16);
		}
		{
			label2 = makeHyperLink("ArmA 3 feedback tracker",
					"http://feedback.arma3.com/view.php?id=11767", 23, 45);
			getContentPane().add(label2);
			label2.setBounds(89, 29, 141, 25);
		}
		{
			buttonOK = new JButton();
			getContentPane().add(buttonOK);
			buttonOK.setText("OK");
			buttonOK.setBounds(112, 66, 69, 27);
		}
		buttonOK.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				buttonOKPerformed();
			}
		});
	}
	
	private void buttonOKPerformed() {
		this.dispose();
	}

	public JLabel makeHyperLink(final String s, final String link, int x, int y) {
		final JLabel l = new JLabel(s);
		l.setText(String.format("<HTML><FONT color = \"#0080FF\"><U>%s</U></FONT></HTML>",
				s));
		l.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseExited(MouseEvent arg0) {
				l.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				l.setText(String.format("<HTML><FONT color = \"#0080FF\"><U>%s</U></FONT></HTML>",
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
