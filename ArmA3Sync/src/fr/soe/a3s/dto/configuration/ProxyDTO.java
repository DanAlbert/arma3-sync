package fr.soe.a3s.dto.configuration;

import fr.soe.a3s.dto.ProtocolDTO;

public class ProxyDTO {

	private ProtocolDTO protocolDTO;

	private boolean enableProxy;

	public ProtocolDTO getProtocolDTO() {
		return protocolDTO;
	}

	public void setProtocolDTO(ProtocolDTO protocolDTO) {
		this.protocolDTO = protocolDTO;
	}

	public boolean isEnableProxy() {
		return enableProxy;
	}

	public void setEnableProxy(boolean enableProxy) {
		this.enableProxy = enableProxy;
	}
}
