package fr.soe.a3s.dto.configuration;

import fr.soe.a3s.constant.LookAndFeel;
import fr.soe.a3s.constant.MinimizationType;

public class PreferencesDTO {

	private MinimizationType launchPanelGameLaunch;

	private MinimizationType launchPanelMinimized;

	private LookAndFeel lookAndFeel;

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
}
