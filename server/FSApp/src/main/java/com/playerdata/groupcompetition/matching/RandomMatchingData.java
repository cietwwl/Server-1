package com.playerdata.groupcompetition.matching;

import java.util.ArrayList;
import java.util.List;

class RandomMatchingData {

	private String userId;
	private List<String> heroIds;
	private boolean cancel;
	
	public RandomMatchingData(String pUserId, List<String> pHeroIds) {
		this.userId = pUserId;
		this.heroIds = new ArrayList<String>(pHeroIds);
	}
	public String getUserId() {
		return userId;
	}
	
	public List<String> getHeroIds() {
		return heroIds;
	}
	
	public void setHeroIds(List<String> list) {
		this.heroIds = new ArrayList<String>(list);
	}
	
	public void setCancel(boolean value) {
		this.cancel = true;
	}
	public boolean isCancel() {
		return cancel;
	}
}
