package fr.soe.a3s.domain.configration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import fr.soe.a3s.constant.DefaultProfileName;
import fr.soe.a3s.constant.GameVersions;

public class Configuration implements Serializable {

	private static final long serialVersionUID = -8310476744472497506L;
	private int height = 0;
	private int width = 0;
	private String profileName = DefaultProfileName.DEFAULT.getDescription();
	private String gameVersion = GameVersions.ARMA3.getDescription();
	private String serverName;
	private String defaultModset;
	private boolean viewModeTree = true;
	private Set<String> addonSearchDirectoryPaths = new TreeSet<String>();
	private final LauncherOptions launcherOptions = new LauncherOptions();
	private List<FavoriteServer> favoriteServers = new ArrayList<FavoriteServer>();
	private List<ExternalApplication> externalApplications = new ArrayList<ExternalApplication>();
	private AcreOptions acreOptions = new AcreOptions();
	private Acre2Options acre2Options = new Acre2Options();
	private TfarOptions tfarOptions = new TfarOptions();
	private AiAOptions aiaOptions = new AiAOptions();
	private RptOptions rptOptions = new RptOptions();

	public String getProfileName() {
		return profileName;
	}

	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}

	public String getGameVersion() {
		return gameVersion;
	}

	public void setGameVersion(String gameVersion) {
		this.gameVersion = gameVersion;
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public Set<String> getAddonSearchDirectoryPaths() {
		return addonSearchDirectoryPaths;
	}

	public LauncherOptions getLauncherOptions() {
		return launcherOptions;
	}

	public List<FavoriteServer> getFavoriteServers() {
		return favoriteServers;
	}

	public List<ExternalApplication> getExternalApplications() {
		return externalApplications;
	}

	public void setAddonSearchDirectoryPaths(
			Set<String> addonSearchDirectoryPaths) {
		this.addonSearchDirectoryPaths = addonSearchDirectoryPaths;
	}

	public void setFavoriteServers(List<FavoriteServer> favoriteServers) {
		this.favoriteServers = favoriteServers;
	}

	public void setExternalApplications(
			List<ExternalApplication> externalApplications) {
		this.externalApplications = externalApplications;
	}

	public boolean isViewModeTree() {
		return viewModeTree;
	}

	public void setViewModeTree(boolean viewModeTree) {
		this.viewModeTree = viewModeTree;
	}

	public AiAOptions getAiaOptions() {
		if (aiaOptions == null) {
			aiaOptions = new AiAOptions();
		}
		return aiaOptions;
	}

	public AcreOptions getAcreOptions() {
		if (acreOptions == null) {
			acreOptions = new AcreOptions();
		}
		return acreOptions;
	}

	public Acre2Options getAcre2Options() {
		if (acre2Options == null) {
			acre2Options = new Acre2Options();
		}
		return acre2Options;
	}

	public TfarOptions getTfarOptions() {
		if (tfarOptions == null) {
			tfarOptions = new TfarOptions();
		}
		return tfarOptions;
	}

	public RptOptions getRptOptions() {
		if (rptOptions == null) {
			rptOptions = new RptOptions();
		}
		return rptOptions;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public String getDefaultModset() {
		return defaultModset;
	}

	public void setDefaultModset(String defaultModset) {
		this.defaultModset = defaultModset;
	}
}
