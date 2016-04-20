package fr.soe.a3s.constant;

public enum TimeOutValues {

	CONNECTION_TIME_OUT(300000), READ_TIME_OUT(300000);

	private int value;

	private TimeOutValues(int value) {
		this.value = value;
	}

	public int getValue() {
		return this.value;
	}
}
