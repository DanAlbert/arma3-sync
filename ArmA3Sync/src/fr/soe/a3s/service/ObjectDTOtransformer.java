package fr.soe.a3s.service;

import java.util.Iterator;
import java.util.List;

import fr.soe.a3s.domain.AbstractProtocole;
import fr.soe.a3s.domain.TreeDirectory;
import fr.soe.a3s.domain.TreeLeaf;
import fr.soe.a3s.domain.TreeNode;
import fr.soe.a3s.domain.configration.AiAOptions;
import fr.soe.a3s.domain.configration.ExternalApplication;
import fr.soe.a3s.domain.configration.FavoriteServer;
import fr.soe.a3s.domain.configration.LauncherOptions;
import fr.soe.a3s.domain.configration.Proxy;
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
import fr.soe.a3s.dto.ProtocolDTO;
import fr.soe.a3s.dto.RepositoryDTO;
import fr.soe.a3s.dto.ServerInfoDTO;
import fr.soe.a3s.dto.TreeDirectoryDTO;
import fr.soe.a3s.dto.TreeLeafDTO;
import fr.soe.a3s.dto.TreeNodeDTO;
import fr.soe.a3s.dto.configuration.AiAOptionsDTO;
import fr.soe.a3s.dto.configuration.ExternalApplicationDTO;
import fr.soe.a3s.dto.configuration.FavoriteServerDTO;
import fr.soe.a3s.dto.configuration.LauncherOptionsDTO;
import fr.soe.a3s.dto.configuration.ProxyDTO;
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
		launcherOptionsDTO.setMallocSelection(launcherOptions
				.getMallocSelection());
		launcherOptionsDTO.setEnableHT(launcherOptions.isEnableHT());
		launcherOptionsDTO.setNoPause(launcherOptions.isNoPause());
		launcherOptionsDTO.setFilePatching(launcherOptions.isFilePatching());
		launcherOptionsDTO
				.setNoSplashScreen(launcherOptions.isNoSplashScreen());
		launcherOptionsDTO.setShowScriptError(launcherOptions
				.isShowScriptErrors());
		launcherOptionsDTO.setWindowMode(launcherOptions.isWindowMode());
		launcherOptionsDTO.setCheckSignatures(launcherOptions
				.isCheckSignatures());
		launcherOptionsDTO.setAutoRestart(launcherOptions.isAutoRestart());

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
		favoriteServerDTO.setRepositoryName(favoriteServer.getRepositoryName());
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
		favoriteServer.setRepositoryName(favoriteServerDTO.getRepositoryName());
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
	
	protected ProxyDTO transformProxy2DTO(Proxy proxy) {

		final ProxyDTO proxyDTO = new ProxyDTO();
		AbstractProtocole protocol = proxy.getProxyProtocol();
		if (protocol != null) {
			ProtocolDTO protocolDTO = new ProtocolDTO();
			protocolDTO.setUrl(protocol.getUrl());
			protocolDTO.setPort(protocol.getPort());
			protocolDTO.setLogin(protocol.getLogin());
			protocolDTO.setPassword(protocol.getPassword());
			protocolDTO.setProtocolType(protocol.getProtocolType());
			protocolDTO.setReadTimeOut(protocol.getReadTimeOut());
			protocolDTO.setConnectionTimeOut(protocol.getConnectionTimeOut());
			proxyDTO.setProtocolDTO(protocolDTO);
		}
		proxyDTO.setEnableProxy(proxy.isEnableProxy());
		return proxyDTO;
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
				treedDirectoryDTO2.setModsetRepositoryName(treeDirectory2
						.getModsetRepositoryName());
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
		treeLeafDTO.setDuplicate(treeLeaf.isDuplicate());
		return treeLeafDTO;
	}

	protected void transformDTO2TreeDirectory(
			TreeDirectoryDTO treeDirectoryDTO, TreeDirectory treeDirectory) {

		List<TreeNodeDTO> list = treeDirectoryDTO.getList();

		for (TreeNodeDTO treeNodeDTO : list) {
			if (treeNodeDTO.isLeaf()) {
				TreeLeafDTO treeLeafDTO = (TreeLeafDTO) treeNodeDTO;
				TreeLeaf treeLeaf = transformDTO2TreeLeaf(treeLeafDTO);
				treeLeaf.setParent(treeDirectory);
				treeDirectory.addTreeNode(treeLeaf);
			} else {
				TreeDirectoryDTO treeDirectoryDTO2 = (TreeDirectoryDTO) treeNodeDTO;
				TreeDirectory treedDirectory2 = new TreeDirectory(
						treeDirectoryDTO2.getName(), treeDirectory);
				treedDirectory2
						.setModsetType(treeDirectoryDTO2.getModsetType());
				treedDirectory2.setModsetRepositoryName(treeDirectoryDTO2
						.getModsetRepositoryName());
				treedDirectory2.setSelected(treeDirectoryDTO2.isSelected());
				treeDirectory.addTreeNode(treedDirectory2);
				transformDTO2TreeDirectory(treeDirectoryDTO2, treedDirectory2);
			}
		}
	}

	protected TreeLeaf transformDTO2TreeLeaf(TreeLeafDTO treeLeafDTO) {

		TreeLeaf treeLeaf = new TreeLeaf();
		treeLeaf.setName(treeLeafDTO.getName());
		treeLeaf.setSelected(treeLeafDTO.isSelected());
		treeLeaf.setOptional(treeLeafDTO.isOptional());
		treeLeaf.setDuplicate(treeLeafDTO.isDuplicate());
		return treeLeaf;
	}

	/* Repository */

	protected RepositoryDTO transformRepository2DTO(Repository repository) {
		final RepositoryDTO repositoryDTO = new RepositoryDTO();
		repositoryDTO.setName(repository.getName());
		repositoryDTO.setNotify(repository.isNotify());
		// Protocole
		ProtocolDTO protocoleDTO = new ProtocolDTO();
		protocoleDTO.setUrl(repository.getProtocol().getUrl());
		protocoleDTO.setLogin(repository.getProtocol().getLogin());
		protocoleDTO.setPassword(repository.getProtocol().getPassword());
		protocoleDTO.setPort(repository.getProtocol().getPort());
		protocoleDTO
				.setProtocolType(repository.getProtocol().getProtocolType());
		protocoleDTO.setConnectionTimeOut(repository.getProtocol()
				.getConnectionTimeOut());
		protocoleDTO.setReadTimeOut(repository.getProtocol().getReadTimeOut());
		repositoryDTO.setProtocoleDTO(protocoleDTO);
		// Repository upload protocole
		ProtocolDTO repositoryUploadProtocoleDTO = new ProtocolDTO();
		if (repository.getUploadProtocole() != null) {
			repositoryUploadProtocoleDTO.setUrl(repository.getUploadProtocole()
					.getUrl());
			repositoryUploadProtocoleDTO.setLogin(repository
					.getUploadProtocole().getLogin());
			repositoryUploadProtocoleDTO.setPassword(repository
					.getUploadProtocole().getPassword());
			repositoryUploadProtocoleDTO.setPort(repository
					.getUploadProtocole().getPort());
			repositoryUploadProtocoleDTO.setProtocolType(repository
					.getUploadProtocole().getProtocolType());
			repositoryUploadProtocoleDTO.setConnectionTimeOut(repository
					.getUploadProtocole().getConnectionTimeOut());
			repositoryUploadProtocoleDTO.setReadTimeOut(repository
					.getUploadProtocole().getReadTimeOut());
			repositoryDTO.setUploadProtocoleDTO(repositoryUploadProtocoleDTO);
		}

		repositoryDTO.setPath(repository.getPath());
		repositoryDTO.setRevision(repository.getRevision());
		repositoryDTO.setAutoConfigURL(repository.getAutoConfigURL());
		return repositoryDTO;
	}

	protected AutoConfigDTO transformAutoConfig2DTO(AutoConfig autoConfig) {

		final AutoConfigDTO autoConfigDTO = new AutoConfigDTO();
		autoConfigDTO.setRepositoryName(autoConfig.getRepositoryName());
		ProtocolDTO protocoleDTO = new ProtocolDTO();
		autoConfigDTO.setProtocoleDTO(protocoleDTO);
		protocoleDTO.setUrl(autoConfig.getProtocole().getUrl());
		protocoleDTO.setPort(autoConfig.getProtocole().getPort());
		protocoleDTO.setPassword(autoConfig.getProtocole().getPassword());
		protocoleDTO.setLogin(autoConfig.getProtocole().getLogin());
		protocoleDTO.setProtocolType(autoConfig.getProtocole()
				.getProtocolType());
		protocoleDTO.setConnectionTimeOut(autoConfig.getProtocole()
				.getConnectionTimeOut());
		protocoleDTO.setReadTimeOut(autoConfig.getProtocole().getReadTimeOut());
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
				syncTreedDirectoryDTO2.setUpdated(syncTreeDirectory2
						.isUpdated());
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
		syncTreeLeafDTO.setCompressedSize(syncTreeLeaf.getCompressedSize());
		syncTreeLeafDTO.setSelected(false);
		syncTreeLeafDTO.setUpdated(syncTreeLeaf.isUpdated());
		syncTreeLeafDTO.setDeleted(syncTreeLeaf.isDeleted());
		syncTreeLeafDTO.setCompressed(syncTreeLeaf.isCompressed());
		String remoteSHA1 = syncTreeLeaf.getSha1();
		String localSHA1 = syncTreeLeaf.getLocalSHA1();
		syncTreeLeafDTO.setLocalSHA1(localSHA1);
		syncTreeLeafDTO.setSha1(syncTreeLeaf.getSha1());
		syncTreeLeafDTO.setDestinationPath(syncTreeLeaf.getDestinationPath());
		return syncTreeLeafDTO;
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
