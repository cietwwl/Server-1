package com.common;

public class RandomMgr implements IRandomMgr {
	//0-100的随机数种子
	public static final int SeedRange = 101;
	private int newRamdomSeed;
	@Override
	public int getRandomSeed() {
		return newRamdomSeed;
	}

	@Override
	public int RefreshSeed() {
		int result = newRamdomSeed;
		newRamdomSeed = HPCUtil.getRandom().nextInt(SeedRange);
		return result;
	}

	@Override
	public int getSeedRange() {
		return SeedRange;
	}
}
