package fr.soe.a3s.domain.configration;

import java.io.Serializable;

public class Acre2Options implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8995842831822253145L;
	private String ts3Path;
	private String acre2PluginPath;

	public String getTs3Path() {
		return ts3Path;
	}

	public void setTs3Path(String ts3Path) {
		this.ts3Path = ts3Path;
	}

	public String getAcre2PluginPath() {
		return acre2PluginPath;
	}

	public void setAcre2PluginPath(String acre2PluginPath) {
		this.acre2PluginPath = acre2PluginPath;
	}
}
