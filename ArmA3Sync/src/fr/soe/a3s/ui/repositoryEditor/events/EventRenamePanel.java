package fr.soe.a3s.ui.repositoryEditor.events;

import java.awt.Color;
import java.awt.Font;
import java.util.List;

import javax.swing.JOptionPane;

import fr.soe.a3s.dto.EventDTO;
import fr.soe.a3s.exception.repository.RepositoryException;
import fr.soe.a3s.ui.Facade;
import fr.soe.a3s.ui.repositoryEditor.EventsPanel;

public class EventRenamePanel extends EventEditPanel {

	private String eventName;
	private String description;
	private EventsPanel eventsPanel;

	public EventRenamePanel(Facade facade, String repositoryName,EventsPanel eventsPanel) {
		super(facade, repositoryName,eventsPanel);
		this.eventsPanel = eventsPanel;
	}

	public void init(String eventName, String description) {
		setTitle("Edit event");
		textFieldEventName.setText(eventName);
		textFieldDescription.setText(description);
		this.eventName = eventName;
		this.description = description;
		textFieldEventName.requestFocus();
		textFieldEventName.selectAll();
	}

	@Override
	public void buttonOKPerformed(String newEventName, String description) {

		try {
			// No duplicate event name
			List<EventDTO> eventDTOs = repositoryService
					.getEvents(repositoryName);
			for (EventDTO eventDTO : eventDTOs) {
				if (eventDTO.equals(newEventName)) {
					labelWarning.setText("duplicate name!");
					labelWarning.setFont(new Font("Tohama", Font.ITALIC, 11));
					labelWarning.setForeground(Color.RED);
					return;
				}
			}

			repositoryService.renameEvent(repositoryName, eventName,
					newEventName, description);
			eventsPanel.updateListEvents();
			this.dispose();
		} catch (RepositoryException e) {
			JOptionPane.showMessageDialog(facade.getMainPanel(),
					e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

}
