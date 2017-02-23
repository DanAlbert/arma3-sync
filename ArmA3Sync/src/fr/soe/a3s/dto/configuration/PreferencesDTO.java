package fr.soe.a3s.dto.configuration;

import fr.soe.a3s.constant.CheckRepositoriesFrequency;
import fr.soe.a3s.constant.IconResize;
import fr.soe.a3s.constant.LookAndFeel;
import fr.soe.a3s.constant.MinimizationType;
import fr.soe.a3s.constant.StartWithOS;

public class PreferencesDTO {

	private MinimizationType launchPanelGameLaunch;
	private MinimizationType launchPanelMinimized;
	private LookAndFeel lookAndFeel;
	private IconResize iconResizeSize;
	private StartWithOS startWithOS;
	private CheckRepositoriesFrequency checkRepositoriesFrequency;

	public MinimizationType getLaunchPanelGameLaunch() {
		return launchPanelGameLaunch;
	}

	public void setLaunchPanelGameLaunch(MinimizationType launchPanelGameLaunch) {
		this.launchPanelGameLaunch = launchPanelGameLaunch;
	}

	public MinimizationType getLaunchPanelMinimized() {
		return launchPanelMinimized;
	}

	public void setLaunchPanelMinimized(MinimizationType launchPanelMinimized) {
		this.launchPanelMinimized = launchPanelMinimized;
	}

	public LookAndFeel getLookAndFeel() {
		return lookAndFeel;
	}

	public void setLookAndFeel(LookAndFeel lookAndFeel) {
		this.lookAndFeel = lookAndFeel;
	}

	public IconResize getIconResizeSize() {
		return iconResizeSize;
	}

	public void setIconResizeSize(IconResize iconResizeSize) {
		this.iconResizeSize = iconResizeSize;
	}

	public StartWithOS getStartWithOS() {
		return startWithOS;
	}

	public void setStartWithOS(StartWithOS startWithOS) {
		this.startWithOS = startWithOS;
	}

	public CheckRepositoriesFrequency getCheckRepositoriesFrequency() {
		return checkRepositoriesFrequency;
	}

	public void setCheckRepositoriesFrequency(
			CheckRepositoriesFrequency checkRepositoriesFrequency) {
		this.checkRepositoriesFrequency = checkRepositoriesFrequency;
	}
}
