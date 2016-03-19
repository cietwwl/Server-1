package com.rwbase.dao.user;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.SkillMgr;
import com.playerdata.dataSyn.annotation.SynClass;
import com.rwbase.dao.user.readonly.TableUserOtherIF;

@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "user_other")
@SynClass
public class UserGameData implements TableUserOtherIF {

	@Id
	private String userId; // 用户ID
	private long version; // 数据版本
	private boolean iphone;
	private long coin;// 铜钱
	private int gold;// 金钱
	private int power;// 体力
	private int maxPower;// 最大体力
	private long upgradeExp;// 升级经验
	private int buyPowerTimes;// 当天购买体力次数
	private int buyCoinTimes;// 当天购买铜钱次数
	private int buySkillTimes;// 当天购买技能次数
	private long lastLoginTime;// 登陆时间chuo


	private int rookieFlag;// 新手标志
	private int freeChat;// 免费聊天次数
	private long lastAddPowerTime;// 上次送体力时间
	private long lastResetTime;// 上次重置时间(用于每天点重置)
	private long lastResetTime5Clock;
	private long lastChangeInfoTime;// 上次变更人物信息时间

	private int taskNum; // 当前还剩余任务的数量
	private int recharge;// 已充值总额（钻石）
	private String headFrame;

	private int skillPointCount = SkillMgr.MAX_SKILL_COUNT;// 剩余技能点数
	private long lastRecoverSkillPointTime;// 需要加满技能点的时间

	private int unendingWarCoin;// 无尽战火;
	private int towerCoin;// 无畏之塔币;


	private int expCoin;// 秘境经验药;
	private int strenCoin;// 秘境强化石;
	private int peakArenaCoin;// 巅峰竞技场货币
	private int arenaCoin; // 职业竞技场货币
	
	private long carrerChangeTime;//角色变换的时间
	
	private UserGameExtendInfo extendInfo;

	public int getExpCoin() {
		return expCoin;
	}

	public void setExpCoin(int expCoin) {
		this.expCoin = expCoin;
	}

	public int getStrenCoin() {
		return strenCoin;
	}

	public void setStrenCoin(int strenCoin) {
		this.strenCoin = strenCoin;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public long getVersion() {
		return version;
	}

	public void setVersion(long version) {
		this.version = version;
	}

	public boolean isIphone() {
		return iphone;
	}

	public void setIphone(boolean iphone) {
		this.iphone = iphone;
	}

	public long getCoin() {
		return coin;
	}

	public void setCoin(long coin) {
		this.coin = coin;
	}

	public int getPower() {
		return power;
	}

	public void setPower(int power) {
		this.power = power;
	}

	public void setMaxPower(int maxPower) {
		this.maxPower = maxPower;
	}

	public int getMaxPower() {
		return this.maxPower;
	}

	public long getUpgradeExp() {
		return upgradeExp;
	}

	public void setUpgradeExp(long upgradeExp) {
		this.upgradeExp = upgradeExp;
	}

	public int getBuyPowerTimes() {
		return buyPowerTimes;
	}

	public void setBuyPowerTimes(int buyPowerTimes) {
		this.buyPowerTimes = buyPowerTimes;
	}

	public int getBuyCoinTimes() {
		return buyCoinTimes;
	}

	public void setBuyCoinTimes(int buyCoinTimes) {
		this.buyCoinTimes = buyCoinTimes;
	}

	public int getGold() {
		return gold;
	}

	public void setGold(int gold) {
		this.gold = gold;
	}

	public int getRookieFlag() {
		return rookieFlag;
	}

	public void setRookieFlag(int rookieFlag) {
		this.rookieFlag = rookieFlag;
	}

	public int getFreeChat() {
		return freeChat;
	}

	public void setFreeChat(int freeChat) {
		this.freeChat = freeChat;
	}

	public long getLastAddPowerTime() {
		return lastAddPowerTime;
	}

	public void setLastAddPowerTime(long lastAddPowerTime) {
		this.lastAddPowerTime = lastAddPowerTime;
	}

	public long getLastResetTime() {
		return lastResetTime;
	}

	public void setLastResetTime(long lastResetTime) {
		this.lastResetTime = lastResetTime;
	}

	public long getLastResetTime5Clock() {
		return lastResetTime5Clock;
	}

	public void setLastResetTime5Clock(long lastResetTime5Clock) {
		this.lastResetTime5Clock = lastResetTime5Clock;
	}

	public long getLastChangeInfoTime() {
		return lastChangeInfoTime;
	}

	public void setLastChangeInfoTime(long lastChangeInfoTime) {
		this.lastChangeInfoTime = lastChangeInfoTime;
	}

	/** 已充值总额（钻石） */
	public int getRecharge() {
		return recharge;
	}

	public void setRecharge(int recharge) {
		this.recharge = recharge;
	}

	public int getTaskNum() {
		return taskNum;
	}

	public void setTaskNum(int taskNum) {
		this.taskNum = taskNum;
	}

	public int getSkillPointCount() {
		return skillPointCount;
	}

	public void setSkillPointCount(int skillPointCount) {
		this.skillPointCount = skillPointCount;
	}

	public long getLastLoginTime() {
		return lastLoginTime;
	}

	public void setLastLoginTime(long lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}
	public String getHeadFrame() {
		return headFrame;
	}

	public void setHeadFrame(String headerBox) {
		this.headFrame = headerBox;
	}

	public int getUnendingWarCoin() {
		return unendingWarCoin;
	}

	public void setUnendingWarCoin(int unendingWarCoin) {
		this.unendingWarCoin = unendingWarCoin;
	}

	public int getTowerCoin() {
		return towerCoin;
	}

	public void setTowerCoin(int towerCoin) {
		this.towerCoin = towerCoin;
	}

	public int getBuySkillTimes() {
		return buySkillTimes;
	}

	public void setBuySkillTimes(int buySkillTimes) {
		this.buySkillTimes = buySkillTimes;
	}

	public int getPeakArenaCoin() {
		return peakArenaCoin;
	}

	public void setPeakArenaCoin(int peakArenaCoin) {
		this.peakArenaCoin = peakArenaCoin;
	}

	public int getArenaCoin() {
		return arenaCoin;
	}

	public void setArenaCoin(int arenaCoin) {
		this.arenaCoin = arenaCoin;
	}
	public long getLastRecoverSkillPointTime() {
		return lastRecoverSkillPointTime;
	}

	public void setLastRecoverSkillPointTime(long lastRecoverSkillPointTime) {
		this.lastRecoverSkillPointTime = lastRecoverSkillPointTime;
	}

	public long getCarrerChangeTime() {
		return carrerChangeTime;
	}

	public void setCarrerChangeTime(long carrerChangeTime) {
		this.carrerChangeTime = carrerChangeTime;
	}

	public UserGameExtendInfo getExtendInfo() {
		if(extendInfo==null){
			extendInfo = new UserGameExtendInfo();
		}
		return extendInfo;
	}

	public void setExtendInfo(UserGameExtendInfo extendInfo) {
		this.extendInfo = extendInfo;
	}
	
	

}
