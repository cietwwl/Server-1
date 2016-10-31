package com.rw.fsutil.dao.cache;


public class UpdateTimeStamp implements Comparable<UpdateTimeStamp> {

	private final long seqId;
	private final long executeTimeMillis;

	public UpdateTimeStamp(long seqId, long executeTimeMillis) {
		super();
		this.seqId = seqId;
		this.executeTimeMillis = executeTimeMillis;
	}

	@Override
	public int compareTo(UpdateTimeStamp o) {
		long otherTimeMillis = o.executeTimeMillis;
		if (executeTimeMillis < otherTimeMillis) {
			return -1;
		}
		if (executeTimeMillis > otherTimeMillis) {
			return 1;
		}
		if (seqId < o.seqId) {
			return -1;
		} else if (seqId > o.seqId) {
			return 1;
		} else {
			return 0;
		}
	}
	
}
