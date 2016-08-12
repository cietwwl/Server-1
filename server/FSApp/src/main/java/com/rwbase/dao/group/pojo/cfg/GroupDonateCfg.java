package com.rwbase.dao.group.pojo.cfg;

/*
 * @author HC
 * @date 2016年1月23日 下午12:18:35
 * @Description 捐献的配置表
 */
public class GroupDonateCfg {
	private int donateId;// 捐献Id
	private int vipLevelLimit;// vip等级限制
	private int donateItemType;// 捐献的货币类型
	private int donateVal;// 捐献的值
	private int rewardContribution;// 奖励的帮派贡献
	private int rewardGroupSupply;// 奖励的帮派物资
	private int rewardGroupExp;// 奖励的帮派经验
	private int donateType;// 捐献的类型

	/**
	 * 获取捐献的Id
	 * 
	 * @return
	 */
	public int getDonateId() {
		return donateId;
	}

	/**
	 * 获取捐献的Vip等级限制
	 * 
	 * @return
	 */
	public int getVipLevelLimit() {
		return vipLevelLimit;
	}

	/**
	 * 获取捐献的货币类型
	 * 
	 * @return
	 */
	public int getDonateItemType() {
		return donateItemType;
	}

	/**
	 * 获取捐献的值
	 * 
	 * @return
	 */
	public int getDonateVal() {
		return donateVal;
	}

	/**
	 * 获取捐献之后获得的个人贡献
	 * 
	 * @return
	 */
	public int getRewardContribution() {
		return rewardContribution;
	}

	/**
	 * 获取捐献之后获得的帮派物资数量
	 * 
	 * @return
	 */
	public int getRewardGroupSupply() {
		return rewardGroupSupply;
	}

	/**
	 * 获取捐献之后获得的帮派经验
	 * 
	 * @return
	 */
	public int getRewardGroupExp() {
		return rewardGroupExp;
	}

	/**
	 * 获取捐献类型
	 * 
	 * @return
	 */
	public int getDonateType() {
		return donateType;
	}
}