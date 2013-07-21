package fr.soe.a3s.domain.repository;

import java.io.Serializable;

import fr.soe.a3s.domain.AbstractProtocole;

public class AutoConfig implements Serializable{
	
	private String repositoryName;
	private AbstractProtocole protocole;
	
	public String getRepositoryName() {
		return repositoryName;
	}
	public void setRepositoryName(String repositoryName) {
		this.repositoryName = repositoryName;
	}
	public AbstractProtocole getProtocole() {
		return protocole;
	}
	public void setProtocole(AbstractProtocole protocole) {
		this.protocole = protocole;
	}

}
