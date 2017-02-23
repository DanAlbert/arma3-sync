package fr.soe.a3s.constant;

public enum StartWithOS {

	ENABLED("Enabled"), DISABLED("Disabled");

	private String description;

	private StartWithOS(String description) {
		this.description = description;
	}

	public String getDescription() {
		return this.description;
	}

	public static StartWithOS getEnum(String designation) {
		if (designation.equals(ENABLED.getDescription())) {
			return ENABLED;
		} else {
			return DISABLED;
		}
	}
}
