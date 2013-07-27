package fr.soe.a3s.domain.configration;

import java.io.Serializable;

public class AcreOptions implements Serializable{

	private String ts3Path;

	public String getTs3Path() {
		return ts3Path;
	}

	public void setTs3Path(String ts3Path) {
		this.ts3Path = ts3Path;
	}
}
