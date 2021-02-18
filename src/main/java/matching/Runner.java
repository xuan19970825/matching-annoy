package matching;

import java.io.IOException;

import matching.annoy.Annoy;
import matching.annoy.IndexType;
import matching.task.TaskMgr;

public class Runner {
	public static final int VEC_DIM = 300;
	public static final int NS_TO_MS = 1_000_000;
	public static final int WARMUPTIME = 10_000;
	public static final int EXECUTE_TIME = 10_000; // should be 60_000 millisecond
	public static final int UPDATERATE = 4; // update index per 4 hours

	public static int userNumber;
	public static int listLength; // should be 1_000_000, in ec2 use 20_000
	public static int nn;// finding nn number of nearest points

	public static IndexType indexType = IndexType.DOT;

	public static void main(String[] args) throws IOException {

		// inputs
		String indexPath = args[0];
		userNumber = Integer.parseInt(args[1]);
		listLength = Integer.parseInt(args[2]);
		nn = Integer.parseInt(args[3]);

		Annoy annIndex = new Annoy(indexPath, indexType);

		// create user threads
		TaskMgr taskMgr = new TaskMgr(userNumber, annIndex);

		// start
		taskMgr.run();

		// do statistic and print out
		StatisticMgr.print();

//		StatisticMgr.writeCsv();

	}
}
