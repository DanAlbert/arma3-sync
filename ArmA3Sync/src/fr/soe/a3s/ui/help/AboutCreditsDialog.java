package fr.soe.a3s.ui.help;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Image;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;

import fr.soe.a3s.ui.AbstractDialog;
import fr.soe.a3s.ui.Facade;
import fr.soe.a3s.ui.ImagePanel;
import fr.soe.a3s.ui.ImageResizer;

public class AboutCreditsDialog extends AbstractDialog {

	public AboutCreditsDialog(Facade facade) {
		super(facade, "Credits", true);
		setResizable(false);

		{
			{
				buttonOK.setEnabled(false);
				buttonOK.setVisible(false);
				buttonCancel.setText("Close");
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
						JTextArea textArea = new JTextArea();
						hBox.add(textArea);
						textArea.setText("Software development\n"
								+ "[S.o.E] Major_Shepard\n"
								+ "\nGraphical design\n" + "[S.o.E] Matt2507\n"
								+ "\nTesting\n"
								+ "[S.o.E],[F27],[BWF]\nTeam Members\n"
								+ "\nInspired from \n"
								+ "- ArmA II Game Launcher"
								+ "\n  by SpiritedMachine.\n"
								+ "- AddonSync 2009 by Yoma.");
						Font fontTextField = UIManager
								.getFont("TextField.font");
						textArea.setFont(fontTextField);
						textArea.setEditable(false);
					}

					hBox.add(Box.createHorizontalStrut(10));

					{
						Box vBox = Box.createVerticalBox();
						hBox.add(vBox);
						{
							vBox.add(Box.createVerticalGlue());
							ImagePanel imagePanel = new ImagePanel();
							Image image = ImageResizer.resizeToNewWidth(SOE,
									150);
							imagePanel.setImage(image);
							imagePanel.repaint();
							imagePanel.setBackground(new java.awt.Color(255,
									255, 255));
							vBox.add(imagePanel);
							vBox.add(Box.createVerticalGlue());
						}
					}
				}
			}
		}

		this.pack();
		this.setLocationRelativeTo(facade.getMainPanel());
	}

	@Override
	protected void buttonOKPerformed() {
		this.dispose();
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
