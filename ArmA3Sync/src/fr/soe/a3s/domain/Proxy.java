package fr.soe.a3s.domain;

import java.io.Serializable;

public class Proxy implements Serializable {

	private static final long serialVersionUID = -50L;
	private boolean proxyEnabled = false;
	private AbstractProtocole proxyProtocol;// may be null

	public boolean isProxyEnabled() {
		return proxyEnabled;
	}

	public void setProxyEnabled(boolean proxyEnabled) {
		this.proxyEnabled = proxyEnabled;
	}

	public AbstractProtocole getProxyProtocol() {
		return proxyProtocol;
	}

	public void setProxyProtocol(AbstractProtocole proxyProtocol) {
		this.proxyProtocol = proxyProtocol;
	}
}
