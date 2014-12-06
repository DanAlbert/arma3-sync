package fr.soe.a3s.domain.configration;

import java.io.Serializable;

public class TfarOptions implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9099384809608223793L;
	private String ts3Path;
	private String tfarPluginPath;
	private String tfarUserconfigPath;

	public String getTs3Path() {
		return ts3Path;
	}

	public void setTs3Path(String ts3Path) {
		this.ts3Path = ts3Path;
	}

	public String getTfarPluginPath() {
		return tfarPluginPath;
	}

	public void setTfarPluginPath(String tfarPluginPath) {
		this.tfarPluginPath = tfarPluginPath;
	}

	public String getTfarUserconfigPath() {
		return tfarUserconfigPath;
	}

	public void setTfarUserconfigPath(String tfarUserconfigPath) {
		this.tfarUserconfigPath = tfarUserconfigPath;
	}

}
