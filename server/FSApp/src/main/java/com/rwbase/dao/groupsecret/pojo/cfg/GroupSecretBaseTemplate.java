package com.rwbase.dao.groupsecret.pojo.cfg;

import org.springframework.util.StringUtils;

import com.common.HPCUtil;

/*
 * @author HC
 * @date 2016年5月25日 下午5:35:45
 * @Description 
 */
public class GroupSecretBaseTemplate {
	private final int[] buyKeyPrice;// 每次购买钥石的价格
	private final int[] buyKeyAdd;// 每次购买钥石增加的数量
	private final int getKeyLimit;// 每天获取钥石的上限
	private final int maxKeyLimit;// 钥石的上限
	private final int rewardKeyCount;// 每抵挡一波敌人进攻奖励多少钥石
	private final int matchNonBattleTime;// 匹配到人之后多久不打就删除
	private final int[] matchPrice;// 探索的价格
	private final int minAssistTime;// 帮忙驻守秘境最小的时间（分钟）
	private final int keyRecoveryLimit;// 钥石恢复的上限
	private final int initKeyNum;// 初始化的钥石数量
	private final int secretCanRobMinLeftTime;// 秘境可以被掠夺的底限剩余时间
	private final int maxDefendLogSize;// 最大的可以保存防守记录的条数

	public GroupSecretBaseTemplate(GroupSecretBaseCfg cfg) {
		this.getKeyLimit = cfg.getGetKeyLimit();
		this.maxKeyLimit = cfg.getMaxKeyLimit();
		this.rewardKeyCount = cfg.getRewardKeyCount();
		this.matchNonBattleTime = cfg.getMatchNonBattleTime();
		this.minAssistTime = cfg.getMinAssistTime();
		this.keyRecoveryLimit = cfg.getKeyRecoveryLimit();
		this.initKeyNum = cfg.getInitKeyNum();
		this.secretCanRobMinLeftTime = cfg.getSecretCanRobMinLeftTime();
		this.maxDefendLogSize = cfg.getMaxDefendLogSize();

		// 转换成对应的数组
		String buyPriceStr = cfg.getBuyKeyPrice();
		if (StringUtils.isEmpty(buyPriceStr)) {
			buyKeyPrice = new int[0];
		} else {
			buyKeyPrice = HPCUtil.parseIntegerArray(buyPriceStr, ",");
		}

		String matchPriceStr = cfg.getMatchPrice();
		if (StringUtils.isEmpty(matchPriceStr)) {
			matchPrice = new int[0];
		} else {
			matchPrice = HPCUtil.parseIntegerArray(matchPriceStr, ",");
		}

		String buyKeyAddStr = cfg.getBuyKeyAdd();
		if (StringUtils.isEmpty(buyKeyAddStr)) {
			buyKeyAdd = new int[0];
		} else {
			buyKeyAdd = HPCUtil.parseIntegerArray(buyKeyAddStr, ",");
		}
	}

	/**
	 * 获取某次对应的购买钥石价格
	 * 
	 * @param times
	 * @return
	 */
	public int getBuyKeyPrice(int times) {
		if (times < 0) {
			times = 0;
		}

		if (times >= buyKeyPrice.length) {
			return -1;
		}

		return buyKeyPrice[times];
	}

	/**
	 * 获取某次对应的购买钥石增加的数量
	 * 
	 * @param times
	 * @return
	 */
	public int getBuyKeyAdd(int times) {
		if (times < 0) {
			times = 0;
		}

		if (times >= buyKeyAdd.length) {
			return buyKeyAdd[buyKeyAdd.length - 1];
		}

		return buyKeyAdd[times];
	}

	public int getGetKeyLimit() {
		return getKeyLimit;
	}

	public int getMaxKeyLimit() {
		return maxKeyLimit;
	}

	public int getRewardKeyCount() {
		return rewardKeyCount;
	}

	public int getMatchNonBattleTime() {
		return matchNonBattleTime;
	}

	public int getMinAssistTime() {
		return minAssistTime;
	}

	/**
	 * 获取某次对应的搜索价格
	 * 
	 * @param times
	 * @return
	 */
	public int getMatchPrice(int times) {
		if (times < 0) {
			times = 0;
		}

		if (times >= matchPrice.length) {
			return matchPrice[matchPrice.length - 1];
		}

		return matchPrice[times];
	}

	public int getKeyRecoveryLimit() {
		return keyRecoveryLimit;
	}

	public int getInitKeyNum() {
		return initKeyNum;
	}

	public int getSecretCanRobMinLeftTime() {
		return secretCanRobMinLeftTime;
	}

	public int getMaxDefendLogSize() {
		return maxDefendLogSize;
	}
}