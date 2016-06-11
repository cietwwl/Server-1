package com.bm.rank.charge;

public class ChargeComparable implements Comparable<ChargeComparable> {

	private int charge;

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

	
	

}
