package com.bm.rank.level;

public class LevelComparable implements Comparable<LevelComparable> {

	private int level;
	private long exp;

	public LevelComparable() {

	}

	public LevelComparable(int level, long exp) {
		super();
		this.level = level;
		this.exp = exp;
	}

	@Override
	public int compareTo(LevelComparable o) {
		if (level < o.level) {
			return -1;
		}
		if (level > o.level) {
			return 1;
		}
		if (exp < o.exp) {
			return -1;
		}
		if (exp > o.exp) {
			return 1;
		}
		return 0;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public long getExp() {
		return exp;
	}

	public void setExp(long exp) {
		this.exp = exp;
	}

}
