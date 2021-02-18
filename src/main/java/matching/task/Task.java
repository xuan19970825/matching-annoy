package matching.task;

import java.util.List;

import matching.Generator;
import matching.ResultSet;
import matching.Runner;
import matching.StatisticMgr;
import matching.annoy.Annoy;
import matching.list.Container;

public class Task implements Runnable {

	final static int NS_TO_MS = Runner.NS_TO_MS;
	Container list;
	Annoy annIndex;
	private volatile boolean stop = false;
	private volatile boolean isWarmingUp = true;

	public Task(Annoy annIndex) {
		this.annIndex = annIndex;
	}

	public void run() {

		while (!stop) {
			float[] target = Generator.genVector();
			JobType type = JobType.SEARCH;
			long startTime = System.nanoTime();
			job(type, target);
			long endTime = System.nanoTime();

			// new result set and add it into staticticMgr
			if (!isWarmingUp) {
				ResultSet rs = new ResultSet(type, startTime, endTime);
				StatisticMgr.addResult(rs);
			}
		}
	}

	// warming information
	public void startStatistic() {
		isWarmingUp = false;
	}

	private void job(JobType type, float[] target) {
		switch (type) {
		case SEARCH:
			search(target);
			break;
		case INSERT:
			insert(target);
			break;
		case UPDATE:
			update(target);
			break;
		case DELETE:
			delete(target);
			break;
		default:
			throw new UnsupportedOperationException();
		}
	}

	private List<Integer> search(float[] target) {
//		System.out.println(annIndex.getNearest(target, Runner.nn).get(0));
		return annIndex.search(target);
	}

	private boolean insert(float[] target) {
		return list.add(target);
	}
	
	private boolean update(float[] newVector) {
		return list.modify(0, newVector);
	}

	private boolean delete(float[] target) {
		return list.delete(target);
	}

	public void stop() {
		stop = true;
	}
}