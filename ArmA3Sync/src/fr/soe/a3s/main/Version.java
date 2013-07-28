package fr.soe.a3s.main;

public class Version {
	
	private static final String NAME = "Alpha";
	
	private static final int MAJOR = 0;
	
	private static final int MINOR = 0;
	
	private static final int BUILD = 17;
	
	private static final String YEAR = "2013";
	
	public static String getVersion(){
		return MAJOR + "." + MINOR + "." + BUILD;
	}
	public static String getName(){
		return NAME;
	}
	public static int getBuild(){
		return BUILD;
	}
	public static String getYear(){
		return YEAR;
	}
}
