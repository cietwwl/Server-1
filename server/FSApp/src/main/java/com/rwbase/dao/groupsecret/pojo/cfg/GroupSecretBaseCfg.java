package com.rwbase.dao.groupsecret.pojo.cfg;

/*
 * @author HC
 * @date 2016年5月25日 下午5:18:26
 * @Description 
 */
public class GroupSecretBaseCfg {
	private String buyKeyPrice;// 每次购买钥石的价格
	private String buyKeyAdd;// 每次购买钥石增加的数量
	private int getKeyLimit;// 每天获取钥石的上限
	private int maxKeyLimit;// 钥石的上限
	private int rewardKeyCount;// 每抵挡一波敌人进攻奖励多少钥石
	private int matchNonBattleTimw;// 匹配到人之后多久不打就删除
	private String matchPrice;// 探索的价格
	private int minAssistTime;// 帮忙驻守秘境最小的时间（分钟）
	private int keyRecoveryLimit;// 钥石恢复的上限
	private int initKeyNum;// 初始化的钥石数量
	private int secretCanRobMinLeftTime;// 秘境可以被掠夺的底限剩余时间

	public String getBuyKeyPrice() {
		return buyKeyPrice;
	}

	public String getBuyKeyAdd() {
		return buyKeyAdd;
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

	public int getMatchNonBattleTimw() {
		return matchNonBattleTimw;
	}

	public String getMatchPrice() {
		return matchPrice;
	}

	public int getMinAssistTime() {
		return minAssistTime;
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
}