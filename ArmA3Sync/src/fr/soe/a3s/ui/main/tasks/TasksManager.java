package fr.soe.a3s.ui.main.tasks;

import java.util.Timer;
import java.util.TimerTask;

public class TasksManager {

	private static TasksManager instance;
	private Timer timer;

	private TasksManager() {
		timer = new Timer();
	}

	public static TasksManager getInstance() {
		if (instance == null) {
			instance = new TasksManager();
		}
		return instance;
	}

	public void addTask(TimerTask task, long delay) {
		timer.schedule(task, delay);
	}

	public void addTask(TimerTask task, long delay, long period) {
		timer.schedule(task, delay, period);
	}

	public void reset() {
		timer.cancel();
		timer = new Timer();
	}
}
