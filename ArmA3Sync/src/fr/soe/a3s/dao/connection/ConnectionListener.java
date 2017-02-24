package fr.soe.a3s.dao.connection;

import fr.soe.a3s.controller.ObserverConnectionLost;
import fr.soe.a3s.controller.ObserverProceed;
import fr.soe.a3s.domain.AbstractProtocole;

public class ConnectionListener extends Thread {

	private long startTime;
	private ObserverProceed observer;
	private boolean canceled;

	public ConnectionListener(long startTime) {
		this.startTime = startTime;
	}

	@Override
	public void run() {
		canceled = false;
		while (!canceled) {
			try {
				this.sleep(1000);
				long endTime = System.nanoTime();
				long deltaTime = endTime - startTime;
				boolean lost = deltaTime > (Math.pow(10, 9) * 30);// 30 s
				if (lost) {
					canceled = true;
					observer.proceed();
				}
			} catch (InterruptedException e) {
			}
		}
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public void cancel() {
		this.canceled = true;
	}

	public void addObserverProceed(ObserverProceed obs) {
		this.observer = obs;
	}
}
