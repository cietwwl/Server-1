package com.bm.rank.groupCompetition.groupRank;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GCompFightingComparable implements Comparable<GCompFightingComparable> {
	private long groupFight; // 帮派战力
	private int groupLevel; // 帮派等级

	public GCompFightingComparable() {

	}

	public GCompFightingComparable(long groupFight, int groupLevel) {
		this.groupFight = groupFight;
		this.groupLevel = groupLevel;
	}

	public long getGroupFight() {
		return this.groupFight;
	}

	public int getGroupLevel() {
		return groupLevel;
	}

	public void setGroupFight(long groupFight) {
		this.groupFight = groupFight;
	}

	public void setGroupLevel(int groupLevel) {
		this.groupLevel = groupLevel;
	}

	@Override
	public int compareTo(GCompFightingComparable o) {
		if (groupFight > o.groupFight) {
			return 1;
		}
		if (groupFight < o.groupFight) {
			return -1;
		}
		if (groupLevel > o.groupLevel) {
			return 1;
		}
		if (groupLevel < o.groupLevel) {
			return -1;
		}
		return 0;
	}

}
