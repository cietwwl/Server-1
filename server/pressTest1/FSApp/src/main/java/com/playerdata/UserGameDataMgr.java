package com.playerdata;

import java.util.Date;

import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.common.PlayerEventListener;
import com.rw.service.dailyActivity.Enum.DailyActivityType;
import com.rwbase.dao.power.RoleUpgradeCfgDAO;
import com.rwbase.dao.power.pojo.RoleUpgradeCfg;
import com.rwbase.dao.publicdata.PublicData;
import com.rwbase.dao.publicdata.PublicDataCfgDAO;
import com.rwbase.dao.user.UserGameData;
import com.rwbase.dao.user.UserGameDataHolder;
import com.rwbase.dao.user.UserGameExtendInfo;
import com.rwbase.dao.user.readonly.TableUserOtherIF;


public class UserGameDataMgr implements PlayerEventListener{
	
	private UserGameDataHolder userGameDataHolder;
	private Player player;// 角色

	public UserGameDataMgr(Player player, String userId) {
		this.player = player;
		userGameDataHolder = new UserGameDataHolder(userId);
	}

	public void syn(int version) {
		userGameDataHolder.syn(player, version);
	}
	
	@Override
	public void notifyPlayerCreated(Player player) {
//		UserGameData userGameDataTmp = new UserGameData();
//		userGameDataTmp.setUserId(player.getUserId());
//		UserGameDataDao.getInstance().update(userGameDataTmp);
	}

	@Override
	public void notifyPlayerLogin(Player player) {
	}


	@Override
	public void init(Player player) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * 保存数据
	 */
	public boolean flush() {
		userGameDataHolder.flush();
		return true;
	}
	
	/** 早点５点刷新 */
	public void onNewDay5Clock() {
		UserGameData tableUserOther = userGameDataHolder.get();
		tableUserOther.setBuyCoinTimes(0);
		tableUserOther.setBuySkillTimes(0);
		tableUserOther.setBuyPowerTimes(0);
		userGameDataHolder.update(player);
	}
	
	/**** 体力回复 ***/
	public void addPowerByTime(int level) {
		UserGameData userGameData = userGameDataHolder.get();
		long now = new Date().getTime();
		long lastTime = userGameData.getLastAddPowerTime();
		long totalSeconds = (now - lastTime) / 1000;
		int recoverTime = PublicDataCfgDAO.getInstance().getPublicDataValueById(PublicData.ID_POWER_RECOVER_TIME);
		if (totalSeconds < recoverTime) {
			return;
		}
		long extraTime = now % (recoverTime * 1000);
		userGameData.setLastAddPowerTime(now - extraTime);

		RoleUpgradeCfg cfg = (RoleUpgradeCfg) RoleUpgradeCfgDAO.getInstance().getCfgById(String.valueOf(level));
		if (cfg == null) {
			StringBuilder errorReason = new StringBuilder("UserGameDataMgr[addPower]缺少").append(level).append("级的配置，对应表名为：roleUpgrade");
			GameLog.error(LogModule.UserGameData.getName(), userGameData.getUserId(), errorReason.toString(), null);
		}else{
			int maxPower = cfg.getMaxPower();
			int addValue = (int) Math.ceil(totalSeconds / recoverTime);
			int newPower = userGameData.getPower() + addValue;
			if(newPower < maxPower){
				userGameData.setPower(newPower);
			}
		}
		userGameDataHolder.update(player);
	}
	

	public void addPower(int value, int level) {
		UserGameData userGameData = userGameDataHolder.get();
		int newPower = userGameData.getPower() + value;
		RoleUpgradeCfg cfg = (RoleUpgradeCfg) RoleUpgradeCfgDAO.getInstance().getCfgById(String.valueOf(level));
		if (cfg == null) {
			StringBuilder errorReason = new StringBuilder("UserGameDataMgr[addPower]缺少").append(level).append("级的配置，对应表名为：roleUpgrade");
			GameLog.error(LogModule.UserGameData.getName(), userGameData.getUserId(), errorReason.toString(), null);
			return;
		}
		newPower = newPower < cfg.getMostPower() ? newPower:cfg.getMostPower();
		if (newPower < 0) {
			GameLog.info("Player", userGameData.getUserId(), " 扣除体力异常,体力值为负", null);
		}
		userGameData.setPower(newPower);
		userGameDataHolder.update(player);
	}
	
	public int getBuyPowerTimes() {
		return userGameDataHolder.get().getBuyPowerTimes();
	}
	
	public void incBuyPowerTimes() {
		UserGameData tableUserOther = userGameDataHolder.get();
		tableUserOther.setBuyPowerTimes(tableUserOther.getBuyPowerTimes() + 1);
		userGameDataHolder.update(player);
	}
	
	public int addCoin(int nValue) {
		UserGameData tableUserOther = userGameDataHolder.get();
		if (tableUserOther.getCoin() + nValue >= 0) {
			tableUserOther.setCoin(tableUserOther.getCoin() + nValue);
			userGameDataHolder.update(player);
			return 0;
		}
		return -1;
	}
	
	public void addBuyCoinTimes(int nValue) {
		// 记录购买点金手的次数
		UserGameData tableUserOther = userGameDataHolder.get();
		if (tableUserOther.getBuyCoinTimes() + nValue >= 0) {
			tableUserOther.setBuyCoinTimes(tableUserOther.getBuyCoinTimes() + nValue);
			userGameDataHolder.update(player);
		}
	}
	public int getGold() {
		return userGameDataHolder.get().getGold();
	}

	public int addGold(int value) {
		UserGameData tableUserOther = userGameDataHolder.get();
		int left = tableUserOther.getGold() + value;
		if (left >= 0) {
			tableUserOther.setGold(left);
			if(value > 0 ){
				UserGameExtendInfo extendInfo = tableUserOther.getExtendInfo();
				extendInfo.addSendGold(value);
			}			
			userGameDataHolder.update(player);
			
			//消耗日常任务
			if(value<0){
				player.getDailyActivityMgr().AddTaskTimesByType(DailyActivityType.CONST, Math.abs(value));
			}
			return 0;
		}
		return -1;
	}
	
	public void setRecharge(int nValue) {
		UserGameData tableUserOther = userGameDataHolder.get();
		tableUserOther.setRecharge(tableUserOther.getRecharge() + nValue);
		userGameDataHolder.update(player);
	}

	public int getRookieFlag(){
		return userGameDataHolder.get().getRookieFlag();
	}
	
	public int getRecharge(){
		return userGameDataHolder.get().getRecharge();
	}
	
	public String getUserId() {
		return userGameDataHolder.get().getUserId();
	}
	
	public TableUserOtherIF getReadOnly(){
		return userGameDataHolder.get();
	}
	
	public long getCoin() {
		return userGameDataHolder.get().getCoin();
	}
	
	public int getUnendingWarCoin() {
		return userGameDataHolder.get().getUnendingWarCoin();
	}

	public int addUnendingWarCoin(int count) {
		int value = count + getUnendingWarCoin();
		if (value > 0) {
			userGameDataHolder.get().setUnendingWarCoin(value);
			userGameDataHolder.update(player);
			return 0;
		}
		return -1;
	}
	
	public int getTowerCoin() {
		return userGameDataHolder.get().getTowerCoin();
	}

	public int addTowerCoin(int count) {
		int value = count + getTowerCoin();
		if (value > 0) {
			userGameDataHolder.get().setTowerCoin(value);
			userGameDataHolder.update(player);
			return 0;
		}
		return -1;
	}
	
	public int getBuyCoinTimes() {
		return userGameDataHolder.get().getBuyCoinTimes();
	}

	public int getPower() {
		return userGameDataHolder.get().getPower();
	}
	public int getFreeChat() {
		return userGameDataHolder.get().getFreeChat();
	}
	public void setFreeChat(int freeChat) {
		if (freeChat < 0) {
			freeChat = 0;
		}
		userGameDataHolder.get().setFreeChat(freeChat);
		userGameDataHolder.update(player);
	}
	
//	public int addGuildCoin(int count) {
//		int value = count + getGuildCoin();
//		if (value >= 0) {
//			userGameDataHolder.get().setGuildCoin(count);
//			userGameDataHolder.update(player);
//			return 1;
//		}
//		return 0;
//	}
//
//	public int addGuildMaterial(int count) {
//		int value = count + userGameDataHolder.get().getGuildMaterial();
//		if (value >= 0) {
//			userGameDataHolder.get().setGuildMaterial(count);
//			userGameDataHolder.update(player);
//			return 1;
//		}
//		return 0;
//	}
//	
//	public int getGuildCoin() {
//		return userGameDataHolder.get().getGuildCoin();
//	}
//	
//	public String getGuildName() {
//		return userGameDataHolder.get().getGuildName();
//	}
//	
//
//	public void setGuildName(String guildName) {
//		userGameDataHolder.get().setGuildName(guildName);
//		userGameDataHolder.update(player);
//	}
//
//	public String getGuildId() {
//		return userGameDataHolder.get().getGuildId();
//	}
//
//	public void setGuildId(String gulidUid) {
//		userGameDataHolder.get().setGuildId(gulidUid);
//	}
	
	public void setLastChangeInfoTime(long lastChangeInfoTime) {
		userGameDataHolder.get().setLastChangeInfoTime(lastChangeInfoTime);
		userGameDataHolder.update(player);
	}

	public void setRookieFlag(int rookieFlag) {
		userGameDataHolder.get().setRookieFlag(rookieFlag);
		userGameDataHolder.update(player);
	}
	public void setCarrerChangeTime() {
		userGameDataHolder.get().setCarrerChangeTime(System.currentTimeMillis());
		userGameDataHolder.update(player);
	}
	public long getCarrerChangeTime() {
		return userGameDataHolder.get().getCarrerChangeTime();
	}

	public void setUpgradeExp(long upgradeExp) {
		userGameDataHolder.get().setUpgradeExp(upgradeExp);
		userGameDataHolder.update(player);
	}
	

	public void setBuySkillTimes(int times) {
		userGameDataHolder.get().setBuySkillTimes(times);
		userGameDataHolder.update(player);
	}

	public int getBuySkillTimes() {
		return userGameDataHolder.get().getBuySkillTimes();
	}
	

	public void setHeadBox(String box) {
		userGameDataHolder.get().setHeadFrame(box);
		userGameDataHolder.update(player);
	}

	public String getHeadBox() {
		return userGameDataHolder.get().getHeadFrame();
	}
	
	public void addSkillPointCount(int count) {
		UserGameData tableUserOther = userGameDataHolder.get();
		if (tableUserOther.getSkillPointCount() + count >= 0) {
			tableUserOther.setSkillPointCount(tableUserOther.getSkillPointCount() + count);
			userGameDataHolder.update(player);
		}
	}
	public void setSkillPointCount(int count) {
		userGameDataHolder.get().setSkillPointCount(count);
		userGameDataHolder.update(player);
	}
	
	public int getSkillPointCount() {
		return userGameDataHolder.get().getSkillPointCount();
	}
	public long getLastRecoverSkillPointTime() {
		return userGameDataHolder.get().getLastRecoverSkillPointTime();
	}
	
	public long getLastChangeInfoTime() {
		return userGameDataHolder.get().getLastChangeInfoTime();
	}
	public void setLastLoginTime(long time) {
		userGameDataHolder.get().setLastLoginTime(time);
		userGameDataHolder.update(player);
	}
	/** 登陆时间 */
	public long getLastLoginTime() {
		return userGameDataHolder.get().getLastLoginTime();
	}

	public long getLastResetTime() {
		return userGameDataHolder.get().getLastResetTime();
	}

	public void setLastResetTime(long lastResetTime) {
		userGameDataHolder.get().setLastResetTime(lastResetTime);
		userGameDataHolder.update(player);
	}
	public void setLastResetTime5Clock(long lastResetTime5Clock) {
		userGameDataHolder.get().setLastResetTime5Clock(lastResetTime5Clock);
		userGameDataHolder.update(player);
	}
	public long getLastResetTime5Clock() {
		return userGameDataHolder.get().getLastResetTime5Clock();
	}
	
	public int getExpCoin() {
		return userGameDataHolder.get().getExpCoin();
	}
	
	public int addExpCoin(int count) {
		int value = count + getExpCoin();
		if (value > 0) {
			userGameDataHolder.get().setExpCoin(count);
			userGameDataHolder.update(player);
			return 0;
		}
		return -1;
	}

	public int getStrenCoin() {
		return userGameDataHolder.get().getStrenCoin();
	}

	public int addStrenCoin(int count) {
		int value = count + getStrenCoin();
		if (value > 0) {
			userGameDataHolder.get().setStrenCoin(count);
			userGameDataHolder.update(player);
			return 0;
		}
		return -1;
	}
	
	public void setLastRecoverSkillPointTime(long time) {
		userGameDataHolder.get().setLastRecoverSkillPointTime(time);
		userGameDataHolder.update(player);
	}
	
	public int addArenaCoin(int currency) {
		UserGameData tableUserOther = userGameDataHolder.get();
		int gold = tableUserOther.getArenaCoin();
		if (currency < 0 && gold <= 0) {
			return -1;
		}
		int total = gold + currency;
		if (currency < 0 && total < 0) {
			total = 0;
		}
		tableUserOther.setArenaCoin(total);
		userGameDataHolder.update(player);
		return 0;
	}
	public int getArenaCoin() {
		return userGameDataHolder.get().getArenaCoin();
	}
	
	public int addPeakArenaCoin(int currency) {
		UserGameData tableUserOther = userGameDataHolder.get();
		int gold = tableUserOther.getPeakArenaCoin();
		if (currency < 0 && gold <= 0) {
			return -1;
		}
		int total = gold + currency;
		if (currency < 0 && total < 0) {
			total = 0;
		}
		tableUserOther.setPeakArenaCoin(total);
		userGameDataHolder.update(player);
		return 0;
	}

	public int getPeakArenaCoin() {
		return this.userGameDataHolder.get().getPeakArenaCoin();
	}

	
}