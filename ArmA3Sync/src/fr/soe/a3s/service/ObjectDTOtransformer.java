package fr.soe.a3s.service;

import java.util.Iterator;
import java.util.List;

import fr.soe.a3s.constant.Protocole;
import fr.soe.a3s.domain.Http;
import fr.soe.a3s.domain.TreeDirectory;
import fr.soe.a3s.domain.TreeLeaf;
import fr.soe.a3s.domain.TreeNode;
import fr.soe.a3s.domain.configration.AiAOptions;
import fr.soe.a3s.domain.configration.ExternalApplication;
import fr.soe.a3s.domain.configration.FavoriteServer;
import fr.soe.a3s.domain.configration.LauncherOptions;
import fr.soe.a3s.domain.repository.AutoConfig;
import fr.soe.a3s.domain.repository.Changelog;
import fr.soe.a3s.domain.repository.Event;
import fr.soe.a3s.domain.repository.Repository;
import fr.soe.a3s.domain.repository.ServerInfo;
import fr.soe.a3s.domain.repository.SyncTreeDirectory;
import fr.soe.a3s.domain.repository.SyncTreeLeaf;
import fr.soe.a3s.domain.repository.SyncTreeNode;
import fr.soe.a3s.dto.AutoConfigDTO;
import fr.soe.a3s.dto.ChangelogDTO;
import fr.soe.a3s.dto.EventDTO;
import fr.soe.a3s.dto.ProtocoleDTO;
import fr.soe.a3s.dto.RepositoryDTO;
import fr.soe.a3s.dto.ServerInfoDTO;
import fr.soe.a3s.dto.TreeDirectoryDTO;
import fr.soe.a3s.dto.TreeLeafDTO;
import fr.soe.a3s.dto.TreeNodeDTO;
import fr.soe.a3s.dto.configuration.AiAOptionsDTO;
import fr.soe.a3s.dto.configuration.ExternalApplicationDTO;
import fr.soe.a3s.dto.configuration.FavoriteServerDTO;
import fr.soe.a3s.dto.configuration.LauncherOptionsDTO;
import fr.soe.a3s.dto.sync.SyncTreeDirectoryDTO;
import fr.soe.a3s.dto.sync.SyncTreeLeafDTO;

public class ObjectDTOtransformer {

	/* Configuration */

	protected ExternalApplicationDTO transformExternalApplication2DTO(
			ExternalApplication externalApplication) {

		final ExternalApplicationDTO externalApplicationDTO = new ExternalApplicationDTO();
		externalApplicationDTO.setName(externalApplication.getName());
		externalApplicationDTO.setExecutablePath(externalApplication
				.getExecutablePath());
		externalApplicationDTO.setEnable(externalApplication.isEnable());
		externalApplicationDTO.setParameters(externalApplication
				.getParameters());
		return externalApplicationDTO;
	}

	protected ExternalApplication transformDTO2ExternalApplication(
			ExternalApplicationDTO externalApplicationDTO) {

		final ExternalApplication externalApplication = new ExternalApplication();
		externalApplication.setName(externalApplicationDTO.getName());
		externalApplication.setExecutablePath(externalApplicationDTO
				.getExecutablePath());
		externalApplication.setEnable(externalApplicationDTO.isEnable());
		externalApplication.setParameters(externalApplicationDTO
				.getParameters());
		return externalApplication;
	}

	protected LauncherOptionsDTO transformLauncherOptions2DTO(
			LauncherOptions launcherOptions) {

		final LauncherOptionsDTO launcherOptionsDTO = new LauncherOptionsDTO();

		launcherOptionsDTO.setArma3ExePath(launcherOptions.getArma3ExePath());
		launcherOptionsDTO.setSteamExePath(launcherOptions.getSteamExePath());
		launcherOptionsDTO.setDefaultWorld(launcherOptions.isDefaultWorld());
		launcherOptionsDTO.setNoLogs(launcherOptions.isNologs());
		launcherOptionsDTO.setGameProfile(launcherOptions.getGameProfile());
		launcherOptionsDTO.setMaxMemorySelection(launcherOptions
				.getMaxMemorySelection());
		launcherOptionsDTO.setCpuCountSelection(launcherOptions
				.getCpuCountSelection());
		launcherOptionsDTO.setExThreadsSelection(launcherOptions
				.getExThreadsSelection());
		launcherOptionsDTO.setNoPause(launcherOptions.isNoPause());
		launcherOptionsDTO
				.setNoFilePatching(launcherOptions.isNoFilePatching());
		launcherOptionsDTO
				.setNoSplashScreen(launcherOptions.isNoSplashScreen());
		launcherOptionsDTO.setShowScriptError(launcherOptions
				.isShowScriptErrors());
		launcherOptionsDTO.setWindowMode(launcherOptions.isWindowMode());

		return launcherOptionsDTO;
	}

	protected FavoriteServerDTO transformFavoriteServers2DTO(
			FavoriteServer favoriteServer) {

		final FavoriteServerDTO favoriteServerDTO = new FavoriteServerDTO();
		favoriteServerDTO.setName(favoriteServer.getName());
		favoriteServerDTO.setIpAddress(favoriteServer.getIpAddress());
		favoriteServerDTO.setPort(favoriteServer.getPort());
		favoriteServerDTO.setPassword(favoriteServer.getPassword());
		favoriteServerDTO.setModsetName(favoriteServer.getModsetName());
		return favoriteServerDTO;
	}

	protected FavoriteServer transformDTO2FavoriteServer(
			FavoriteServerDTO favoriteServerDTO) {

		final FavoriteServer favoriteServer = new FavoriteServer();
		favoriteServer.setName(favoriteServerDTO.getName());
		favoriteServer.setIpAddress(favoriteServerDTO.getIpAddress());
		favoriteServer.setPort(favoriteServerDTO.getPort());
		favoriteServer.setPassword(favoriteServerDTO.getPassword());
		favoriteServer.setModsetName(favoriteServerDTO.getModsetName());
		return favoriteServer;
	}

	protected AiAOptionsDTO transformAiAOptions2DTO(AiAOptions aiaOptions) {

		final AiAOptionsDTO aiaOptionsDTO = new AiAOptionsDTO();
		aiaOptionsDTO.setArma2Path(aiaOptions.getArma2Path());
		aiaOptionsDTO.setArma2OAPath(aiaOptions.getArma2OAPath());
		aiaOptionsDTO.setArmaPath(aiaOptions.getArmaPath());
		aiaOptionsDTO.setTohPath(aiaOptions.getTohPath());
		aiaOptionsDTO.setAllinArmaPath(aiaOptions.getAllinArmaPath());
		return aiaOptionsDTO;
	}

	/* Profile */

	protected void transformTreeDirectory2DTO(TreeDirectory treeDirectory,
			TreeDirectoryDTO treeDirectoryDTO) {

		List<TreeNode> list = treeDirectory.getList();

		for (TreeNode treeNode : list) {
			if (treeNode.isLeaf()) {
				TreeLeaf treeLeaf = (TreeLeaf) treeNode;
				TreeLeafDTO treeLeafDTO = transformTreeLeaf2DTO(treeLeaf);
				treeLeafDTO.setParent(treeDirectoryDTO);
				treeDirectoryDTO.addTreeNode(treeLeafDTO);
			} else {
				TreeDirectory treeDirectory2 = (TreeDirectory) treeNode;
				TreeDirectoryDTO treedDirectoryDTO2 = new TreeDirectoryDTO();
				treedDirectoryDTO2.setName(treeDirectory2.getName());
				treedDirectoryDTO2
						.setModsetType(treeDirectory2.getModsetType());
				treedDirectoryDTO2.setSelected(treeDirectory2.isSelected());
				treedDirectoryDTO2.setParent(treeDirectoryDTO);
				treeDirectoryDTO.addTreeNode(treedDirectoryDTO2);
				transformTreeDirectory2DTO(treeDirectory2, treedDirectoryDTO2);
			}
		}
	}

	protected TreeLeafDTO transformTreeLeaf2DTO(TreeLeaf treeLeaf) {

		TreeLeafDTO treeLeafDTO = new TreeLeafDTO();
		treeLeafDTO.setName(treeLeaf.getName());
		treeLeafDTO.setSelected(treeLeaf.isSelected());
		treeLeafDTO.setOptional(treeLeaf.isOptional());
		return treeLeafDTO;
	}

	protected void transform2TreeDirectory(TreeDirectoryDTO treeDirectoryDTO,
			TreeDirectory treeDirectory) {

		List<TreeNodeDTO> list = treeDirectoryDTO.getList();

		for (TreeNodeDTO treeNodeDTO : list) {
			if (treeNodeDTO.isLeaf()) {
				TreeLeafDTO treeLeafDTO = (TreeLeafDTO) treeNodeDTO;
				TreeLeaf treeLeaf = transform2TreeLeaf(treeLeafDTO);
				treeLeaf.setParent(treeDirectory);
				treeDirectory.addTreeNode(treeLeaf);
			} else {
				TreeDirectoryDTO treeDirectoryDTO2 = (TreeDirectoryDTO) treeNodeDTO;
				TreeDirectory treedDirectory2 = new TreeDirectory(
						treeDirectoryDTO2.getName(), treeDirectory);
				treedDirectory2
						.setModsetType(treeDirectoryDTO2.getModsetType());
				treedDirectory2.setSelected(treeDirectoryDTO2.isSelected());
				treeDirectory.addTreeNode(treedDirectory2);
				transform2TreeDirectory(treeDirectoryDTO2, treedDirectory2);
			}
		}
	}

	protected TreeLeaf transform2TreeLeaf(TreeLeafDTO treeLeafDTO) {

		TreeLeaf treeLeaf = new TreeLeaf();
		treeLeaf.setName(treeLeafDTO.getName());
		treeLeaf.setSelected(treeLeafDTO.isSelected());
		treeLeaf.setOptional(treeLeafDTO.isOptional());
		return treeLeaf;
	}

	/* Repository */

	protected RepositoryDTO transformRepository2DTO(Repository repository) {
		final RepositoryDTO repositoryDTO = new RepositoryDTO();
		repositoryDTO.setName(repository.getName());
		repositoryDTO.setNotify(repository.isNotify());
		ProtocoleDTO protocoleDTO = new ProtocoleDTO();
		protocoleDTO.setUrl(repository.getProtocole().getUrl());
		protocoleDTO.setLogin(repository.getProtocole().getLogin());
		protocoleDTO.setPassword(repository.getProtocole().getPassword());
		protocoleDTO.setPort(repository.getProtocole().getPort());
		protocoleDTO.setEncryptionMode(repository.getProtocole()
				.getEncryptionMode());
		if (repository.getProtocole() instanceof Http) {
			protocoleDTO.setProtocole(Protocole.HTTP);
		} else {
			protocoleDTO.setProtocole(Protocole.FTP);
		}
		repositoryDTO.setProtocoleDTO(protocoleDTO);
		repositoryDTO.setPath(repository.getPath());
		repositoryDTO.setRevision(repository.getRevision());
		repositoryDTO.setAutoConfigURL(repository.getAutoConfigURL());
		repositoryDTO.setOutOfSynk(repository.isOutOfSynk());
		return repositoryDTO;
	}
	
	protected AutoConfigDTO transformAutoConfig2DTO(AutoConfig autoConfig) {

		final AutoConfigDTO autoConfigDTO = new AutoConfigDTO();
		autoConfigDTO.setRepositoryName(autoConfig.getRepositoryName());
		ProtocoleDTO protocoleDTO = new ProtocoleDTO();
		autoConfigDTO.setProtocoleDTO(protocoleDTO);
		protocoleDTO.setUrl(autoConfig.getProtocole().getUrl());
		protocoleDTO.setPort(autoConfig.getProtocole().getPort());
		protocoleDTO.setPassword(autoConfig.getProtocole().getPassword());
		protocoleDTO.setLogin(autoConfig.getProtocole().getLogin());
		protocoleDTO.setEncryptionMode(autoConfig.getProtocole()
				.getEncryptionMode());
		if (autoConfig.getProtocole() instanceof Http) {
			protocoleDTO.setProtocole(Protocole.HTTP);
		} else {
			protocoleDTO.setProtocole(Protocole.FTP);
		}
		return autoConfigDTO;
	}

	protected ServerInfoDTO transformServerInfo2DTO(ServerInfo serverInfo) {

		final ServerInfoDTO serverInfoDTO = new ServerInfoDTO();
		serverInfoDTO.setBuildDate(serverInfo.getBuildDate());
		serverInfoDTO.setNumberOfFiles(serverInfo.getNumberOfFiles());
		serverInfoDTO.setRevision(serverInfo.getRevision());
		serverInfoDTO.setTotalFilesSize(serverInfo.getTotalFilesSize());
		return serverInfoDTO;
	}

	protected ServerInfo transformDTO2ServerInfo(ServerInfoDTO serverInfoDTO) {

		final ServerInfo serverInfo = new ServerInfo();
		serverInfo.setBuildDate(serverInfoDTO.getBuildDate());
		serverInfo.setNumberOfFiles(serverInfoDTO.getNumberOfFiles());
		serverInfo.setRevision(serverInfoDTO.getRevision());
		serverInfo.setTotalFilesSize(serverInfoDTO.getTotalFilesSize());
		return serverInfo;
	}

	protected void transformSyncTreeDirectory2DTO(
			SyncTreeDirectory syncTreeDirectory,
			SyncTreeDirectoryDTO syncTreeDirectoryDTO) {

		List<SyncTreeNode> list = syncTreeDirectory.getList();

		for (SyncTreeNode node : list) {
			if (node.isLeaf()) {
				SyncTreeLeaf syncTreeLeaf = (SyncTreeLeaf) node;
				SyncTreeLeafDTO syncTreeLeafDTO = transformSyncTreeLeaf2DTO(syncTreeLeaf);
				syncTreeLeafDTO.setParent(syncTreeDirectoryDTO);
				syncTreeDirectoryDTO.addTreeNode(syncTreeLeafDTO);
				if (syncTreeLeafDTO.isUpdated() || syncTreeLeafDTO.isDeleted()) {
					SyncTreeDirectoryDTO parent = syncTreeLeafDTO.getParent();
					while (parent != null) {
						parent.setChanged(true);
						parent = parent.getParent();
					}
				}
			} else {
				SyncTreeDirectory syncTreeDirectory2 = (SyncTreeDirectory) node;
				SyncTreeDirectoryDTO syncTreedDirectoryDTO2 = new SyncTreeDirectoryDTO();
				syncTreedDirectoryDTO2.setName(syncTreeDirectory2.getName());
				syncTreedDirectoryDTO2.setParent(syncTreeDirectoryDTO);
				syncTreedDirectoryDTO2.setMarkAsAddon(syncTreeDirectory2
						.isMarkAsAddon());
				syncTreedDirectoryDTO2.setDestinationPath(syncTreeDirectory2
						.getDestinationPath());
				syncTreedDirectoryDTO2.setSelected(false);
				syncTreedDirectoryDTO2.setUpdated(false);
				syncTreedDirectoryDTO2.setDeleted(syncTreeDirectory2
						.isDeleted());
				syncTreedDirectoryDTO2.setHidden(syncTreeDirectory2.isHidden());
				syncTreeDirectoryDTO.addTreeNode(syncTreedDirectoryDTO2);
				transformSyncTreeDirectory2DTO(syncTreeDirectory2,
						syncTreedDirectoryDTO2);
			}
		}
	}

	protected SyncTreeLeafDTO transformSyncTreeLeaf2DTO(
			SyncTreeLeaf syncTreeLeaf) {

		SyncTreeLeafDTO syncTreeLeafDTO = new SyncTreeLeafDTO();
		syncTreeLeafDTO.setName(syncTreeLeaf.getName());
		syncTreeLeafDTO.setSize(syncTreeLeaf.getSize());
		syncTreeLeafDTO.setSelected(false);
		syncTreeLeafDTO.setDeleted(syncTreeLeaf.isDeleted());
		String remoteSHA1 = syncTreeLeaf.getSha1();
		String localSHA1 = syncTreeLeaf.getLocalSHA1();
		if (remoteSHA1 == null) {// remote does not exists => file to delete
			syncTreeLeafDTO.setUpdated(false);
		} else if (!remoteSHA1.equals(localSHA1)) {//localSHA1 == null if file does not exists locally
			syncTreeLeafDTO.setUpdated(true);
		} else {
			syncTreeLeafDTO.setUpdated(false);
		}
		syncTreeLeafDTO.setLocalSHA1(localSHA1);
		syncTreeLeafDTO.setDestinationPath(syncTreeLeaf.getDestinationPath());
		return syncTreeLeafDTO;
	}

	private void propagateUpdatedStatus(
			SyncTreeDirectoryDTO syncTreeDirectoryDTO) {

		if (syncTreeDirectoryDTO != null) {
			syncTreeDirectoryDTO.setUpdated(true);
			SyncTreeDirectoryDTO parentDTO = syncTreeDirectoryDTO.getParent();
			propagateUpdatedStatus(parentDTO);
		}
	}

	private void propagateDeletedStatus(
			SyncTreeDirectoryDTO syncTreeDirectoryDTO) {

		if (syncTreeDirectoryDTO != null) {
			syncTreeDirectoryDTO.setDeleted(true);
			SyncTreeDirectoryDTO parentDTO = syncTreeDirectoryDTO.getParent();
			propagateDeletedStatus(parentDTO);
		}
	}

	protected ChangelogDTO transformChangelog2DTO(Changelog changelog) {

		final ChangelogDTO changelogDTO = new ChangelogDTO();
		changelogDTO.setRevision(changelog.getRevision());
		changelogDTO.setBuildDate(changelog.getBuildDate());
		for (String stg : changelog.getUpdatedAddons()) {
			changelogDTO.getUpdatedAddons().add(stg);
		}
		for (String stg : changelog.getNewAddons()) {
			changelogDTO.getNewAddons().add(stg);
		}
		for (String stg : changelog.getDeletedAddons()) {
			changelogDTO.getDeletedAddons().add(stg);
		}
		return changelogDTO;
	}

	protected Event transformDTO2Event(EventDTO eventDTO) {

		final Event event = new Event(eventDTO.getName());
		event.setDescription(eventDTO.getDescription());
		for (Iterator<String> iter = eventDTO.getAddonNames().keySet()
				.iterator(); iter.hasNext();) {
			String key = iter.next();
			boolean value = eventDTO.getAddonNames().get(key);
			event.getAddonNames().put(key, value);
		}
		return event;
	}

	protected EventDTO transformEvent2DTO(Event event) {

		final EventDTO eventDTO = new EventDTO();
		eventDTO.setName(event.getName());
		eventDTO.setDescription(event.getDescription());
		for (Iterator<String> iter = event.getAddonNames().keySet().iterator(); iter
				.hasNext();) {
			String key = iter.next();
			boolean value = event.getAddonNames().get(key);
			eventDTO.getAddonNames().put(key, value);
		}
		for (Iterator<String> iter = event.getUserconfigFolderNames().keySet()
				.iterator(); iter.hasNext();) {
			String key = iter.next();
			boolean value = event.getUserconfigFolderNames().get(key);
			eventDTO.getUserconfigFolderNames().put(key, value);
		}
		return eventDTO;
	}

}
