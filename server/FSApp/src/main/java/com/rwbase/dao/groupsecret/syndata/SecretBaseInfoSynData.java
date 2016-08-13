package com.rwbase.dao.groupsecret.syndata;

import com.playerdata.dataSyn.annotation.SynClass;

/*
 * @author HC
 * @date 2016年5月30日 下午12:32:50
 * @Description 
 */
@SynClass
public class SecretBaseInfoSynData {
	private int mainPos = 0; // 主界面的位置
	private final String id;// 秘境的Id
	private final int cfgId;// 秘境的配置Id
	private final boolean isFinish;// 秘境是否驻守完成
	private final long creatTime;// 秘境创建的时间
	private final int myIndex;// 我自己驻守的那个矿点
	private final int dropDiamond;// 掉落的钻石
	private final int getRes;// 可以获得的资源
	private final int getGE;// 可以获得的帮派经验
	private final int getGS;// 可以获得的帮派资源
	private final String groupId;// 此秘境所属的帮派Id，对于自己创建的是自己创建那一刻的帮派Id，对于匹配的来说，是自己匹配到那一刻的秘境Id
	@SuppressWarnings("unused")
	private int robRes; // 被掠夺的资源
	@SuppressWarnings("unused")
	private int robGE; // 被掠夺的帮派经验
	@SuppressWarnings("unused")
	private int robGS; // 被掠夺的帮派资源
	@SuppressWarnings("unused")
	private int robTimes; // 被掠夺的次数

	public SecretBaseInfoSynData(String id, int cfgId, boolean isFinish, long creatTime, int myIndex, int dropDiamond, int getRes, int getGE, int getGS, String groupId) {
		this.id = id;
		this.cfgId = cfgId;
		this.isFinish = isFinish;
		this.creatTime = creatTime;
		this.myIndex = myIndex;
		this.dropDiamond = dropDiamond;
		this.getRes = getRes;
		this.getGE = getGE;
		this.getGS = getGS;
		this.groupId = groupId;
	}
	
	public void setRoboInfo(int pRobTimes, int pRobRes, int pRobGE, int pRobGS) {
		this.robTimes = pRobTimes;
		this.robRes = pRobRes;
		this.robGE = pRobGE;
		this.robGS = pRobGS;
	}
	
	public void setMainPos(int pos) {
		this.mainPos = pos;
	}
	
	public int getMainPos() {
		return mainPos;
	}

	public int getDropDiamond() {
		return dropDiamond;
	}

	public String getId() {
		return id;
	}

	public int getCfgId() {
		return cfgId;
	}

	public boolean isFinish() {
		return isFinish;
	}

	public long getCreatTime() {
		return creatTime;
	}

	public int getMyIndex() {
		return myIndex;
	}

	public int getGetRes() {
		return getRes;
	}

	public int getGetGE() {
		return getGE;
	}

	public int getGetGS() {
		return getGS;
	}

	public String getGroupId() {
		return groupId;
	}
}