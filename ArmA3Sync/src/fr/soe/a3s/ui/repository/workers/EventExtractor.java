package fr.soe.a3s.ui.repository.workers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.soe.a3s.dto.EventDTO;
import fr.soe.a3s.dto.sync.SyncTreeDirectoryDTO;
import fr.soe.a3s.dto.sync.SyncTreeNodeDTO;
import fr.soe.a3s.service.RepositoryService;

public class EventExtractor {

	private SyncTreeDirectoryDTO parent;
	private final String eventName;
	private final String repositoryName;
	private boolean found;
	private final RepositoryService repositoryService = new RepositoryService();

	public EventExtractor(SyncTreeDirectoryDTO parent, String eventName,
			String repositoryName) {
		this.parent = parent;
		this.eventName = eventName;
		this.repositoryName = repositoryName;
	}

	public SyncTreeDirectoryDTO run() {

		if (eventName != null) {
			List<EventDTO> eventDTOs = repositoryService
					.getEvents(repositoryName);
			Map<String, Boolean> addonNames = new HashMap<String, Boolean>();
			Map<String, Boolean> userconfigFolderNames = new HashMap<String, Boolean>();
			if (eventDTOs != null) {
				for (EventDTO eventDTO : eventDTOs) {
					if (eventDTO.getName().equals(this.eventName)) {
						addonNames = eventDTO.getAddonNames();
						userconfigFolderNames = eventDTO
								.getUserconfigFolderNames();
						break;
					}
				}
			}

			SyncTreeDirectoryDTO newRacine = new SyncTreeDirectoryDTO();
			newRacine.setName(this.parent.getName());
			newRacine.setParent(null);
			if (!userconfigFolderNames.isEmpty()) {
				refineUserconfig(this.parent, newRacine, userconfigFolderNames);
			}
			if (!addonNames.isEmpty()) {
				refineAddons(this.parent, newRacine, addonNames);
			}
			this.parent = newRacine;
		}
		return this.parent;
	}

	private void refineAddons(SyncTreeDirectoryDTO oldRacine,
			SyncTreeDirectoryDTO newRacine, Map<String, Boolean> addonNames) {

		for (SyncTreeNodeDTO nodeDTO : oldRacine.getList()) {
			if (!nodeDTO.isLeaf()) {
				SyncTreeDirectoryDTO directoryDTO = (SyncTreeDirectoryDTO) nodeDTO;
				if (directoryDTO.isMarkAsAddon()
						&& addonNames.containsKey(nodeDTO.getName())) {
					newRacine.addTreeNode(directoryDTO);
					directoryDTO.setParent(newRacine);
					directoryDTO.setOptional(addonNames.get(nodeDTO.getName()));
				} else if (!directoryDTO.isMarkAsAddon()) {
					this.found = false;
					seek(directoryDTO, addonNames);
					if (this.found) {
						SyncTreeDirectoryDTO newDirectory = new SyncTreeDirectoryDTO();
						newDirectory.setName(directoryDTO.getName());
						newRacine.addTreeNode(newDirectory);
						newDirectory.setParent(newRacine);
						refineAddons(directoryDTO, newDirectory, addonNames);
					}
				}
			}
		}
	}

	private void seek(SyncTreeDirectoryDTO seakDirectory,
			Map<String, Boolean> addonNames) {

		for (SyncTreeNodeDTO nodeDTO : seakDirectory.getList()) {
			if (!nodeDTO.isLeaf()) {
				SyncTreeDirectoryDTO directoryDTO = (SyncTreeDirectoryDTO) nodeDTO;
				if (directoryDTO.isMarkAsAddon()
						&& addonNames.containsKey(nodeDTO.getName())) {
					this.found = true;
					directoryDTO.setOptional(addonNames.get(nodeDTO.getName()));
				} else {
					seek(directoryDTO, addonNames);
				}
			}
		}
	}

	private void refineUserconfig(SyncTreeDirectoryDTO oldRacine,
			SyncTreeDirectoryDTO newRacine,
			Map<String, Boolean> userconfigFolderNames) {

		SyncTreeDirectoryDTO userconfigNode = null;

		for (SyncTreeNodeDTO nodeDTO : oldRacine.getList()) {
			if (!nodeDTO.isLeaf()
					&& nodeDTO.getName().toLowerCase().equals("userconfig")) {
				userconfigNode = (SyncTreeDirectoryDTO) nodeDTO;
				break;
			}
		}

		if (userconfigNode != null) {
			SyncTreeDirectoryDTO newUserconfigNode = new SyncTreeDirectoryDTO();
			newUserconfigNode.setName(userconfigNode.getName());
			newRacine.addTreeNode(newUserconfigNode);
			for (SyncTreeNodeDTO nodeDTO : userconfigNode.getList()) {
				if (!nodeDTO.isLeaf()
						&& userconfigFolderNames.containsKey(nodeDTO.getName())) {
					newUserconfigNode.addTreeNode(nodeDTO);
					nodeDTO.setOptional(userconfigFolderNames.get(nodeDTO
							.getName()));
				}
			}
		}
	}
}
