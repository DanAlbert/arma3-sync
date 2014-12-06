package fr.soe.a3s.service;

public class Semaphore {

	private int val = 1;

	public boolean acquire() {
		if (val == 1) {
			val = 0;
			return true;
		} else {
			return false;
		}
	}

}
