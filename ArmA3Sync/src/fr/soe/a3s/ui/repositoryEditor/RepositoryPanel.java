package fr.soe.a3s.ui.repositoryEditor;

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
import fr.soe.a3s.ui.UIConstants;

public class RepositoryPanel extends JPanel implements UIConstants {

	private Facade facade;
	private JButton buttonRepository, buttonDownload, buttonEvents;
	private JPanel centerPanel;
	private AdminPanel adminPanel;
	private DownloadPanel downloadPanel;
	private EventsPanel eventsPanel;

	public RepositoryPanel(Facade facade) {
		this.facade = facade;
		this.facade.setRepositoryPanel(this);
		this.setLayout(new BorderLayout());

		Box vertBox1 = Box.createVerticalBox();
		vertBox1.add(Box.createVerticalStrut(10));
		centerPanel = new JPanel();
		centerPanel.setLayout(new CardLayout());
		vertBox1.add(centerPanel);
		downloadPanel = new DownloadPanel(facade);
		downloadPanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(), "Download"));
		centerPanel.add(downloadPanel);
		adminPanel = new AdminPanel(facade);
		adminPanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(), "Repository"));
		centerPanel.add(adminPanel);
		eventsPanel = new EventsPanel(facade);
		eventsPanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(), "Events"));
		centerPanel.add(eventsPanel);
		this.add(vertBox1, BorderLayout.CENTER);

		Box vertBox2 = Box.createVerticalBox();
		vertBox2.add(Box.createVerticalStrut(25));
		buttonDownload = new JButton();
		ImageIcon downloadIcon = new ImageIcon(DOWNLOAD);
		buttonDownload.setIcon(downloadIcon);
		vertBox2.add(buttonDownload);
		buttonRepository = new JButton();
		ImageIcon repositoryIcon = new ImageIcon(REPOSITORY);
		buttonRepository.setIcon(repositoryIcon);
		vertBox2.add(buttonRepository);
		buttonEvents = new JButton();
		ImageIcon eventsIcon = new ImageIcon(EVENTS);
		buttonEvents.setIcon(eventsIcon);
		vertBox2.add(buttonEvents);
	
		this.add(vertBox2, BorderLayout.EAST);

		buttonRepository.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				buttonRepositoryPerformed();
			}
		});
		buttonDownload.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				buttonDownloadPerformed();
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
		buttonRepository.setToolTipText("Repository");
		buttonDownload.setToolTipText("Download");
		buttonEvents.setToolTipText("Events");
	}

	public void init(String repositoryName) {
		adminPanel.init(repositoryName);
		downloadPanel.init(repositoryName);
		eventsPanel.init(repositoryName);
	}
	
	public void init(String repositoryName,String eventName) {
		downloadPanel.init(repositoryName,eventName);
		buttonRepository.setVisible(false);
		buttonEvents.setVisible(false);
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
	}
}
