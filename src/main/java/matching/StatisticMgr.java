package matching;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import matching.task.JobType;

public class StatisticMgr {

	// every user threads will add result in this
	private static List<ResultSet> resultSets = new ArrayList<ResultSet>();

	// use for classify the result set and do statistic
	private static Map<String, ArrayList<Long>> history = new TreeMap<String, ArrayList<Long>>();
	// use for record minimum start time of each operation
	private static Map<String, Long> minStart = new TreeMap<String, Long>();
	// use for record maximum end time of each operation
	private static Map<String, Long> maxEnd = new TreeMap<String, Long>();

	private static long median;
	private static double mean;
	private static double var;
	private static long lowerQ, upperQ;

	public synchronized static void addResult(ResultSet rs) {
		resultSets.add(rs);
	}

	public static void print() {
		// do classify
		for (int i = 0; i < resultSets.size(); i++) {
			StatisticMgr.addHistory(resultSets.get(i));
		}
		System.out.println("\t throughput, latency, variance, min, max, median");
		makeStatString("SEARCH", history.get("SEARCH"), maxEnd.get("SEARCH"), minStart.get("SEARCH"));
//		makeStatString("INSERT", history.get("INSERT"), maxEnd.get("INSERT"), minStart.get("INSERT"));
//		makeStatString("UPDATE", history.get("UPDATE"), maxEnd.get("UPDATE"), minStart.get("UPDATE"));
//		makeStatString("DELETE", history.get("DELETE"), maxEnd.get("DELETE"), minStart.get("DELETE"));
	}

	public static void writeCsv() {
		try (FileWriter writer = new FileWriter(
				String.format("user%d_total%d_%dnn.csv", Runner.userNumber, Runner.listLength, Runner.nn))) {
			writer.append("UserNumber,ListLength,Job,Latency,nn\n");
			for (int i = 0; i < JobType.values().length; i++) {
				String jobType = JobType.values()[i].toString();
				ArrayList<Long> typeHistory = history.get(jobType);
				if (typeHistory == null)
					continue;
				for (int j = 0; j < typeHistory.size(); j++)
					writer.append(
							String.format("%d,%s,%d,%d\n", Runner.userNumber, jobType, typeHistory.get(j), Runner.nn));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// classify the result set and calculate the run time
	private static void addHistory(ResultSet rs) {
		String type = rs.getType().toString();
		ArrayList<Long> typeHistory = history.get(type);
		Long typeMinStart = minStart.get(type);
		Long typeMaxEnd = maxEnd.get(type);
		if (typeHistory == null) {
			typeHistory = new ArrayList<Long>();
			history.put(type, typeHistory);
		}
		if (typeMaxEnd == null) {
			typeMaxEnd = 0l;
			maxEnd.put(type, typeMaxEnd);
		}
		if (typeMinStart == null) {
			typeMinStart = Long.MAX_VALUE;
			minStart.put(type, typeMinStart);
		}
		typeHistory.add(TimeUnit.MILLISECONDS.convert(rs.getEndTime() - rs.getStartTime(), TimeUnit.NANOSECONDS));
		if (typeMaxEnd < rs.getEndTime()) {
			typeMaxEnd = rs.getEndTime();
			maxEnd.replace(type, typeMaxEnd);
		}
		if (typeMinStart > rs.getStartTime()) {
			typeMinStart = rs.getStartTime();
			minStart.replace(type, typeMinStart);
		}
	}

	// print
	private static void makeStatString(ResultSet rs) {
		if (rs.getType() == JobType.SEARCH)
			System.out.println(String.format("Search : %d (ms)",
					TimeUnit.MILLISECONDS.convert(rs.getEndTime() - rs.getStartTime(), TimeUnit.NANOSECONDS)));
		else if (rs.getType() == JobType.INSERT)
			System.out.println(String.format("Insert : %d (ms)",
					TimeUnit.MILLISECONDS.convert(rs.getEndTime() - rs.getStartTime(), TimeUnit.NANOSECONDS)));
		else if (rs.getType() == JobType.UPDATE)
			System.out.println(String.format("Update : %d (ms)",
					TimeUnit.MILLISECONDS.convert(rs.getEndTime() - rs.getStartTime(), TimeUnit.NANOSECONDS)));
		else if (rs.getType() == JobType.DELETE)
			System.out.println(String.format("Delete : %d (ms)",
					TimeUnit.MILLISECONDS.convert(rs.getEndTime() - rs.getStartTime(), TimeUnit.NANOSECONDS)));
	}

	// do statistic and print for certain operation (search/add/delete)
	private static void makeStatString(String key, List<Long> typeHistory, Long maxEndTime, Long minStartTime) {
		if (typeHistory != null) {
			int throughput = typeHistory.size(); // get finished task number

			Collections.sort(typeHistory);

			// Transfer it to unmodifiable in order to prevent modification
			// when we use a sublist to access it.
			typeHistory = Collections.unmodifiableList(typeHistory);

			calcMean(typeHistory);
			calcVar(typeHistory);
//			calcQ(typeHistory);
			calcMedian(typeHistory);

			Long min = Collections.min(typeHistory);
			Long max = Collections.max(typeHistory);

			long spendTime = TimeUnit.SECONDS.convert(maxEndTime - minStartTime, TimeUnit.NANOSECONDS);
			throughput = (int) (throughput / spendTime);
			System.out.println(String.format("%s\t: %d, %f, %f, %d, %d, %d", key, throughput, StatisticMgr.mean,
					Math.sqrt(StatisticMgr.var), min, max, StatisticMgr.median));
		} else {
			System.out.println(String.format("%s\t: %d, %d, %d, %d, %d, %d",key, 0, 0, 0, 0, 0, 0));
		}

	}

	private static void calcMedian(List<Long> timeSlot) {
		int count = timeSlot.size();
		Long median;
		if (count % 2 != 0) // Odd
			median = timeSlot.get((count - 1) / 2);
		else {// Even
			long front = timeSlot.get(count / 2 - 1);
			long back = timeSlot.get(count / 2);
			median = (front + back) / 2;
		}
		StatisticMgr.median = median;
	}

	private static void calcMean(List<Long> timeSlot) {
		double mean = 0;
		double count = timeSlot.size();
		for (Long lat : timeSlot)
			mean += (lat / count);
		StatisticMgr.mean = mean;
	}

	private static void calcQ(List<Long> timeSlot) {

		int count = timeSlot.size(); // get total task number
		int middleOffset = timeSlot.size() / 2;

		calcMedian(timeSlot);
		if (count < 2) { // Boundary case: there is only one number in the list
			lowerQ = StatisticMgr.median;
			upperQ = StatisticMgr.median;
		} else if (count % 2 == 0) { // Even
			calcMedian(timeSlot.subList(0, middleOffset));
			lowerQ = StatisticMgr.median;
			calcMedian(timeSlot.subList(middleOffset, count));
			upperQ = StatisticMgr.median;
		} else { // Odd
			calcMedian(timeSlot.subList(0, middleOffset));
			lowerQ = StatisticMgr.median;
			calcMedian(timeSlot.subList(middleOffset + 1, count));
			upperQ = StatisticMgr.median;
		}

	}

	private static void calcVar(List<Long> timeSlot) {
		double mean = StatisticMgr.mean;
		double temp = 0;
		int count = timeSlot.size();
		for (Long lat : timeSlot)
			temp += (lat - mean) * (lat - mean);
		StatisticMgr.var = temp / count;
	}
}
