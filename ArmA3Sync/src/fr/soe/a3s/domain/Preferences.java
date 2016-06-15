package fr.soe.a3s.domain;

import java.io.Serializable;

import fr.soe.a3s.constant.LookAndFeel;
import fr.soe.a3s.constant.MinimizationType;

public class Preferences implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7034433806261372240L;
	private MinimizationType launchPanelGameLaunch = MinimizationType.TASK_BAR;
	private MinimizationType launchPanelMinimized = MinimizationType.TASK_BAR;
	private final MinimizationType syncPanelMinimized = MinimizationType.TRAY;
	private LookAndFeel lookAndFeel = LookAndFeel.LAF_DEFAULT;

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
		if (lookAndFeel == null) {
			lookAndFeel = LookAndFeel.LAF_DEFAULT;
		}
		return lookAndFeel;
	}

	public void setLookAndFeel(LookAndFeel lookAndFeel) {
		this.lookAndFeel = lookAndFeel;
	}
}
