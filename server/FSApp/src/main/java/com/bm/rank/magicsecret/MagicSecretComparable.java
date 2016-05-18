package com.bm.rank.magicsecret;

public class MagicSecretComparable implements Comparable<MagicSecretComparable> {

	private int level;
	private long exp;

	@Override
	public int compareTo(MagicSecretComparable o) {
		if(level < o.level){
			return -1;
		}
		if(level > o.level){
			return 1;
		}
		if(exp < o.exp){
			return -1;
		}
		if(exp > o.exp){
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
