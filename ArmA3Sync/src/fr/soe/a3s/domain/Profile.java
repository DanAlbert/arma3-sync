package fr.soe.a3s.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import fr.soe.a3s.domain.configration.LauncherOptions;

public class Profile implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4395099790449721021L;
	private String name;
	private TreeDirectory tree = new TreeDirectory("racine", null);
	private String additionalParameters;
	private List<String> addonNamesByPriority = new ArrayList<String>();
	private LauncherOptions launcherOptions = new LauncherOptions();
	private List<String> addonSearchDirectories = new ArrayList<String>();

	public Profile(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public TreeDirectory getTree() {
		return tree;
	}

	public String getAdditionalParameters() {
		return additionalParameters;
	}

	public void setAdditionalParameters(String additionalParameters) {
		this.additionalParameters = additionalParameters;
	}

	public List<String> getAddonNamesByPriority() {
		if (addonNamesByPriority == null) {
			addonNamesByPriority = new ArrayList<String>();
		}
		return addonNamesByPriority;
	}

	public LauncherOptions getLauncherOptions() {
		if (launcherOptions == null) {
			launcherOptions = new LauncherOptions();
		}
		return launcherOptions;
	}

	public void setLauncherOptions(LauncherOptions launcherOptions) {
		this.launcherOptions = launcherOptions;
	}

	public List<String> getAddonSearchDirectories() {
		if (addonSearchDirectories == null) {
			addonSearchDirectories = new ArrayList<String>();
		}
		return addonSearchDirectories;
	}

	public void setAddonSearchDirectories(List<String> addonSearchDirectories) {
		this.addonSearchDirectories = addonSearchDirectories;
	}
}
