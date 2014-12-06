package fr.soe.a3s.domain.configration;

import java.io.Serializable;

public class AcreOptions implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1596307340587244621L;
	private String ts3Path;
	private String acrePluginPath;
	private String acreUserconfigPath;

	public String getTs3Path() {
		return ts3Path;
	}

	public void setTs3Path(String ts3Path) {
		this.ts3Path = ts3Path;
	}

	public String getAcrePluginPath() {
		return acrePluginPath;
	}

	public void setAcrePluginPath(String acrePluginPath) {
		this.acrePluginPath = acrePluginPath;
	}

	public String getAcreUserconfigPath() {
		return acreUserconfigPath;
	}

	public void setAcreUserconfigPath(String acreUserconfigPath) {
		this.acreUserconfigPath = acreUserconfigPath;
	}
}
