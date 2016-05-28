package com.rwbase.dao.groupsecret.pojo.cfg;

/*
 * @author HC
 * @date 2016年5月25日 下午5:57:12
 * @Description 秘境类型配置表
 */
public class GroupSecretResourceCfg {
	private int id;// 秘境的Id
	private int resType;// 秘境的类型
	private int needTime;// 产出需要的时间（分钟）
	private int reward;// 奖励的物品Id
	private String productRatio;// 每分钟产出资源的权重
	private String groupSupplyRatio;// 每分钟帮派物资的产出权重
	private int robGSRatio;// 掠夺帮派物资的比例
	private String groupExpRatio;// 每分钟帮派经验的产出权重
	private int robGERatio;// 掠夺帮派经验的比例
	private int diamondDropId;// 钻石掉落组的Id
	private int robCount;// 可以掠夺的次数
	private int robGold;// 掠夺的钻石
	private int protectTime;// 保护的时间（分钟）
	private int robProtectTime;// 被掠夺之后的保护时间（分钟）

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

	public String getProductRatio() {
		return productRatio;
	}

	public String getGroupSupplyRatio() {
		return groupSupplyRatio;
	}

	/**
	 * 获取帮派物资的掠夺比例
	 * 
	 * @return
	 */
	public int getRobGSRatio() {
		return robGSRatio;
	}

	public String getGroupExpRatio() {
		return groupExpRatio;
	}

	/**
	 * 获取帮派资源的掠夺比例
	 * 
	 * @return
	 */
	public int getRobGERatio() {
		return robGERatio;
	}

	public int getDiamondDropId() {
		return diamondDropId;
	}

	public int getRobCount() {
		return robCount;
	}

	public int getRobGold() {
		return robGold;
	}

	public int getProtectTime() {
		return protectTime;
	}

	public int getRobProtectTime() {
		return robProtectTime;
	}
}