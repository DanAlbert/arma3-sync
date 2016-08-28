package fr.soe.a3s.constant;

public enum IconResize {

	NONE("Disabled", 1), SIZE1("x1.5", 1.5), SIZE2("x2", 2), AUTO("Automatic",
			0);

	private String description;

	private double value;

	private IconResize(String description, double value) {
		this.description = description;
		this.value = value;
	}

	public String getDescription() {
		return description;
	}

	public double getValue() {
		return value;
	}

	public static IconResize getEnum(String iconResize) {

		if (iconResize.equals(NONE.getDescription())) {
			return NONE;
		} else if (iconResize.equals(SIZE1.getDescription())) {
			return SIZE1;
		} else if (iconResize.equals(SIZE2.getDescription())) {
			return SIZE2;
		} else if (iconResize.equals(AUTO.getDescription())) {
			return AUTO;
		} else {
			return null;
		}
	}
}
