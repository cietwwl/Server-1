package com.rw.service.TaoistMagic.datamodel;

public class TaoistMagicFormula {
	private int valueInit;//结果初值
	private int deltaInit;//速度初值
	private int accDelta;//加速度递增值
	private int levelDelta;//等级间隔，用于计算加速度
	
	private int[] valueCache;
	private int[] deltaCache;
	private int[] accCache;
	
	public TaoistMagicFormula(int valueInit, int deltaInit, int accDelta, int levelDelta) {
		this.valueInit = valueInit;
		this.deltaInit = deltaInit;
		this.accDelta = accDelta;
		this.levelDelta = levelDelta;
		if (valueInit <0 || deltaInit <0 || accDelta <0 || levelDelta <0){
			throw new RuntimeException("无效公式参数:"+this.toString());
		}
	}
	
	// 优化：记住已经计算的结果，用空间换时间
	public void cacheToLevel(int level){
		if (valueCache != null && valueCache.length >= level +1){//已有的缓存已经包含这个等级
			return;
		}
		valueCache = extendCache(valueCache,level);
		deltaCache = extendCache(deltaCache,level);
		accCache = extendCache(accCache,level);
		getValue(level);
	}

	private int[] extendCache(int[] org, int level) {
		int[] tmp = new int[level+1];
		if (org != null){
			System.arraycopy(org, 0, tmp, 0, org.length);
		}
		return tmp;
	}
	
	@Override
	public String toString() {
		return "TaoistMagicFormula [valueInit=" + valueInit + ", deltaInit=" + deltaInit + ", accDelta=" + accDelta
				+ ", levelDelta=" + levelDelta + "]";
	}

	//TODO test cache
	public int getValue(int level){
		if (level <=0) return 0;
		return value(level);
	}

	private int value(int level) {
		if (level <= 0) return 0;
		if (level == 1) return valueInit;
		if (valueCache != null && valueCache.length > level){
			if (valueCache[level] <= 0){
				valueCache[level] = value(level - 1) + delta(level);
			}
			return valueCache[level];
		}
		return value(level-1)+delta(level);
	}

	//速度
	private int delta(int level) {
		if (level <= 0)
			return 0;
		if (deltaCache != null && deltaCache.length > level) {
			if (deltaCache[level] <= 0) {
				deltaCache[level] = deltaInit + acc(level);
			}
			return deltaCache[level];
		}
		return deltaInit + acc(level);
	}

	//加速度
	private int acc(int level) {
		if (level <= 0) return 0;
		if (levelDelta <=0) return 0;
		if (accCache != null && accCache.length > level) {
			if (accCache[level] <= 0) {
				accCache[level] = accDelta * ((level-1) / levelDelta);
			}
			return accCache[level];
		}
		return accDelta * ((level-1) / levelDelta);
	}
	
}
