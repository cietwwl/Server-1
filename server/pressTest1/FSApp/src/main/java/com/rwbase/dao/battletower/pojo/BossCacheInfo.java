package com.rwbase.dao.battletower.pojo;

/*
 * @author HC
 * @date 2015年9月8日 下午6:10:48
 * @Description 产生Boss的信息（用于还没重置的时候的缓存数据）
 */
public class BossCacheInfo {
	private final int bossId;// 产生的BossId
	private final int markId;// Boss产生所在的里程碑

	public BossCacheInfo(int bossId, int markId) {
		this.bossId = bossId;
		this.markId = markId;
	}

	public int getBossId() {
		return bossId;
	}

	public int getMarkId() {
		return markId;
	}
}