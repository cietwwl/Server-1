package com.rw.handler.groupsecret;

import com.rw.dataSyn.SynItem;

public class SecretBaseInfoSynData implements SynItem{
	private String id;// 秘境的Id
	private int cfgId;// 秘境的配置Id
	private boolean isFinish;// 秘境是否驻守完成
	private long creatTime;// 秘境创建的时间
	private int myIndex;// 我自己驻守的那个矿点
	private int dropDiamond;// 掉落的钻石
	private int getRes;// 被掠夺的资源
	private int getGE;// 被掠夺的帮派经验
	private int getGS;// 被掠夺的帮派物资
	private String groupId;// 此秘境所属的帮派Id，对于自己创建的是自己创建那一刻的帮派Id，对于匹配的来说，是自己匹配到那一刻的秘境Id

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

	public int getDropDiamond() {
		return dropDiamond;
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
