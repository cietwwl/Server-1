package com.rw.fsutil.dao.optimize;

public class UpdateCountStat {

	public UpdateCountStat(String tableName, String name) {
		this.name = name;
		this.tableName = tableName;
	}

	private final String tableName;
	final String name;
	private int successCount;
	private int failCount;
	private int exceptionCount;
	private int recordCount;
	private int totalCost;
	private int max;

	public String getName() {
		return name;
	}

	public void incFail() {
		this.failCount++;
	}

	public void incException() {
		this.exceptionCount++;
	}

	public void incSuccess(long cost, int count) {
		int cost_ = (int) cost;
		recordCount += count;
		if (cost_ > max) {
			max = cost_;
		}
		totalCost += cost_;
		successCount++;
	}

	public int getSuccessCount() {
		return successCount;
	}

	public int getFailCount() {
		return failCount;
	}

	public int getExceptionCount() {
		return exceptionCount;
	}

	public int getRecordCount() {
		return recordCount;
	}

	public String getTableName() {
		return tableName;
	}

	public void setRecordCount(int recordCount) {
		this.recordCount = recordCount;
	}

	public int getTotalCost() {
		return totalCost;
	}

	public void setTotalCost(int totalCost) {
		this.totalCost = totalCost;
	}

	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		this.max = max;
	}

	public void setSuccessCount(int successCount) {
		this.successCount = successCount;
	}

	public void setFailCount(int failCount) {
		this.failCount = failCount;
	}

	public void setExceptionCount(int exceptionCount) {
		this.exceptionCount = exceptionCount;
	}

	@Override
	public String toString() {
		return "[" + tableName + ", successCount=" + successCount + ", failCount=" + failCount + ", exceptionCount=" + exceptionCount + ", recordCount=" + recordCount + ", totalCost=" + totalCost
				+ ", max=" + max + "]";
	}

}
