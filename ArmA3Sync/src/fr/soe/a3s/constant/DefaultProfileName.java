package fr.soe.a3s.constant;

public enum DefaultProfileName {

	DEFAULT("Default");
	
	private String description;
	
	private DefaultProfileName(String description){
		this.description = description;
	}
	
	public String getDescription(){
		return this.description;
	}
}
