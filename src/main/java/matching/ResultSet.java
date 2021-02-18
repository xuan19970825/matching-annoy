package matching;

import matching.task.JobType;

// every resultSet will be use for statistic
public class ResultSet {
	private JobType jobType;
	private long startTime; // in ns
	private long endTime; // in ns

	public ResultSet(JobType type, long startTime, long endTime) {
		this.jobType = type;
		this.startTime = startTime;
		this.endTime = endTime;
	}

	public JobType getType() {
		return jobType;
	}

	public long getStartTime() {
		return startTime;
	}

	public long getEndTime() {
		return endTime;
	}
}
