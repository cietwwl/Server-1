package com.rwbase.dao.battletower.pojo.cfg;

/*
 * @author HC
 * @date 2015年9月3日 上午10:58:50
 * @Description 
 */
public class BattleTowerFloorCfg {
	private int floor;// （试练塔层数）
	private int groupId;// 组Id
	private int bossPro;// 产生Boss的概率
	private int markId;// 里程碑Id
	private int bossBreakEvenNum;// Boss保底数量(如果这个数值大于0，则证明这就是里程碑所在的最后一站)

	// /////////////////////////////////////////SET区

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	public void setFloor(int floor) {
		this.floor = floor;
	}

	public void setBossPro(int bossPro) {
		this.bossPro = bossPro;
	}

	public void setMarkId(int markId) {
		this.markId = markId;
	}

	public void setBossBreakEvenNum(int bossBreakEvenNum) {
		this.bossBreakEvenNum = bossBreakEvenNum;
	}

	// /////////////////////////////////////////GET区
	public int getGroupId() {
		return groupId;
	}

	public int getFloor() {
		return floor;
	}

	public int getBossPro() {
		return bossPro;
	}

	public int getMarkId() {
		return markId;
	}

	public int getBossBreakEvenNum() {
		return bossBreakEvenNum;
	}
}