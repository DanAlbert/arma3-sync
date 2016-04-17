package fr.soe.a3s.ui.repository;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import fr.soe.a3s.ui.Facade;
import fr.soe.a3s.ui.ImageResizer;
import fr.soe.a3s.ui.ImageResizer.Resizing;
import fr.soe.a3s.ui.UIConstants;

public class RepositoryPanel extends JPanel implements UIConstants {

	private final Facade facade;
	private final JButton buttonRepository, buttonDownload, buttonEvents;
	private final JPanel centerPanel;
	private final AdminPanel adminPanel;
	private final DownloadPanel downloadPanel;
	private final EventsPanel eventsPanel;

	public RepositoryPanel(Facade facade) {
		this.facade = facade;
		this.setLayout(new BorderLayout());

		Box vertBox1 = Box.createVerticalBox();
		vertBox1.add(Box.createVerticalStrut(10));
		centerPanel = new JPanel();
		centerPanel.setLayout(new CardLayout());
		vertBox1.add(centerPanel);
		downloadPanel = new DownloadPanel(facade, this);
		downloadPanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(), "Download"));
		centerPanel.add(downloadPanel);
		adminPanel = new AdminPanel(facade, this);
		adminPanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(), "Repository"));
		centerPanel.add(adminPanel);
		eventsPanel = new EventsPanel(facade, this);
		eventsPanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(), "Events"));
		centerPanel.add(eventsPanel);
		this.add(vertBox1, BorderLayout.CENTER);

		Box vertBox2 = Box.createVerticalBox();
		vertBox2.add(Box.createVerticalStrut(25));
		buttonDownload = new JButton();
		ImageIcon downloadIcon = new ImageIcon(
				ImageResizer
						.resizeToScreenResolution(DOWNLOAD, Resizing.MEDIUM));
		buttonDownload.setIcon(downloadIcon);
		vertBox2.add(buttonDownload);
		buttonRepository = new JButton();
		ImageIcon repositoryIcon = new ImageIcon(
				ImageResizer.resizeToScreenResolution(REPOSITORY,
						Resizing.MEDIUM));
		buttonRepository.setIcon(repositoryIcon);
		vertBox2.add(buttonRepository);
		buttonEvents = new JButton();
		ImageIcon eventsIcon = new ImageIcon(
				ImageResizer.resizeToScreenResolution(EVENTS, Resizing.MEDIUM));
		buttonEvents.setIcon(eventsIcon);
		vertBox2.add(buttonEvents);
		this.add(vertBox2, BorderLayout.EAST);

		buttonDownload.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				buttonDownloadPerformed();
			}
		});
		buttonRepository.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				buttonRepositoryPerformed();
			}
		});
		buttonEvents.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				buttonEventsPerformed();
			}
		});

		setContextualHelp();
	}

	private void setContextualHelp() {
		buttonDownload.setToolTipText("Download");
		buttonRepository.setToolTipText("Repository");
		buttonEvents.setToolTipText("Events");
	}

	public void admin(String repositoryName) {

		buttonDownload.setVisible(true);
		buttonRepository.setVisible(true);
		buttonEvents.setVisible(true);
		downloadPanel.init(repositoryName, null);
		adminPanel.init(repositoryName);
		eventsPanel.init(repositoryName);
		CardLayout cl = (CardLayout) centerPanel.getLayout();
		cl.first(centerPanel);
		cl.next(centerPanel);
		getRootPane().setDefaultButton(buttonRepository);
		buttonRepository.requestFocus();
	}

	public void download(String repositoryName, String eventName) {

		buttonDownload.setVisible(true);
		buttonRepository.setVisible(false);
		buttonEvents.setVisible(false);
		downloadPanel.init(repositoryName, eventName);
		CardLayout cl = (CardLayout) centerPanel.getLayout();
		cl.first(centerPanel);
		getRootPane().setDefaultButton(buttonDownload);
		buttonDownload.requestFocus();
		downloadPanel.checkForAddons();
	}

	private void buttonDownloadPerformed() {
		CardLayout cl = (CardLayout) centerPanel.getLayout();
		cl.first(centerPanel);
	}

	private void buttonRepositoryPerformed() {
		CardLayout cl = (CardLayout) centerPanel.getLayout();
		cl.first(centerPanel);
		cl.next(centerPanel);
	}

	private void buttonEventsPerformed() {
		CardLayout cl = (CardLayout) centerPanel.getLayout();
		cl.last(centerPanel);
		this.revalidate();
	}

	public AdminPanel getAdminPanel() {
		return adminPanel;
	}

	public EventsPanel getEventsPanel() {
		return eventsPanel;
	}

	public DownloadPanel getDownloadPanel() {
		return downloadPanel;
	}
}
