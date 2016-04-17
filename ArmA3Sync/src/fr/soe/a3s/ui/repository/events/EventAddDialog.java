package fr.soe.a3s.ui.repository.events;

import java.awt.Color;
import java.awt.Font;
import java.util.List;

import javax.swing.JOptionPane;

import fr.soe.a3s.dto.EventDTO;
import fr.soe.a3s.exception.repository.RepositoryException;
import fr.soe.a3s.service.RepositoryService;
import fr.soe.a3s.ui.Facade;
import fr.soe.a3s.ui.repository.EventsPanel;

public class EventAddDialog extends AbstractEventDialog {

	// Services
	private RepositoryService repositoryService = new RepositoryService();

	public EventAddDialog(Facade facade, String repositoryName,
			EventsPanel eventsPanel) {
		super(facade, repositoryName, eventsPanel);
		this.setTitle("New event");
	}

	@Override
	protected void buttonOKPerformed() {

		String newEventName = textFieldEventName.getText().trim();
		String description = textFieldDescription.getText().trim();
		if (!newEventName.isEmpty()) {
			try {
				// No duplicate event name
				List<EventDTO> eventDTOs = repositoryService
						.getEvents(repositoryName);
				if (eventDTOs != null) {
					for (EventDTO eventDTO : eventDTOs) {
						if (eventDTO.getName().equals(newEventName)) {
							labelWarning.setText("duplicate name!");
							labelWarning.setFont(labelWarning.getFont()
									.deriveFont(Font.ITALIC));
							labelWarning.setForeground(Color.RED);
							return;
						}
					}
				}

				EventDTO eventDTO = new EventDTO();
				eventDTO.setName(newEventName);
				eventDTO.setDescription(description);
				repositoryService.addEvent(repositoryName, eventDTO);
				eventsPanel.updateListEvents();
				this.dispose();
			} catch (RepositoryException e) {
				JOptionPane.showMessageDialog(facade.getMainPanel(),
						e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
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
