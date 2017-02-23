package fr.soe.a3s.constant;

public enum CheckRepositoriesFrequency {

	DISABLED("Disabled", 0), FREQ1("Every 15 minutes", 15), FREQ2(
			"Every 30 minutes", 30), FREQ3("Every 60 minutes", 60);

	private String description;

	private int frequency;

	private CheckRepositoriesFrequency(String description, int frequency) {
		this.description = description;
		this.frequency = frequency;
	}

	public String getDescription() {
		return this.description;
	}

	public int getFrequency() {
		return this.frequency;
	}

	public static CheckRepositoriesFrequency getEnum(String description) {

		if (description.equals(DISABLED.getDescription())) {
			return DISABLED;
		} else if (description.equals(FREQ1.getDescription())) {
			return FREQ1;
		} else if (description.equals(FREQ2.getDescription())) {
			return FREQ2;
		} else if (description.equals(FREQ3.getDescription())) {
			return FREQ3;
		}
		return null;
	}
}
