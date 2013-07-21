package fr.soe.a3s.domain.configration;

import java.io.Serializable;

public class AiAOptions implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2248371007390169026L;
	private String arma2Path;
	private String armaPath;
	private String tohPath;
	private String arma2OAPath;
	private String allinArmaPath;

	public String getArma2Path() {
		return arma2Path;
	}

	public void setArma2Path(String arma2Path) {
		this.arma2Path = arma2Path;
	}

	public String getArmaPath() {
		return armaPath;
	}

	public void setArmaPath(String armaPath) {
		this.armaPath = armaPath;
	}

	public String getTohPath() {
		return tohPath;
	}

	public void setTohPath(String tohPath) {
		this.tohPath = tohPath;
	}

	public String getArma2OAPath() {
		return arma2OAPath;
	}

	public void setArma2OAPath(String arma2oaPath) {
		arma2OAPath = arma2oaPath;
	}

	public String getAllinArmaPath() {
		return allinArmaPath;
	}

	public void setAllinArmaPath(String allinArmaPath) {
		this.allinArmaPath = allinArmaPath;
	}

}
