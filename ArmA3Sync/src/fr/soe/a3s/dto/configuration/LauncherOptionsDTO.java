package fr.soe.a3s.dto.configuration;


public class LauncherOptionsDTO {

	/* Launcher options */
	private String gameProfile;
	private boolean showScriptError;
	private boolean noPause;
	private boolean windowMode;
	private boolean runBeta;
	private String maxMemorySelection;
	private int cpuCountSelection;
	private boolean noSplashScreen;
	private boolean defaultWorld;
	private boolean noLogs;
	
	/* Executable location */
	private String arma3ExePath;
	private String steamExePath;

	public String getGameProfile() {
		return gameProfile;
	}
	public void setGameProfile(String gameProfile) {
		this.gameProfile = gameProfile;
	}
	public boolean isShowScriptError() {
		return showScriptError;
	}
	public void setShowScriptError(boolean showScriptError) {
		this.showScriptError = showScriptError;
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
	public String getArma3ExePath() {
		return arma3ExePath;
	}
	public void setArma3ExePath(String arma2ExePath) {
		this.arma3ExePath = arma2ExePath;
	}
	public String getSteamExePath() {
		return steamExePath;
	}
	public void setSteamExePath(String steamExePath) {
		this.steamExePath = steamExePath;
	}
	public boolean isNoLogs() {
		return noLogs;
	}
	public void setNoLogs(boolean noLogs) {
		this.noLogs = noLogs;
	}

}
