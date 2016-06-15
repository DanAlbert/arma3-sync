package fr.soe.a3s.domain.configration;

import java.io.Serializable;

import fr.soe.a3s.domain.AbstractProtocole;

public class Proxy implements Serializable {

	private AbstractProtocole proxyProtocol;

	private boolean enableProxy;

	public AbstractProtocole getProxyProtocol() {
		return proxyProtocol;
	}

	public void setProxyProtocol(AbstractProtocole proxyProtocol) {
		this.proxyProtocol = proxyProtocol;
	}

	public boolean isEnableProxy() {
		return enableProxy;
	}

	public void setEnableProxy(boolean enableProxy) {
		this.enableProxy = enableProxy;
	}
}
