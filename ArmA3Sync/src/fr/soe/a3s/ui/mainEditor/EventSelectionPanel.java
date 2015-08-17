package fr.soe.a3s.ui.mainEditor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.BevelBorder;

import fr.soe.a3s.dto.EventDTO;
import fr.soe.a3s.dto.RepositoryDTO;
import fr.soe.a3s.exception.repository.RepositoryException;
import fr.soe.a3s.service.RepositoryService;
import fr.soe.a3s.ui.CheckBoxList;
import fr.soe.a3s.ui.Facade;
import fr.soe.a3s.ui.UIConstants;

public class EventSelectionPanel extends JDialog implements UIConstants {

	private Facade facade;
	private JButton buttonOK;
	private JButton buttonCancel;
	private CheckBoxList listEvents;
	private CheckBoxList listRepositories;
	private JScrollPane scrollPane1, scrollPane2;
	private RepositoryService repositoryService = new RepositoryService();
	private List<EventDTO> eventDTOs = new ArrayList<EventDTO>();
	private List<RepositoryDTO> repositoryDTOs = new ArrayList<RepositoryDTO>();

	public EventSelectionPanel(Facade facade) {
		super(facade.getMainPanel(), "Modsets", true);
		this.facade = facade;
		setResizable(true);
		this.setMinimumSize(new Dimension(400, 430));
		setIconImage(ICON);
		this.setLocation(
				(int) facade.getMainPanel().getLocation().getX()
						+ facade.getMainPanel().getWidth() / 2
						- this.getWidth() / 2,
				(int) facade.getMainPanel().getLocation().getY()
						+ facade.getMainPanel().getHeight() / 2
						- this.getHeight() / 2);

		// Container contenu = getContentPane();
		this.setLayout(new BorderLayout());
		{
			JPanel centerPanel = new JPanel();
			GridLayout grid1 = new GridLayout(1, 1);
			centerPanel.setLayout(grid1);
			this.add(centerPanel, BorderLayout.CENTER);

			JPanel repositoriesPanel = new JPanel();
			repositoriesPanel.setLayout(new BorderLayout());
			{
				repositoriesPanel.setBorder(BorderFactory.createTitledBorder(
						BorderFactory.createEtchedBorder(),
						"Get addon group from Repository"));
				listRepositories = new CheckBoxList();
				scrollPane1 = new JScrollPane(listRepositories);
				scrollPane1.setColumnHeader(null);
				scrollPane1.setBorder(BorderFactory
						.createEtchedBorder(BevelBorder.LOWERED));
				repositoriesPanel.add(scrollPane1, BorderLayout.CENTER);
			}

			JPanel eventsPanel = new JPanel();
			eventsPanel.setLayout(new BorderLayout());
			{
				eventsPanel.setBorder(BorderFactory.createTitledBorder(
						BorderFactory.createEtchedBorder(),
						"Get addon group from Event"));
				listEvents = new CheckBoxList();
				scrollPane2 = new JScrollPane(listEvents);
				scrollPane2.setColumnHeader(null);
				scrollPane2.setBorder(BorderFactory
						.createEtchedBorder(BevelBorder.LOWERED));
				eventsPanel.add(scrollPane2, BorderLayout.CENTER);
			}

			Box vertBox = Box.createVerticalBox();
			vertBox.add(Box.createVerticalStrut(10));
			vertBox.add(repositoriesPanel);
			vertBox.add(Box.createVerticalStrut(10));
			vertBox.add(eventsPanel);
			vertBox.add(Box.createVerticalStrut(5));
			centerPanel.add(vertBox);
		}
		{
			JPanel eastPanel = new JPanel();
			JPanel westPanel = new JPanel();
			this.add(eastPanel, BorderLayout.EAST);
			this.add(westPanel, BorderLayout.WEST);
		}
		{
			JPanel controlPanel = new JPanel();
			FlowLayout flowLayout = new FlowLayout(FlowLayout.RIGHT);
			controlPanel.setLayout(flowLayout);
			buttonOK = new JButton("OK");
			getRootPane().setDefaultButton(buttonOK);
			buttonCancel = new JButton("Cancel");
			buttonOK.setPreferredSize(buttonCancel.getPreferredSize());
			controlPanel.add(buttonOK);
			controlPanel.add(buttonCancel);
			this.add(controlPanel, BorderLayout.SOUTH);
		}

		// {
		// JPanel topPanel = new JPanel();
		// topPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		// JLabel label = new JLabel();
		// label.setText("  Get addon group from event");
		// topPanel.add(label);
		// contenu.add(topPanel, BorderLayout.NORTH);
		// }
		// {
		// JPanel centerPanel = new JPanel();
		// contenu.add(centerPanel, BorderLayout.CENTER);
		// centerPanel.setLayout(new BorderLayout());
		// listEvents = new JList();
		// scrollPane = new JScrollPane(listEvents);
		// scrollPane.setColumnHeader(null);
		// scrollPane.setBorder(BorderFactory
		// .createEtchedBorder(BevelBorder.LOWERED));
		// centerPanel.add(scrollPane);
		// }

		buttonOK.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				buttonOKPerformed();
			}
		});
		buttonCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				buttonCancelPerformed();
			}
		});
	}

	public void init() {

		repositoryDTOs = repositoryService.getRepositories();

		Collections.sort(repositoryDTOs);

		JCheckBox[] tab1 = new JCheckBox[repositoryDTOs.size()];
		for (int i = 0; i < repositoryDTOs.size(); i++) {
			String repositoryName = repositoryDTOs.get(i).getName();
			JCheckBox checkBox = new JCheckBox();
			checkBox.setText(repositoryName);
			tab1[i] = checkBox;
		}
		listRepositories.setListData(tab1);

		for (RepositoryDTO repositoryDTO : repositoryDTOs) {
			try {
				List<EventDTO> list = repositoryService.getEvents(repositoryDTO
						.getName());
				if (list != null) {
					for (EventDTO eventDTO : list) {
						eventDTOs.add(eventDTO);
					}
				}
			} catch (RepositoryException e) {
				e.printStackTrace();
			}
		}

		Collections.sort(eventDTOs);

		JCheckBox[] tab2 = new JCheckBox[eventDTOs.size()];
		for (int i = 0; i < eventDTOs.size(); i++) {
			String profileName = eventDTOs.get(i).getName();
			JCheckBox checkBox = new JCheckBox();
			checkBox.setText(profileName);
			tab2[i] = checkBox;
		}
		listEvents.setListData(tab2);
	}

	private void buttonOKPerformed() {

		if (!(listRepositories.getSelectedItems().size() == 0)) {
			List<String> list = new ArrayList<String>();
			for (int i = 0; i < listRepositories.getSelectedItems().size(); i++) {
				String name = listRepositories.getSelectedItems().get(i);
				for (RepositoryDTO repositoryDTO : repositoryDTOs) {
					if (repositoryDTO.getName().equals(name)) {
						list.add(repositoryDTO.getName());
						break;
					}
				}
			}
			facade.getAddonsPanel().createGroupFromRepository(list);
		}

		if (!(listEvents.getSelectedItems().size() == 0)) {
			List<EventDTO> list = new ArrayList<EventDTO>();
			for (int i = 0; i < listEvents.getSelectedItems().size(); i++) {
				String name = listEvents.getSelectedItems().get(i);
				for (EventDTO eventDTO : eventDTOs) {
					if (eventDTO.getName().equals(name)) {
						list.add(eventDTO);
						break;
					}
				}
			}
			facade.getAddonsPanel().createGroupFromEvents(list);
		}

		this.dispose();

		// int index = listEvents.getSelectedIndex();
		//
		// if (index == -1 || (index > listEvents.getVisibleRowCount())) {
		// this.dispose();
		// } else {
		// EventDTO eventDTO = eventDTOs.get(index);
		// facade.getAddonsPanel().createGroupFromEvent(eventDTO.getName(),
		// eventDTO.getAddonNames());
		// this.dispose();
		// }
	}

	private void buttonCancelPerformed() {
		this.dispose();
	}
}
