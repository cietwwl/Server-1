package com.bm.worldBoss.rank;

import java.util.ArrayList;
import java.util.List;

public class WBRankInfo {

	private int userRank;
	
	private List<WBHurtItem> rankList = new ArrayList<WBHurtItem>();

	public int getUserRank() {
		return userRank;
	}

	public void setUserRank(int userRank) {
		this.userRank = userRank;
	}

	public List<WBHurtItem> getRankList() {
		return rankList;
	}

	public void setRankList(List<WBHurtItem> rankList) {
		this.rankList = rankList;
	}
	
	
	
	
}
