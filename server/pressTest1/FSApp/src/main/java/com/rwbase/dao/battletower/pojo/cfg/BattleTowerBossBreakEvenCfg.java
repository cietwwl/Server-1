package com.rwbase.dao.battletower.pojo.cfg;

/*
 * @author HC
 * @date 2015年9月8日 下午5:04:35
 * @Description 
 */
public class BattleTowerBossBreakEvenCfg {
	private int minFloor;// 保底的最低层数
	private int maxFloor;// 保底的最高层数
	private int bossBreakEvenNum;// Boss保底产生的数量

	public int getMinFloor() {
		return minFloor;
	}

	public int getMaxFloor() {
		return maxFloor;
	}

	public int getBossBreakEvenNum() {
		return bossBreakEvenNum;
	}

	public void setMinFloor(int minFloor) {
		this.minFloor = minFloor;
	}

	public void setMaxFloor(int maxFloor) {
		this.maxFloor = maxFloor;
	}

	public void setBossBreakEvenNum(int bossBreakEvenNum) {
		this.bossBreakEvenNum = bossBreakEvenNum;
	}
}