package com.playerdata.activityCommon.activityType;

public interface IndexRankJudgeIF {
	/**
	 * 判断一个id是否是该类活动的合法id
	 * (每类活动都有一个id取值范围)
	 * @param index
	 * @return
	 */
	public boolean isThisActivityIndex(int index);
}
