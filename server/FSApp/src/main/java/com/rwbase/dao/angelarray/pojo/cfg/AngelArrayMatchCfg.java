package com.rwbase.dao.angelarray.pojo.cfg;

/*
 * @author HC
 * @date 2015年11月13日 下午2:52:37
 * @Description 万仙阵匹配规则
 */
public class AngelArrayMatchCfg {
	private int uniqueId;// 单挑数据的唯一标识
	private int level;// 匹配段的最低等级
	private int maxLevel;// 匹配段位的最高等级（包含）
	private int floor;// 层数
	private float minFightingRatio;// 匹配最低战力下限
	private float maxFightingRatio;// 匹配最高战力上限
	private int robotId;// 机器人的Id

	// //////////////////////////////////////////////GET区

	public int getUniqueId() {
		return uniqueId;
	}

	public int getLevel() {
		return level;
	}

	public int getFloor() {
		return floor;
	}

	public float getMinFightingRatio() {
		return minFightingRatio;
	}

	public float getMaxFightingRatio() {
		return maxFightingRatio;
	}

	public int getRobotId() {
		return robotId;
	}

	public int getMaxLevel() {
		return maxLevel;
	}

	// //////////////////////////////////////////////SET区

	public void setUniqueId(int uniqueId) {
		this.uniqueId = uniqueId;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public void setFloor(int floor) {
		this.floor = floor;
	}

	public void setMinFightingRatio(float minFightingRatio) {
		this.minFightingRatio = minFightingRatio;
	}

	public void setMaxFightingRatio(float maxFightingRatio) {
		this.maxFightingRatio = maxFightingRatio;
	}

	public void setRobotId(int robotId) {
		this.robotId = robotId;
	}

	public void setMaxLevel(int maxLevel) {
		this.maxLevel = maxLevel;
	}
}