package com.rwbase.dao.randomBoss.cfg;

public class RandomBossLevelKey {
	
	private int upperlv;
	
	private int lowerLv;

	public RandomBossLevelKey(int upperlv, int lowerLv) {
		super();
		this.upperlv = upperlv;
		this.lowerLv = lowerLv;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + lowerLv;
		result = prime * result + upperlv;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RandomBossLevelKey other = (RandomBossLevelKey) obj;
		if (lowerLv != other.lowerLv)
			return false;
		if (upperlv != other.upperlv)
			return false;
		return true;
	}

	public boolean match(int level) {
		return upperlv>= level && level >= lowerLv;
	}
	
	
	
	

}
