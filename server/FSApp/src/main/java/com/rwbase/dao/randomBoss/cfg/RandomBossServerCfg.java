package com.rwbase.dao.randomBoss.cfg;

/**
 * 随机boss系统配置
 * @author Alex
 * 2016年9月10日 下午4:59:38
 */
public class RandomBossServerCfg {

	private int openLv;//功能开启等级
	private int maxBattleCount;//每人每天可讨伐次数
	private int createBossCountLimit;//每人每天生成boss次数上限
	private int killBossRewardLimit;//每天可领取击杀奖励次数
	private int bossBornRate;//出现bosss机率
	private int battleTimeLimit;//单次战斗时间上限(s)
	private String bossBornTips;//boss出现提示
	private String invitedTimeOutTips;//邀请超时提示
	private String invitedAccepted;//邀请已经接受提示
	private String bossExcapeTips;//boss已经离开提示
	private String totalFightLimitTips;//当天所有boss挑战达到上限
	private String singleBossFightLimitTips;//单个boss讨伐达到上限提示
	private String bossWasKilledTips;//boss被击杀提示
	private String bossInBattleTips;//boss正在战斗提示
	
	
	public int getOpenLv() {
		return openLv;
	}
	public void setOpenLv(int openLv) {
		this.openLv = openLv;
	}
	public int getMaxBattleCount() {
		return maxBattleCount;
	}
	public void setMaxBattleCount(int maxBattleCount) {
		this.maxBattleCount = maxBattleCount;
	}
	public int getCreateBossCountLimit() {
		return createBossCountLimit;
	}
	public void setCreateBossCountLimit(int createBossCountLimit) {
		this.createBossCountLimit = createBossCountLimit;
	}
	public int getKillBossRewardLimit() {
		return killBossRewardLimit;
	}
	public void setKillBossRewardLimit(int killBossRewardLimit) {
		this.killBossRewardLimit = killBossRewardLimit;
	}
	public int getBossBornRate() {
		return bossBornRate;
	}
	public void setBossBornRate(int bossBornRate) {
		this.bossBornRate = bossBornRate;
	}
	public int getBattleTimeLimit() {
		return battleTimeLimit;
	}
	public void setBattleTimeLimit(int battleTimeLimit) {
		this.battleTimeLimit = battleTimeLimit;
	}
	public String getBossBornTips() {
		return bossBornTips;
	}
	public void setBossBornTips(String bossBornTips) {
		this.bossBornTips = bossBornTips;
	}
	public String getInvitedTimeOutTips() {
		return invitedTimeOutTips;
	}
	public void setInvitedTimeOutTips(String invitedTimeOutTips) {
		this.invitedTimeOutTips = invitedTimeOutTips;
	}
	public String getInvitedAccepted() {
		return invitedAccepted;
	}
	public void setInvitedAccepted(String invitedAccepted) {
		this.invitedAccepted = invitedAccepted;
	}
	public String getBossExcapeTips() {
		return bossExcapeTips;
	}
	public void setBossExcapeTips(String bossExcapeTips) {
		this.bossExcapeTips = bossExcapeTips;
	}
	public String getTotalFightLimitTips() {
		return totalFightLimitTips;
	}
	public void setTotalFightLimitTips(String totalFightLimitTips) {
		this.totalFightLimitTips = totalFightLimitTips;
	}
	public String getSingleBossFightLimitTips() {
		return singleBossFightLimitTips;
	}
	public void setSingleBossFightLimitTips(String singleBossFightLimitTips) {
		this.singleBossFightLimitTips = singleBossFightLimitTips;
	}
	public String getBossWasKilledTips() {
		return bossWasKilledTips;
	}
	public void setBossWasKilledTips(String bossWasKilledTips) {
		this.bossWasKilledTips = bossWasKilledTips;
	}
	public String getBossInBattleTips() {
		return bossInBattleTips;
	}
	public void setBossInBattleTips(String bossInBattleTips) {
		this.bossInBattleTips = bossInBattleTips;
	}
	
	
	
	
	
	
	
}
