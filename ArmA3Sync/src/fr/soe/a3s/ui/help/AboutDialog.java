package fr.soe.a3s.ui.help;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import fr.soe.a3s.main.Version;
import fr.soe.a3s.ui.AbstractDialog;
import fr.soe.a3s.ui.Facade;
import fr.soe.a3s.ui.ImagePanel;
import fr.soe.a3s.ui.ImageResizer;

public class AboutDialog extends AbstractDialog {

	private JPanel panelLeft;
	private ImagePanel panelRight;

	public AboutDialog(Facade facade) {
		super(facade, "About", true);
		setResizable(false);

		{
			buttonOK.setText("Credits");
			buttonCancel.setText("Close");
			buttonCancel.setPreferredSize(buttonOK.getPreferredSize());
			getRootPane().setDefaultButton(buttonCancel);
		}
		{
			JPanel mainPanel = new JPanel();
			mainPanel.setBorder(BorderFactory
					.createEtchedBorder(BevelBorder.LOWERED));
			mainPanel.setLayout(new BorderLayout());
			mainPanel.setBackground(new java.awt.Color(255, 255, 255));
			this.add(mainPanel, BorderLayout.CENTER);
			{
				JPanel sidePanel1 = new JPanel();
				sidePanel1.setBackground(new java.awt.Color(255, 255, 255));
				mainPanel.add(sidePanel1, BorderLayout.NORTH);
				JPanel sidePanel2 = new JPanel();
				sidePanel2.setBackground(new java.awt.Color(255, 255, 255));
				mainPanel.add(sidePanel2, BorderLayout.WEST);
				JPanel sidePanel3 = new JPanel();
				sidePanel3.setBackground(new java.awt.Color(255, 255, 255));
				mainPanel.add(sidePanel3, BorderLayout.EAST);
				JPanel sidePanel4 = new JPanel();
				sidePanel4.setBackground(new java.awt.Color(255, 255, 255));
				mainPanel.add(sidePanel4, BorderLayout.SOUTH);
			}
			{
				Box hBox = Box.createHorizontalBox();
				mainPanel.add(hBox, BorderLayout.CENTER);
				{
					panelLeft = new JPanel();
					panelLeft.setBackground(new java.awt.Color(255, 255, 255));
					panelLeft.setLayout(new GridBagLayout());
					hBox.add(panelLeft);
					{
						Box vBox = Box.createVerticalBox();
						hBox.add(vBox);
						{
							JLabel label = new JLabel(APPLICATION_NAME);
							label.setFont(label.getFont().deriveFont(Font.BOLD,
									new Float(32)));
							vBox.add(label);
						}
						vBox.add(Box.createVerticalStrut(10));
						{
							JLabel label = new JLabel("by the [S.o.E] Team,");
							vBox.add(label);
						}
						{
							JLabel label = makeHyperLink("www.sonsofexiled.fr",
									"www.sonsofexiled.fr");
							vBox.add(label);
						}
						vBox.add(Box.createVerticalStrut(10));
						{
							JLabel label = new JLabel("Version" + " "
									+ Version.getName());
							vBox.add(label);
						}
						{
							JLabel label = new JLabel("Build "
									+ Version.getVersion() + " ("
									+ Version.getYear() + ")");
							vBox.add(label);
						}
					}

					hBox.add(Box.createHorizontalStrut(10));

					panelRight = new ImagePanel();
					Image image = ImageResizer.resizeToNewWidth(PICTURE, 120);
					panelRight.setImage(image);
					panelRight.repaint();
					panelRight.setBackground(new java.awt.Color(255, 255, 255));
					hBox.add(panelRight);
				}
			}
		}

		this.pack();
		this.setLocationRelativeTo(facade.getMainPanel());
	}

	public JLabel makeHyperLink(final String s, final String link) {
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

	@Override
	protected void buttonOKPerformed() {
		AboutCreditsDialog creditsDialog = new AboutCreditsDialog(facade);
		creditsDialog.setVisible(true);
	}

	@Override
	protected void buttonCancelPerformed() {
		this.dispose();
	}

	@Override
	protected void menuExitPerformed() {
		this.dispose();
	}
}
