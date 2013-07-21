package fr.soe.a3s.dto;


public class AutoConfigDTO {

	private String repositoryName;
	private FtpDTO ftpDTO;
	
	public String getRepositoryName() {
		return repositoryName;
	}
	public void setRepositoryName(String repositoryName) {
		this.repositoryName = repositoryName;
	}
	public FtpDTO getFtpDTO() {
		return ftpDTO;
	}
	public void setFtpDTO(FtpDTO ftpDTO) {
		this.ftpDTO = ftpDTO;
	}

}
