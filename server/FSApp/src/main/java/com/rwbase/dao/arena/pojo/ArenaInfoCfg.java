package com.rwbase.dao.arena.pojo;

import com.common.BaseConfig;

public class ArenaInfoCfg extends BaseConfig {
	private int copyType;
	private int count;
	private int cdTime;
	private int winCdTime;
	private int loseCdTime;

	public int getCopyType() {
		return copyType;
	}

	public int getCount() {
		return count;
	}

	public int getCdTime() {
		return cdTime;
	}

	public int getWinCdTime() {
		return winCdTime;
	}

	public int getLoseCdTime() {
		return loseCdTime;
	}

	private int cdTimeInMillSecond;

	public int getCdTimeInMillSecond() {
		return cdTimeInMillSecond;
	}

	@Override
	public void ExtraInitAfterLoad() {
		if (cdTime < 0)
			throw new RuntimeException("cdTime不能是负数");
		cdTimeInMillSecond = cdTime * 1000;
	}

}
