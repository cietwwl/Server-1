package com.bm.rank.consume;

public class ConsumeComparable implements Comparable<ConsumeComparable> {

	private int consume;
	
	private long time;

	@Override
	public int compareTo(ConsumeComparable o) {
		return consume - o.consume;
	}

	public int getConsume() {
		return consume;
	}

	public void setConsume(int consume) {
		this.consume = consume;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}
}
