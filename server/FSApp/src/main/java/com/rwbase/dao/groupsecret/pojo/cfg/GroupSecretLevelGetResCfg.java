package com.rwbase.dao.groupsecret.pojo.cfg;

/*
 * @author HC
 * @date 2016年6月7日 上午11:33:15
 * @Description 
 */
public class GroupSecretLevelGetResCfg {
	private int level;// 可以掉落的Id
	private int levelGroupId;// 等级组Id
	private int dropDiamondId;// 钻石掉落组的Id
	private int robDiamond;// 掠夺的钻石
	private int robGSRatio;// 掠夺帮派物资的权重
	private int robGERatio;// 掠夺帮派经验的权重
	private int robRatio;// 掠夺资源的权重
	private String productRatio;// 每分钟产出资源的权重
	private String groupSupplyRatio;// 每分钟帮派物资的产出权重
	private String groupExpRatio;// 每分钟帮派经验的产出权重
	private String dropIdBasedOnJoinTime;// 基于驻守剩余的时间对应的掉落宝石的方案

	public int getLevel() {
		return level;
	}

	public int getLevelGroupId() {
		return levelGroupId;
	}

	public int getDiamondDropId() {
		return dropDiamondId;
	}

	public int getRobDiamond() {
		return robDiamond;
	}

	public int getRobGSRatio() {
		return robGSRatio;
	}

	public int getRobGERatio() {
		return robGERatio;
	}

	public int getRobRatio() {
		return robRatio;
	}

	public String getProductRatio() {
		return productRatio;
	}

	public String getGroupSupplyRatio() {
		return groupSupplyRatio;
	}

	public String getGroupExpRatio() {
		return groupExpRatio;
	}

	public String getDropIdBasedOnJoinTime() {
		return dropIdBasedOnJoinTime;
	}
}