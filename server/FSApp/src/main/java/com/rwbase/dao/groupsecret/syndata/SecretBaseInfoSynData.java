package com.rwbase.dao.groupsecret.syndata;

import com.playerdata.dataSyn.annotation.SynClass;

/*
 * @author HC
 * @date 2016年5月30日 下午12:32:50
 * @Description 
 */
@SynClass
public class SecretBaseInfoSynData {
	private final String id;// 秘境的Id
	private final int cfgId;// 秘境的配置Id
	private final boolean isFinish;// 秘境是否驻守完成
	private final long creatTime;// 秘境创建的时间
	private final int myIndex;// 我自己驻守的那个矿点
	private final int dropDiamond;// 掉落的钻石
	private final int getRes;// 被掠夺的资源
	private final int getGE;// 被掠夺的帮派经验
	private final int getGS;// 被掠夺的帮派物资

	public SecretBaseInfoSynData(String id, int cfgId, boolean isFinish, long creatTime, int myIndex, int dropDiamond, int getRes, int getGE, int getGS) {
		this.id = id;
		this.cfgId = cfgId;
		this.isFinish = isFinish;
		this.creatTime = creatTime;
		this.myIndex = myIndex;
		this.dropDiamond = dropDiamond;
		this.getRes = getRes;
		this.getGE = getGE;
		this.getGS = getGS;
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
}