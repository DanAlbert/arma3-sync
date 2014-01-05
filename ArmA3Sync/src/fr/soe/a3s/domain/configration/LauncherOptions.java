package fr.soe.a3s.domain.configration;

import java.io.Serializable;

public class LauncherOptions implements Serializable {

	private static final long serialVersionUID = 7112913210279158291L;

	/* Launcher options */
	private String gameProfile;
	private boolean showScriptErrors = false;
	private boolean noPause = false;
	private boolean noFilePatching = false;
	private boolean windowMode = false;
	private boolean runBeta = false;
	private boolean xpCompatibilityMode = false;
	private String maxMemorySelection;
	private int cpuCountSelection = 0;
	private String exThreadsSelection;
	private boolean noSplashScreen = false;
	private boolean defaultWorld = false;
	private boolean nologs = false;

	/* Executable location */
	private String arma3ExePath;

	private String steamExePath;

	public String getGameProfile() {
		return gameProfile;
	}

	public void setGameProfile(String gameProfile) {
		this.gameProfile = gameProfile;
	}

	public boolean isShowScriptErrors() {
		return showScriptErrors;
	}

	public void setShowScriptErrors(boolean showScriptErrors) {
		this.showScriptErrors = showScriptErrors;
	}

	public boolean isNoPause() {
		return noPause;
	}

	public void setNoPause(boolean noPause) {
		this.noPause = noPause;
	}

	public boolean isWindowMode() {
		return windowMode;
	}

	public void setWindowMode(boolean windowMode) {
		this.windowMode = windowMode;
	}

	public boolean isRunBeta() {
		return runBeta;
	}

	public void setRunBeta(boolean runBeta) {
		this.runBeta = runBeta;
	}

	public boolean isXpCompatibilityMode() {
		return xpCompatibilityMode;
	}

	public void setXpCompatibilityMode(boolean xpCompatibilityMode) {
		this.xpCompatibilityMode = xpCompatibilityMode;
	}

	public String getMaxMemorySelection() {
		return maxMemorySelection;
	}

	public void setMaxMemorySelection(String maxMemorySelection) {
		this.maxMemorySelection = maxMemorySelection;
	}

	public int getCpuCountSelection() {
		return cpuCountSelection;
	}

	public void setCpuCountSelection(int cpuCountSelection) {
		this.cpuCountSelection = cpuCountSelection;
	}

	public boolean isNoSplashScreen() {
		return noSplashScreen;
	}

	public void setNoSplashScreen(boolean noSplashScreen) {
		this.noSplashScreen = noSplashScreen;
	}

	public boolean isDefaultWorld() {
		return defaultWorld;
	}

	public void setDefaultWorld(boolean defaultWorld) {
		this.defaultWorld = defaultWorld;
	}

	public void setNoLogs(boolean nologs) {
		this.nologs = nologs;
	}

	public boolean isNologs() {
		return nologs;
	}

	public String getArma3ExePath() {
		return arma3ExePath;
	}

	public void setArma3ExePath(String arma3ExePath) {
		this.arma3ExePath = arma3ExePath;
	}

	public void setSteamExePath(String steamExePath) {
		this.steamExePath = steamExePath;
	}

	public String getSteamExePath() {
		return steamExePath;
	}

	public String getExThreadsSelection() {
		return exThreadsSelection;
	}

	public void setExThreadsSelection(String exThreadsSelection) {
		this.exThreadsSelection = exThreadsSelection;
	}

	public boolean isNoFilePatching() {
		return noFilePatching;
	}

	public void setNoFilePatching(boolean value) {
		this.noFilePatching = value;
	}
}
