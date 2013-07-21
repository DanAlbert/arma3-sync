package fr.soe.a3s.constant;

public enum MinimizationType {
	NOTHING("Do nothing"),TASK_BAR("Reduce to task bar"),TRAY("Reduce to tray");
	
	private String description;
	
	private MinimizationType(String description){
		this.description = description;
	}
	
	public String getDescription(){
		return this.description;
	}
	
	public static MinimizationType getEnum(String designation){
		if (designation.equals("Do nothing")){
			return NOTHING;
		}else if (designation.equals("Reduce to task bar")){
			return TASK_BAR;
		}else {
			return TRAY;
		}
	}
}
