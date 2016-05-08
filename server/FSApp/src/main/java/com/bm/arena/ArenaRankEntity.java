package com.bm.arena;

import java.util.List;

import com.common.HPCUtil;
import com.rwbase.dao.copy.pojo.ItemInfo;

public class ArenaRankEntity {

	private final int key;
	private final int rank;
	private final List<ItemInfo> rewardList;

	public ArenaRankEntity(int key, int rank, String text) {
		this.key = key;
		this.rank = rank;
		this.rewardList = HPCUtil.createItemInfo(text);
	}

	public int getKey() {
		return key;
	}

	public int getRank() {
		return rank;
	}

	public List<ItemInfo> getRewardList() {
		return rewardList;
	}
}
