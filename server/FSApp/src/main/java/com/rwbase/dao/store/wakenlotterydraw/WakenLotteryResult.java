package com.rwbase.dao.store.wakenlotterydraw;

import java.util.HashMap;

public class WakenLotteryResult {
	HashMap<Integer, Integer> rewardMap = new HashMap<Integer, Integer>();
	private boolean isGuarantee;
	public HashMap<Integer, Integer> getRewardMap() {
		return rewardMap;
	}
	public void setRewardMap(HashMap<Integer, Integer> rewardMap) {
		this.rewardMap = rewardMap;
	}
	public boolean isGuarantee() {
		return isGuarantee;
	}
	public void setGuarantee(boolean isGuarantee) {
		this.isGuarantee = isGuarantee;
	}
	
	
}
