package fr.soe.a3s.domain;

import java.io.Serializable;

import fr.soe.a3s.constant.CheckRepositoriesFrequency;
import fr.soe.a3s.constant.IconResize;
import fr.soe.a3s.constant.LookAndFeel;
import fr.soe.a3s.constant.MinimizationType;
import fr.soe.a3s.constant.StartWithOS;

public class Preferences implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7034433806261372240L;
	private MinimizationType launchPanelGameLaunch = MinimizationType.TASK_BAR;
	private MinimizationType launchPanelMinimized = MinimizationType.TASK_BAR;
	private final MinimizationType syncPanelMinimized = MinimizationType.TRAY;
	private LookAndFeel lookAndFeel = LookAndFeel.LAF_DEFAULT;
	private IconResize iconResizeSize = IconResize.AUTO;
	private StartWithOS startWithOS = StartWithOS.DISABLED;
	private CheckRepositoriesFrequency checkRepositoriesFrequency = CheckRepositoriesFrequency.FREQ3;

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

	public IconResize getIconResizeSize() {
		if (iconResizeSize == null) {
			iconResizeSize = IconResize.AUTO;
		}
		return iconResizeSize;
	}

	public void setIconResizeSize(IconResize iconResizeSize) {
		this.iconResizeSize = iconResizeSize;
	}

	public StartWithOS getStartWithOS() {
		if (startWithOS == null) {
			startWithOS = StartWithOS.DISABLED;
		}
		return startWithOS;
	}

	public void setStartWithOS(StartWithOS startWithOS) {
		this.startWithOS = startWithOS;
	}

	public CheckRepositoriesFrequency getCheckRepositoriesFrequency() {
		if (checkRepositoriesFrequency == null) {
			checkRepositoriesFrequency = CheckRepositoriesFrequency.FREQ3;
		}
		return checkRepositoriesFrequency;
	}

	public void setCheckRepositoriesFrequency(
			CheckRepositoriesFrequency checkRepositoriesFrequency) {
		this.checkRepositoriesFrequency = checkRepositoriesFrequency;
	}
}
