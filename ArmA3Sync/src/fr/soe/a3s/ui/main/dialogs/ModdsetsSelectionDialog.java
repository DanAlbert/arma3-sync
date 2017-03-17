package fr.soe.a3s.ui.main.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.BevelBorder;

import fr.soe.a3s.dto.EventDTO;
import fr.soe.a3s.dto.RepositoryDTO;
import fr.soe.a3s.service.RepositoryService;
import fr.soe.a3s.ui.AbstractDialog;
import fr.soe.a3s.ui.CheckBoxList;
import fr.soe.a3s.ui.Facade;

public class ModdsetsSelectionDialog extends AbstractDialog {

	private CheckBoxList listEvents;
	private CheckBoxList listRepositories;
	private JScrollPane scrollPane1, scrollPane2;
	private final RepositoryService repositoryService = new RepositoryService();
	private final List<EventDTO> eventDTOs = new ArrayList<EventDTO>();
	private List<RepositoryDTO> repositoryDTOs = new ArrayList<RepositoryDTO>();

	public ModdsetsSelectionDialog(Facade facade) {
		super(facade, "Modsets", true);
		setResizable(true);

		{
			buttonOK.setPreferredSize(buttonCancel.getPreferredSize());
			getRootPane().setDefaultButton(buttonOK);
		}
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

		this.setMinimumSize(new Dimension(400, 430));
		this.pack();
		this.setLocationRelativeTo(facade.getMainPanel());
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
			List<EventDTO> list = repositoryService.getEvents(repositoryDTO
					.getName());
			for (EventDTO eventDTO : list) {
				eventDTOs.add(eventDTO);
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

	@Override
	protected void buttonOKPerformed() {

		List<RepositoryDTO> selectedRepositoryDTOs = new ArrayList<RepositoryDTO>();
		for (int i = 0; i < listRepositories.getSelectedItems().size(); i++) {
			String name = listRepositories.getSelectedItems().get(i);
			for (RepositoryDTO repositoryDTO : repositoryDTOs) {
				if (repositoryDTO.getName().equals(name)) {
					selectedRepositoryDTOs.add(repositoryDTO);
					break;
				}
			}
		}

		List<EventDTO> selectedEventDTOs = new ArrayList<EventDTO>();
		for (int i = 0; i < listEvents.getSelectedItems().size(); i++) {
			String name = listEvents.getSelectedItems().get(i);
			for (EventDTO eventDTO : eventDTOs) {
				if (eventDTO.getName().equals(name)) {
					selectedEventDTOs.add(eventDTO);
					break;
				}
			}
		}

		{
			List<String> repositoryNames = new ArrayList<String>();
			for (RepositoryDTO repositoryDTO : selectedRepositoryDTOs) {
				repositoryNames.add(repositoryDTO.getName());
			}
			if (!repositoryNames.isEmpty()) {
				facade.getAddonsPanel().getGroupManager()
						.addGroupFromRepository(repositoryNames,false);
			}
			if (!selectedEventDTOs.isEmpty()) {
				facade.getAddonsPanel().getGroupManager()
						.addGroupFromEvents(selectedEventDTOs,false);
			}
		}
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
