package com.rwbase.dao.groupsecret.pojo.db.data;

import java.util.List;

import com.rw.fsutil.dao.annotation.CombineSave;

/*
 * @author HC
 * @date 2016年5月26日 下午2:43:32
 * @Description 驻守人的信息
 */
public class DefendUserInfoData {
	private String userId;// 驻守人的Id
	private int index;// 驻守点
	private List<String> heroList;// 驻守的英雄信息（包含了主角）
	private long defTime;// 驻守进来的时间点
	private long changeTeamTime;// 更换阵容的时间
	private int fighting;// 驻守时的战力
	// ///////////////////////////////////////////////被掠夺资源
	@CombineSave(Column = "rob")
	private int robRes;// 可以掠夺的资源数量
	@CombineSave(Column = "rob")
	private int robGS;// 可以掠夺的帮派物资
	@CombineSave(Column = "rob")
	private int robGE;// 可以掠夺的帮派经验
	// ///////////////////////////////////////////////在更换阵容前获取的资源
	@CombineSave(Column = "product")
	private int proRes;// 可以掠夺的资源数量
	@CombineSave(Column = "product")
	private int proGS;// 可以掠夺的帮派物资
	@CombineSave(Column = "product")
	private int proGE;// 可以掠夺的帮派经验
	// ///////////////////////////////////////////////获取的钻石
	private int dropDiamond;// 掉落的钻石

	// ////////////////////////////////////////////////逻辑Set区

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public void setHeroList(List<String> heroList) {
		this.heroList = heroList;
	}

	public void setDefTime(long defTime) {
		this.defTime = defTime;
	}

	public void setChangeTeamTime(long changeTeamTime) {
		this.changeTeamTime = changeTeamTime;
	}

	public void setRobRes(int robRes) {
		this.robRes = robRes;
	}

	public void setRobGS(int robGS) {
		this.robGS = robGS;
	}

	public void setRobGE(int robGE) {
		this.robGE = robGE;
	}

	public void setProRes(int proRes) {
		this.proRes = proRes;
	}

	public void setProGS(int proGS) {
		this.proGS = proGS;
	}

	public void setProGE(int proGE) {
		this.proGE = proGE;
	}

	public void setDropDiamond(int dropDiamond) {
		this.dropDiamond = dropDiamond;
	}

	public void setFighting(int fighting) {
		this.fighting = fighting;
	}

	// ////////////////////////////////////////////////逻辑Get区
	public String getUserId() {
		return userId;
	}

	public int getIndex() {
		return index;
	}

	public List<String> getHeroList() {
		return heroList;
	}

	public long getDefTime() {
		return defTime;
	}

	public long getChangeTeamTime() {
		return changeTeamTime;
	}

	public int getRobRes() {
		return robRes;
	}

	public int getRobGS() {
		return robGS;
	}

	public int getRobGE() {
		return robGE;
	}

	public int getProRes() {
		return proRes;
	}

	public int getProGS() {
		return proGS;
	}

	public int getProGE() {
		return proGE;
	}

	public int getDropDiamond() {
		return dropDiamond;
	}

	public int getFighting() {
		return fighting;
	}
	// ////////////////////////////////////////////////逻辑区
}