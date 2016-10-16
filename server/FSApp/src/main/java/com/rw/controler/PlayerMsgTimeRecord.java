package com.rw.controler;

public class PlayerMsgTimeRecord {

	private final Integer seqId;
	private final long timeMillis;

	public PlayerMsgTimeRecord(Integer seqId, long timeMillis) {
		super();
		this.seqId = seqId;
		this.timeMillis = timeMillis;
	}

	public Integer getSeqId() {
		return seqId;
	}

	public long getTimeMillis() {
		return timeMillis;
	}

}
