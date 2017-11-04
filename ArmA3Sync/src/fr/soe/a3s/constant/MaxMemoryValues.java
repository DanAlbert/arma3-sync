package fr.soe.a3s.constant;

public enum MaxMemoryValues {

	MIN(1024), MAX32(2047), MAX64(3071);

	private int value;

	private MaxMemoryValues(int value) {
		this.value = value;
	}

	public int getValue() {
		return this.value;
	}

}
