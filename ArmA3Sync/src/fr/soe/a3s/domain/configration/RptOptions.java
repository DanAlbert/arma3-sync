package fr.soe.a3s.domain.configration;

import java.io.Serializable;

public class RptOptions implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7740252023809740200L;
	String rptPath;

	public String getRptPath() {
		return rptPath;
	}

	public void setRptPath(String rptPath) {
		this.rptPath = rptPath;
	}

}
