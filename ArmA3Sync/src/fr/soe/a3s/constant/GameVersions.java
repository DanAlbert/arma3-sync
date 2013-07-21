package fr.soe.a3s.constant;

public enum GameVersions {

	ARMA3("ArmA III"),ARMA3_AIA("ArmA III - AiA");

	private String description;
	
	private GameVersions(String description){
		this.description = description;
	}
	
	public String getDescription(){
		return this.description;
	}
	
}
