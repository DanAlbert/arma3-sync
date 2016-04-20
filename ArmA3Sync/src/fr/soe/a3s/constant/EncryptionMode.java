package fr.soe.a3s.constant;

public enum EncryptionMode {

	NO_ENCRYPTION("No encryption"), EXPLICIT_SSL("SSL explicit encryption"), IMPLICIT_SSL(
			"SSL implicit encryption");

	private String description;

	private EncryptionMode(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public static EncryptionMode getEnum(String description) {
		if (description.equals("No encryption")) {
			return NO_ENCRYPTION;
		} else if (description.equals("SSL explicit encryption")) {
			return EXPLICIT_SSL;
		} else {
			return IMPLICIT_SSL;
		}
	}
}
