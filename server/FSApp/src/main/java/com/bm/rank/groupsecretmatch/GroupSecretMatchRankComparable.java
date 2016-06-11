package com.bm.rank.groupsecretmatch;

/*
 * @author HC
 * @date 2016年5月26日 下午5:50:34
 * @Description 
 */
public class GroupSecretMatchRankComparable implements Comparable<GroupSecretMatchRankComparable> {
	private int level;// 等级
	private int fighting;// 战力

	@Override
	public int compareTo(GroupSecretMatchRankComparable o) {
		if (level > o.level) {
			return 1;
		} else if (level < o.level) {
			return -1;
		}

		if (fighting > o.fighting) {
			return 1;
		} else if (fighting < o.fighting) {
			return -1;
		}

		return 0;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getFighting() {
		return fighting;
	}

	public void setFighting(int fighting) {
		this.fighting = fighting;
	}
}