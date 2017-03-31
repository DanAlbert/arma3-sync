package fr.soe.a3s.main;

public class Version {

	private static final String NAME = "1.6 Beta 5";

	private static final int MAJOR = 1;

	private static final int MINOR = 6;

	private static final int BUILD = 89;

	private static final String YEAR = "2013-2017";

	public static String getVersion() {
		return MAJOR + "." + MINOR + "." + BUILD;
	}

	public static String getName() {
		return NAME;
	}

	public static int getBuild() {
		return BUILD;
	}

	public static String getYear() {
		return YEAR;
	}
}
