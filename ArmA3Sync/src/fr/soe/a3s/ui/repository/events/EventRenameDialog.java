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

public class EventRenameDialog extends AbstractEventDialog {

	private String eventName;
	// Services
	private RepositoryService repositoryService = new RepositoryService();

	public EventRenameDialog(Facade facade, String repositoryName,
			EventsPanel eventsPanel) {
		super(facade, repositoryName, eventsPanel);
		setTitle("Edit event");
	}

	public void init(String eventName, String description) {

		this.eventName = eventName;
		textFieldEventName.setText(eventName);
		textFieldDescription.setText(description);
		textFieldEventName.requestFocus();
		textFieldEventName.selectAll();
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
				for (EventDTO eventDTO : eventDTOs) {
					if (eventDTO.equals(newEventName)) {
						labelWarning.setText("duplicate name!");
						labelWarning.setFont(labelWarning.getFont().deriveFont(
								Font.ITALIC));
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

	@Override
	protected void buttonCancelPerformed() {
		this.dispose();
	}

	@Override
	protected void menuExitPerformed() {
		this.dispose();
	}
}
