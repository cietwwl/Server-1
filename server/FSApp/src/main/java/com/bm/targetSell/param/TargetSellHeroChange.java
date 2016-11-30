package com.bm.targetSell.param;

import java.util.ArrayList;
import java.util.List;
import com.bm.targetSell.param.attrs.EAchieveType;
import com.rw.fsutil.util.DateUtils;

public class TargetSellHeroChange {

	private final List<EAchieveType> changeList;
	private final long createTime;

	public TargetSellHeroChange() {
		this.changeList = new ArrayList<EAchieveType>(5);
		this.createTime = DateUtils.getSecondLevelMillis();
	}

	public List<EAchieveType> getChangeList() {
		return changeList;
	}

	public long getCreateTime() {
		return createTime;
	}
}
