package com.playerdata.activity.timeCardType.data;

import javax.persistence.Id;

import com.playerdata.dataSyn.annotation.SynClass;


@SynClass
public class FriendMonthCardInfo {

	@Id
	private String id;

	private boolean isMonthCardMax;
	
	private boolean isEternalCardMax;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isMonthCardMax() {
		return isMonthCardMax;
	}

	public void setMonthCardMax(boolean isMonthCardMax) {
		this.isMonthCardMax = isMonthCardMax;
	}

	public boolean isEternalCardMax() {
		return isEternalCardMax;
	}

	public void setEternalCardMax(boolean isEternalCardMax) {
		this.isEternalCardMax = isEternalCardMax;
	}
}
