package com.bm.rank.group.base;

/*
 * @author HC
 * @date 2016年1月19日 下午8:45:17
 * @Description 帮派基础排行榜的比较条件
 */
public class GroupBaseRankComparable implements Comparable<GroupBaseRankComparable> {
	private int groupLevel;// 帮派等级
	private int groupExp;// 帮派经验
	private long toLevelTime;// 达成等级的时间

	@Override
	public int compareTo(GroupBaseRankComparable o) {
//		int result = groupLevel - o.groupLevel;
//		if (result > 0) {
//			return -1;
//		} else if (result < 0) {
//			return 1;
//		}
//
//		result = groupExp - o.groupExp;
//		if (result > 0) {
//			return -1;
//		} else if (result < 0) {
//			return 1;
//		}
//
//		long lResult = toLevelTime - o.toLevelTime;
//		if (lResult == 0) {
//			return 0;
//		}
//		return lResult > 0 ? 1 : -1;
		int result = groupLevel - o.groupLevel;
		if (result > 0) {
			return 1;
		} else if (result < 0) {
			return -1;
		}

		result = groupExp - o.groupExp;
		if (result > 0) {
			return 1;
		} else if (result < 0) {
			return -1;
		}

		long lResult = toLevelTime - o.toLevelTime;
		if (lResult == 0) {
			return 0;
		}
		return lResult > 0 ? -1 : 1;
	}

	/**
	 * 设置比较条件中的帮派等级
	 * 
	 * @param groupLevel
	 */
	public void setGroupLevel(int groupLevel) {
		this.groupLevel = groupLevel;
	}

	/**
	 * 设置比较条件中的帮派经验
	 * 
	 * @param groupExp
	 */
	public void setGroupExp(int groupExp) {
		this.groupExp = groupExp;
	}

	/**
	 * 设置比较条件中的到达等级时间
	 * 
	 * @param toLevelTime
	 */
	public void setToLevelTime(long toLevelTime) {
		this.toLevelTime = toLevelTime;
	}

	public int getGroupLevel() {
		return groupLevel;
	}

	public int getGroupExp() {
		return groupExp;
	}

	public long getToLevelTime() {
		return toLevelTime;
	}
}