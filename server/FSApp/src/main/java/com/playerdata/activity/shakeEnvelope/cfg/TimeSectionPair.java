package com.playerdata.activity.shakeEnvelope.cfg;

/**
 * 每天的时间片
 * 取的是基于当天开始0点的相对时间
 * @author aken
 *
 */
public class TimeSectionPair {
	
	private long startTime;
	
	private long endTime;

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}
}
