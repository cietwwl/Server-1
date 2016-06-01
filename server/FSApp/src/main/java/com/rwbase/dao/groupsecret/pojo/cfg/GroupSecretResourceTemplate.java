package com.rwbase.dao.groupsecret.pojo.cfg;

import com.log.GameLog;

/*
 * @author HC
 * @date 2016年5月25日 下午6:11:00
 * @Description 
 */
public class GroupSecretResourceTemplate {
	private final int id;// 秘境的Id
	private final int resType;// 秘境的类型
	private final int needTime;// 产出需要的时间（分钟）
	private final int reward;// 奖励的物品Id
	private final int diamondDropId;// 钻石掉落组的Id
	private final int robCount;// 可以掠夺的次数
	private final int robGold;// 掠夺的钻石
	private final int protectTime;// 保护的时间（分钟）
	private final int robProtectTime;// 被掠夺之后保护时间（分钟）
	private final int robGSRatio;// 掠夺帮派物资的权重
	private final int robGERatio;// 掠夺帮派经验的权重
	private final int robRatio;// 掠夺资源的权重
	private final int fromCreate2RobNeedTime;// 从创建到可以被掠夺至少要过多久

	private final float productRatio;// 每分钟产出资源的权重
	private final float groupSupplyRatio;// 每分钟帮派物资的产出权重
	private final float groupExpRatio;// 每分钟帮派经验的产出权重

	public GroupSecretResourceTemplate(GroupSecretResourceCfg cfg) {
		this.id = cfg.getId();
		this.resType = cfg.getResType();
		this.needTime = cfg.getNeedTime();
		this.reward = cfg.getReward();
		this.diamondDropId = cfg.getDiamondDropId();
		this.robCount = cfg.getRobCount();
		this.robGold = cfg.getRobGold();
		this.protectTime = cfg.getProtectTime();
		this.robProtectTime = cfg.getRobProtectTime();
		this.robGSRatio = cfg.getRobGSRatio();
		this.robGERatio = cfg.getRobGERatio();
		this.robRatio = cfg.getRobRatio();
		this.fromCreate2RobNeedTime = cfg.getFromCreate2RobNeedTime();

		try {
			this.productRatio = Float.parseFloat(cfg.getProductRatio());
			this.groupSupplyRatio = Float.parseFloat(cfg.getGroupSupplyRatio());
			this.groupExpRatio = Float.parseFloat(cfg.getGroupExpRatio());
		} catch (Exception e) {
			GameLog.error("解析秘境资源表", "GroupSecretResourceTemplate", "解析过程中把产出权重，物资权重，贡献权重中的某一个出现了异常");
			throw new ExceptionInInitializerError(e);
		}
	}

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

	public int getRobGSRatio() {
		return robGSRatio;
	}

	public int getRobGERatio() {
		return robGERatio;
	}

	public float getProductRatio() {
		return productRatio;
	}

	public float getGroupSupplyRatio() {
		return groupSupplyRatio;
	}

	public float getGroupExpRatio() {
		return groupExpRatio;
	}

	public int getRobRatio() {
		return robRatio;
	}

	public int getFromCreate2RobNeedTime() {
		return fromCreate2RobNeedTime;
	}
}