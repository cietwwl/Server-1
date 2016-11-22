package com.bm.rank.recharge;

public class ChargeComparable implements Comparable<ChargeComparable> {

	private int charge;
	
	private long time;

	@Override
	public int compareTo(ChargeComparable o) {
		return charge - o.charge;
	}

	public int getCharge() {
		return charge;
	}

	public void setCharge(int charge) {
		this.charge = charge;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}
}
