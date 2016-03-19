package com.rwbase.dao.arena.pojo;

import com.rwbase.common.enu.eSpecialItemId;

public class PrizeInfo {

	private eSpecialItemId type;
	private float count;

	public eSpecialItemId getType() {
		return type;
	}

	public void setType(eSpecialItemId type) {
		this.type = type;
	}

	public float getCount() {
		return count;
	}

	public void setCount(float count) {
		this.count = count;
	}

}
