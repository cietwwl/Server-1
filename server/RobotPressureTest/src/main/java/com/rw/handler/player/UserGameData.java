package com.rw.handler.player;

import com.rw.dataSyn.SynItem;

public class UserGameData implements SynItem{
	
	private String userId; // 用户ID
	private long version; // 数据版本
	private boolean iphone;

	private int power;// 体力
	private int maxPower;// 最大体力
	private long upgradeExp;// 升级经验
	private int buyPowerTimes;// 当天购买体力次数
	private int buyCoinTimes;// 当天购买铜钱次数
	private int buySkillTimes;// 当天购买技能次数

	private int rookieFlag;// 新手标志
	private int freeChat;// 免费聊天次数
	private long lastAddPowerTime;// 上次送体力时间
	private long lastResetTime;// 上次重置时间(用于每天点重置)
	private long lastResetTime5Clock;
	private long lastChangeInfoTime;// 上次变更人物信息时间

	private String headFrame;

	private int skillPointCount;// 剩余技能点数
	private long lastRecoverSkillPointTime;// 需要加满技能点的时间

	private int unendingWarCoin;// 无尽战火;
	private int towerCoin;// 无畏之塔币;

	private int expCoin;// 秘境经验药;
	private int strenCoin;// 秘境强化石;
	private int peakArenaCoin;// 巅峰竞技场货币
	private int arenaCoin; // 职业竞技场货币
	private int wakenPiece;//觉醒碎片
	private int wakenKey;//觉醒钥匙

	private long carrerChangeTime;// 角色变换的时间
	private volatile long lastWorshipTime;
	
	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return userId;
	}

	public long getCarrerChangeTime() {
		return carrerChangeTime;
	}

	public String getUserId() {
		return userId;
	}

	public long getVersion() {
		return version;
	}

	public boolean isIphone() {
		return iphone;
	}

	public int getPower() {
		return power;
	}

	public int getMaxPower() {
		return maxPower;
	}

	public long getUpgradeExp() {
		return upgradeExp;
	}

	public int getBuyPowerTimes() {
		return buyPowerTimes;
	}

	public int getBuyCoinTimes() {
		return buyCoinTimes;
	}

	public int getBuySkillTimes() {
		return buySkillTimes;
	}

	public int getRookieFlag() {
		return rookieFlag;
	}

	public int getFreeChat() {
		return freeChat;
	}

	public long getLastAddPowerTime() {
		return lastAddPowerTime;
	}

	public long getLastResetTime() {
		return lastResetTime;
	}

	public long getLastResetTime5Clock() {
		return lastResetTime5Clock;
	}

	public long getLastChangeInfoTime() {
		return lastChangeInfoTime;
	}

	public String getHeadFrame() {
		return headFrame;
	}

	public int getSkillPointCount() {
		return skillPointCount;
	}

	public long getLastRecoverSkillPointTime() {
		return lastRecoverSkillPointTime;
	}

	public int getUnendingWarCoin() {
		return unendingWarCoin;
	}

	public int getTowerCoin() {
		return towerCoin;
	}

	public int getExpCoin() {
		return expCoin;
	}

	public int getStrenCoin() {
		return strenCoin;
	}

	public int getPeakArenaCoin() {
		return peakArenaCoin;
	}

	public int getArenaCoin() {
		return arenaCoin;
	}

	public int getWakenPiece() {
		return wakenPiece;
	}

	public int getWakenKey() {
		return wakenKey;
	}

	public long getLastWorshipTime() {
		return lastWorshipTime;
	}
}
