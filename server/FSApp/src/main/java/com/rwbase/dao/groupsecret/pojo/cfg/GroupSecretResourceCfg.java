package com.rwbase.dao.groupsecret.pojo.cfg;

/*
 * @author HC
 * @date 2016年5月25日 下午5:57:12
 * @Description 秘境类型配置表
 */
public class GroupSecretResourceCfg {
	private int id;// 秘境的Id
	private String name;// 秘境的名字
	private int resType;// 秘境的类型
	private int needTime;// 产出需要的时间（分钟）
	private int reward;// 奖励的物品Id
	private int robCount;// 可以掠夺的次数
	private int protectTime;// 保护的时间（分钟）
	private int robProtectTime;// 被掠夺之后的保护时间（分钟）
	private int fromCreate2RobNeedTime;// 从创建到可以被掠夺至少要过多久
	private int robNeedKeyNum;// 掠夺需要消耗的钥石数量
	private int levelGroupId;// 获取等级对应的Id

	public int getId() {
		return id;
	}

	public int getResType() {
		return resType;
	}

	public int getNeedTime() {
		return needTime;
	}

	public int getReward() {
		return reward;
	}

	public int getRobCount() {
		return robCount;
	}

	public int getProtectTime() {
		return protectTime;
	}

	public int getRobProtectTime() {
		return robProtectTime;
	}

	public int getFromCreate2RobNeedTime() {
		return fromCreate2RobNeedTime;
	}

	public int getRobNeedKeyNum() {
		return robNeedKeyNum;
	}

	public int getLevelGroupId() {
		return levelGroupId;
	}

	public String getName() {
		return name;
	}
}