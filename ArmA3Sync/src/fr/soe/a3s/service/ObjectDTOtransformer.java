package fr.soe.a3s.service;

import java.util.List;

import fr.soe.a3s.domain.TreeDirectory;
import fr.soe.a3s.domain.TreeLeaf;
import fr.soe.a3s.domain.TreeNode;
import fr.soe.a3s.domain.configration.AiAOptions;
import fr.soe.a3s.domain.configration.ExternalApplication;
import fr.soe.a3s.domain.configration.FavoriteServer;
import fr.soe.a3s.domain.configration.LauncherOptions;
import fr.soe.a3s.dto.TreeDirectoryDTO;
import fr.soe.a3s.dto.TreeLeafDTO;
import fr.soe.a3s.dto.TreeNodeDTO;
import fr.soe.a3s.dto.configuration.AiAOptionsDTO;
import fr.soe.a3s.dto.configuration.ExternalApplicationDTO;
import fr.soe.a3s.dto.configuration.FavoriteServerDTO;
import fr.soe.a3s.dto.configuration.LauncherOptionsDTO;

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
		return favoriteServerDTO;
	}

	protected FavoriteServer transformDTO2FavoriteServer(
			FavoriteServerDTO favoriteServerDTO) {

		final FavoriteServer favoriteServer = new FavoriteServer();
		favoriteServer.setName(favoriteServerDTO.getName());
		favoriteServer.setIpAddress(favoriteServerDTO.getIpAddress());
		favoriteServer.setPort(favoriteServerDTO.getPort());
		favoriteServer.setPassword(favoriteServerDTO.getPassword());
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

}
