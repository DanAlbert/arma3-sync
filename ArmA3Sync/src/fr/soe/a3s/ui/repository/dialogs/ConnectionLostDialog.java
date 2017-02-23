package fr.soe.a3s.ui.repository.dialogs;

import java.awt.BorderLayout;
import java.awt.Image;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import fr.soe.a3s.ui.AbstractDialog;
import fr.soe.a3s.ui.Facade;
import fr.soe.a3s.ui.ImagePanel;
import fr.soe.a3s.ui.ImageResizer;

public class ConnectionLostDialog extends AbstractDialog {

	private JLabel labelConnectionLost, labelInfo;
	private Thread t;
	private int delay = 5;
	private boolean reconnect = true;

	public ConnectionLostDialog(Facade facade, String repositoryName,String title) {
		super(facade, title, true);
		this.setResizable(false);

		{
			buttonOK.setText("Connect");
			getRootPane().setDefaultButton(buttonOK);
		}
		{
			Box vBox = Box.createVerticalBox();
			labelConnectionLost = new JLabel("Repository: " + repositoryName
					+ " - Error: Connection failed.");
			vBox.add(labelConnectionLost);
			labelInfo = new JLabel("Trying to reconnect in " + delay
					+ " seconds.");
			vBox.add(labelInfo);
			this.add(vBox, BorderLayout.CENTER);
		}
		{
			Box vBox = Box.createVerticalBox();
			vBox.add(Box.createVerticalGlue());
			ImagePanel imagePanel = new ImagePanel();
			imagePanel.setImage(WARNING);
			imagePanel.repaint();
			vBox.add(imagePanel);
			vBox.add(Box.createVerticalGlue());
			this.add(vBox, BorderLayout.WEST);
		}
		this.pack();
		this.setLocationRelativeTo(facade.getMainPanel());
	}

	public void init() {

		t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					while (delay >= 0) {
						labelInfo.setText("Trying to reconnect in " + delay
								+ " seconds.");
						delay--;
						t.sleep(1000);
					}
				} catch (InterruptedException e) {
				} finally {
					setVisible(false);
					dispose();
					t.interrupt();
				}
			}
		});
		t.start();
	}

	@Override
	protected void buttonOKPerformed() {
		dispose();
	}

	@Override
	protected void buttonCancelPerformed() {
		dispose();
		this.reconnect = false;
	}

	@Override
	protected void menuExitPerformed() {
		dispose();
		this.reconnect = false;
	}

	public boolean reconnect() {
		return this.reconnect;
	}
}
