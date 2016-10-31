package com.rw.service.PeakArena.datamodel;

import com.common.BaseConfig;

public class PeakArenaInfo extends BaseConfig {
	private int key; // 关键字段
	private int count; // 挑战次数
	private int cdTime; // CD时间(单位是秒)
	private int prizeAccLimit; // 累积奖励上限
	private int winScore; // 胜利的积分
	private int failScore; // 失败的积分

	public int getKey() {
		return key;
	}

	public int getCount() {
		return count;
	}

	public int getCdTime() {
		return cdTime;
	}

	public int getPrizeAccLimit() {
		return prizeAccLimit;
	}

	private int cdTimeInMillSecond;

	public int getCdTimeInMillSecond() {
		return cdTimeInMillSecond;
	}

	@Override
	public void ExtraInitAfterLoad() {
		if (cdTime < 0){
			throw new RuntimeException("cdTime不能是负数");
		}
		cdTimeInMillSecond = cdTime * 1000;
	}

	public int getWinScore() {
		return winScore;
	}

	public int getFailScore() {
		return failScore;
	}

}
