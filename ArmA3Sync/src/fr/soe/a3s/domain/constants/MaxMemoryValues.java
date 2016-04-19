package fr.soe.a3s.domain.constants;

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
