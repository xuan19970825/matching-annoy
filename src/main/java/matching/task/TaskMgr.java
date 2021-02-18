package matching.task;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import matching.Runner;
import matching.annoy.Annoy;

public class TaskMgr {

	private ExecutorService threadPool;
	private Task[] tasks;
	private TaskForUpdate updateTask;

	public TaskMgr(int userNumber, Annoy annIndex) {
		threadPool = Executors.newFixedThreadPool(userNumber + 1);

		tasks = new Task[userNumber];
		updateTask = new TaskForUpdate(annIndex);

		for (int i = 0; i < tasks.length; i++)
			tasks[i] = new Task(annIndex);
	}

	public void run() {

		runTask();// start all threads
		warmup();// warm up
		startStatistic();// start record after warm up

		try {
			Thread.sleep(Runner.EXECUTE_TIME);
		} catch (InterruptedException e) {
		}

		shutdown();// stop all threads
	}

	// start all threads
	private void runTask() {
		for (int i = 0; i < tasks.length; i++)
			threadPool.execute(tasks[i]);
		threadPool.execute(updateTask);
	}

	// start record after warm up
	private void warmup() {
		try {
			Thread.sleep(Runner.WARMUPTIME);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void startStatistic() {
		for (int i = 0; i < tasks.length; i++)
			tasks[i].startStatistic();
	}

	// stop all threads
	private void shutdown() {
		for (int i = 0; i < tasks.length; i++) {
			tasks[i].stop();
		}
		updateTask.stop();
		threadPool.shutdown();
	}
}