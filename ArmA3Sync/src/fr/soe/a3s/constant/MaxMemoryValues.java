package fr.soe.a3s.constant;

public enum MaxMemoryValues {

	MIN(768),MEDIUM(1024),MAX(2047);

	private int value;
	
	private MaxMemoryValues(int value){
		this.value = value;
	}
	
	public int getValue(){
		return this.value;
	}

}
