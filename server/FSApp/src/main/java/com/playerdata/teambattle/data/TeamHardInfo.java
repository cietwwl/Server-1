package com.playerdata.teambattle.data;

import com.playerdata.dataSyn.annotation.SynClass;

@SynClass
public class TeamHardInfo {
	private String hardID;
	
	private int finishTimes;
	
	private int buyTimes;

	public String getHardID() {
		return hardID;
	}

	public void setHardID(String hardID) {
		this.hardID = hardID;
	}

	public int getFinishTimes() {
		return finishTimes;
	}

	public void setFinishTimes(int finishTimes) {
		this.finishTimes = finishTimes;
	}

	public int getBuyTimes() {
		return buyTimes;
	}

	public void setBuyTimes(int buyTimes) {
		this.buyTimes = buyTimes;
	}
}
