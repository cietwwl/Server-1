package com.bm.rank.group.createtime;

/*
 * @author HC
 * @date 2016年1月20日 上午11:22:59
 * @Description 创建时间排行榜是按照时间进行排序，时间越小代表越早
 */
public class GroupCreateTimeComparable implements Comparable<GroupCreateTimeComparable> {
	private long time;// 帮派创建的时间

	@Override
	public int compareTo(GroupCreateTimeComparable o) {
		long result = time - o.time;
		if (result == 0) {
			return 0;
		}
		return result > 0 ? -1 : 1;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}
}