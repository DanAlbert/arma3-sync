package fr.soe.a3s.domain;

public class Addon {

	private final String key;
	private final String name;
	private final String path;
	private boolean atArmA3InstallRoot;

	public Addon(String key, String name, String path) {
		this.key = key;
		this.name = name;
		this.path = path;
	}

	public String getKey() {
		return key;
	}

	public String getName() {
		return name;
	}

	public String getPath() {
		return path;
	}

	public boolean isAtArmA3InstallRoot() {
		return atArmA3InstallRoot;
	}

	public void setAtArmA3InstallRoot(boolean atArmA3InstallRoot) {
		this.atArmA3InstallRoot = atArmA3InstallRoot;
	}
}
