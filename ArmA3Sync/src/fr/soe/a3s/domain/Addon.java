package fr.soe.a3s.domain;

public class Addon {

	private String name;
	private String path;
	private boolean atArmA3InstallRoot;
	
	public Addon(String name, String path) {
		this.name=name;
		this.path=path;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public boolean isAtArmA3InstallRoot() {
		return atArmA3InstallRoot;
	}

	public void setAtArmA3InstallRoot(boolean atArmA3InstallRoot) {
		this.atArmA3InstallRoot = atArmA3InstallRoot;
	}

}
