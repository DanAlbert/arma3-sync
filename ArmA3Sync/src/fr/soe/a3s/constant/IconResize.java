package fr.soe.a3s.constant;

public enum IconResize {

	AUTO("Default", 0), SIZE1("x1.25", 1.25), SIZE2("x1.5", 1.5), SIZE3(
			"x1.75", 1.75), SIZE4("x2", 2), NONE("Disabled", 1);

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
		} else if (iconResize.equals(SIZE3.getDescription())) {
			return SIZE3;
		} else if (iconResize.equals(SIZE4.getDescription())) {
			return SIZE4;
		} else if (iconResize.equals(AUTO.getDescription())) {
			return AUTO;
		} else {
			return null;
		}
	}
}
