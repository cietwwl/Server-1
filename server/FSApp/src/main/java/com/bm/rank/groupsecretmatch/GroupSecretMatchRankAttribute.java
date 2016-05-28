package com.bm.rank.groupsecretmatch;

/*
 * @author HC
 * @date 2016年5月26日 下午5:54:03
 * @Description 
 */
public class GroupSecretMatchRankAttribute {
	private long createTime;// 创建时间
	private long switchStateTime;// 转换状态时间
	private int state;// 秘境的状态

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public long getSwitchStateTime() {
		return switchStateTime;
	}

	public void setSwitchStateTime(long switchStateTime) {
		this.switchStateTime = switchStateTime;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}
}