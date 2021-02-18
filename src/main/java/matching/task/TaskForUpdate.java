package matching.task;

import matching.Runner;
import matching.annoy.Annoy;

public class TaskForUpdate implements Runnable {

	private static volatile boolean stop = false;
	private final static int  TOHOUR =1_000; // 60 * 60 * 1_000;
	private final static int UPDATERATE = Runner.UPDATERATE * TOHOUR;

	Annoy annoy;

	public TaskForUpdate(Annoy annIndex) {
		this.annoy = annIndex;
	}

	public void run() {
		while (!stop) {
			try {
				Thread.sleep(UPDATERATE);
			} catch (InterruptedException e) {
			}
			update();
		}
	}

	// update annoy index
	private void update() {
		annoy.update();
	}

	public void stop() {
		stop = true;
	}
}
