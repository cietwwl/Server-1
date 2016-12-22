package com.playerdata;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.bm.player.Observer;
import com.bm.player.ObserverFactory;
import com.bm.player.ObserverFactory.ObserverType;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.group.UserGroupAttributeDataMgr;
import com.playerdata.mgcsecret.manager.MagicSecretMgr;
import com.playerdata.teambattle.manager.UserTeamBattleDataMgr;
import com.rw.service.dailyActivity.Enum.DailyActivityType;
import com.rwbase.common.enu.eSpecialItemId;
import com.rwbase.common.userEvent.UserEventMgr;
import com.rwbase.dao.majorDatas.MajorDataDataHolder;
import com.rwbase.dao.majorDatas.pojo.MajorData;
import com.rwbase.dao.power.PowerInfoDataHolder;
import com.rwbase.dao.power.RoleUpgradeCfgDAO;
import com.rwbase.dao.power.pojo.RoleUpgradeCfg;
import com.rwbase.dao.publicdata.PublicData;
import com.rwbase.dao.publicdata.PublicDataCfgDAO;
import com.rwbase.dao.user.UserGameData;
import com.rwbase.dao.user.UserGameDataHolder;
import com.rwbase.dao.user.readonly.TableUserOtherIF;

public class UserGameDataMgr {

	private UserGameDataHolder userGameDataHolder;
	private MajorDataDataHolder majorDataHolder;
	private Player player;// 角色

	public UserGameDataMgr(Player player, String userId) {
		this.player = player;
		majorDataHolder = new MajorDataDataHolder(userId);
		userGameDataHolder = new UserGameDataHolder(userId);

	}

	public void syn(int version) {
		userGameDataHolder.syn(player, version);
		majorDataHolder.syn(player, version);

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

		tableUserOther.setRandomBossFightCount(0);// 重置随机boss的战斗次数
		tableUserOther.setKillBossRewardCount(0);
		tableUserOther.setCreateBossCount(0);
		userGameDataHolder.update(player);
	}

	/**** 体力回复 ***/
	public void addPowerByTime(int level) {
		UserGameData userGameData = userGameDataHolder.get();

		int recoverTime = PublicDataCfgDAO.getInstance().getPublicDataValueById(PublicData.ID_POWER_RECOVER_TIME);
		RoleUpgradeCfg cfg = (RoleUpgradeCfg) RoleUpgradeCfgDAO.getInstance().getCfgById(String.valueOf(level));

		long now = System.currentTimeMillis();// 当前时间
		if (cfg == null) {
			StringBuilder errorReason = new StringBuilder("UserGameDataMgr[addPower]缺少").append(level).append("级的配置，对应表名为：roleUpgrade");
			GameLog.error(LogModule.UserGameData.getName(), userGameData.getUserId(), errorReason.toString(), null);
			userGameData.setLastAddPowerTime(now);// 上次检查时间是0
			userGameDataHolder.flush();
		} else {
			int curPower = userGameData.getPower();// 当前的体力
			int maxPower = cfg.getMaxPower();// 最大的体力

			long lastTime = userGameData.getLastAddPowerTime();
			if (curPower >= maxPower) {// 已经超过了最大的体力就停止检查
				if (lastTime > 0) {
					userGameData.setLastAddPowerTime(0);
					userGameDataHolder.flush();
				}
			} else {
				if (lastTime <= 0) {
					lastTime = now;
				}

				long flowTime = now - lastTime;// 流失的时间
				if (flowTime <= 0) {// 流失时间小于0
					userGameData.setLastAddPowerTime(now);// 上次检查时间是0
					userGameDataHolder.flush();

					// TODO HC 把改变数据推送到前台
					PowerInfoDataHolder.synPowerInfo(player);
				} else {
					long hasSeconds = TimeUnit.MILLISECONDS.toSeconds(flowTime);// 过了多少秒
					int addValue = (int) Math.ceil(hasSeconds / recoverTime);// 可以增加多少个

					int tempPower = curPower + addValue;// 临时增加到多少体力
					tempPower = tempPower >= maxPower ? maxPower : tempPower;
					if (tempPower != curPower) {
						userGameData.setPower(tempPower);
						if (tempPower < maxPower) {
							userGameData.setLastAddPowerTime(now - TimeUnit.SECONDS.toMillis(hasSeconds - addValue * recoverTime));
						} else {
							userGameData.setLastAddPowerTime(0);
						}
						userGameDataHolder.flush();
						// TODO 这里调用处需要做支持，检测是否存在这里的属性域，否则是不安全和没有可维护性
						userGameDataHolder.update(player, "power");
						// TODO HC 把改变数据推送到前台
						PowerInfoDataHolder.synPowerInfo(player);
					}
				}
			}
		}
	}

	public boolean addPower(int value, int level) {
		UserGameData userGameData = userGameDataHolder.get();
		int newPower = userGameData.getPower() + value;
		RoleUpgradeCfg cfg = (RoleUpgradeCfg) RoleUpgradeCfgDAO.getInstance().getCfgById(String.valueOf(level));
		if (cfg == null) {
			StringBuilder errorReason = new StringBuilder("UserGameDataMgr[addPower]缺少").append(level).append("级的配置，对应表名为：roleUpgrade");
			GameLog.error(LogModule.UserGameData.getName(), userGameData.getUserId(), errorReason.toString(), null);
			return false;
		}
		newPower = newPower < cfg.getMostPower() ? newPower : cfg.getMostPower();
		if (newPower < 0) {
			GameLog.info("Player", userGameData.getUserId(), " 扣除体力异常,体力值为负", null);
		}
		userGameData.setPower(newPower);
		userGameDataHolder.update(player);
		// TODO HC 把改变数据推送到前台
		PowerInfoDataHolder.synPowerInfo(player);
		return true;
	}

	public int getBuyPowerTimes() {
		return userGameDataHolder.get().getBuyPowerTimes();
	}

	public void incBuyPowerTimes() {
		UserGameData tableUserOther = userGameDataHolder.get();
		tableUserOther.setBuyPowerTimes(tableUserOther.getBuyPowerTimes() + 1);
		userGameDataHolder.update(player);
	}

	// public int addCoin(int nValue) {
	// UserGameData tableUserOther = userGameDataHolder.get();
	// if (tableUserOther.getCoin() + nValue >= 0) {
	// tableUserOther.setCoin(tableUserOther.getCoin() + nValue);
	// userGameDataHolder.update(player);
	//
	// String scenceId = null;// 暂时留空
	// ItemChangedEventType_1 type_1 = null; // 暂时留空
	// ItemChangedEventType_2 type_2 = null;// 暂时留空
	// BILogMgr.getInstance().logCoinChanged(player, scenceId, type_1, type_2,
	// nValue, tableUserOther.getCoin());
	// if(nValue < 0){
	// UserEventMgr.getInstance().coinSpendDaily(player, -nValue);
	// }
	// return 0;
	// }
	// return -1;
	// }

	public int addCoin(int nValue) {
		MajorData marjorData = majorDataHolder.getMarjorData();
		if (marjorData.getCoin() + nValue >= 0) {
			marjorData.setCoin(marjorData.getCoin() + nValue);
			majorDataHolder.addCoin(player, marjorData);

			if (nValue < 0) {
				UserEventMgr.getInstance().coinSpendDaily(player, -nValue);
			}
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
		return majorDataHolder.getMarjorData().getGold();
	}

	public int getGiftGold() {
		return majorDataHolder.getMarjorData().getGiftGold();
	}

	public int getChargeGold() {
		return majorDataHolder.getMarjorData().getChargeGold();
	}

	public boolean isGoldEngough(int value) {
		if (value > 0) {
			return true;
		}

		MajorData marjorData = majorDataHolder.getMarjorData();

		int giftGold = marjorData.getGiftGold();
		int chargeGold = marjorData.getChargeGold();
		boolean hasEngoughGold = giftGold + chargeGold + value >= 0;
		return hasEngoughGold;

	}

	public boolean isCoinEnough(int value) {
		if (value > 0) {
			return true;
		}
		MajorData marjorData = majorDataHolder.getMarjorData();
		long curCoin = marjorData.getCoin();
		return curCoin + value >= 0;
	}

	// public int addGold(int value) {
	// UserGameData tableUserOther = userGameDataHolder.get();
	// int result = 0;
	// if (value >= 0) {
	// // 加钻石
	// result = incrGold(tableUserOther, value);
	// } else {
	// // 扣钻石
	// result = decrGold(tableUserOther, value);
	// // 消耗日常任务
	// if (result == 0) {
	// player.getDailyActivityMgr().AddTaskTimesByType(DailyActivityType.CONST,
	// Math.abs(value));
	// }
	// }
	//
	// if (result == 0) {
	// userGameDataHolder.update(player);
	// }
	//
	// return result;
	// }

	public int addGoldByGm(int value) {
		int result = 0;
		MajorData marjorData = majorDataHolder.getMarjorData();
		if (value >= 0) {
			// 加钻石
			result = incrGold(marjorData, value);
		} else {
			// 扣钻石
			result = decrGoldByGm(marjorData, value);
			// 消耗日常任务
			if (result == 0) {
				// player.getDailyActivityMgr().AddTaskTimesByType(DailyActivityType.CONST,
				// Math.abs(value));
			}
		}

		if (result == 0) {
			majorDataHolder.addGold(player, marjorData);
		}
		return result;
	}

	public int addGold(int value) {
		MajorData marjorData = majorDataHolder.getMarjorData();
		int result = 0;
		if (value >= 0) {
			// 加钻石
			result = incrGold(marjorData, value);
		} else {
			// 扣钻石
			result = decrGold(marjorData, value);
			// 消耗日常任务
			if (result == 0) {
				player.getDailyActivityMgr().AddTaskTimesByType(DailyActivityType.CONST, Math.abs(value));
			}
		}

		if (result == 0) {
			majorDataHolder.addGold(player, marjorData);
		}

		return result;
	}

	// // 增加钻石
	// private int incrGold(UserGameData tableUserOther, int value) {
	//
	// tableUserOther.setGiftGold(tableUserOther.getGiftGold() + value);
	// tableUserOther.updateGold();
	//
	// String scenceId = null;// 暂时留空
	// ItemChangedEventType_1 type_1 = null; // 暂时留空
	// ItemChangedEventType_2 type_2 = null;// 暂时留空
	// BILogMgr.getInstance().logGiftGoldChanged(player, scenceId, type_1,
	// type_2, value, tableUserOther.getGiftGold());
	//
	// return 0;
	// }

	// 增加钻石
	private int incrGold(MajorData majordata, int value) {

		majordata.setGiftGold(majordata.getGiftGold() + value);
		majordata.updateGold();

		return 0;
	}

	// // 消费钻石
	// private int decrGold(UserGameData tableUserOther, int value) {
	// // 先消费赠送货币，再消费充值货币
	// int giftGold = tableUserOther.getGiftGold();
	// int chargeGold = tableUserOther.getChargeGold();
	// boolean hasEngoughGold = giftGold + chargeGold + value >= 0;
	// int result = -1;
	// int giftGoldChanged = 0;
	// if (hasEngoughGold) {
	// if (giftGold + value >= 0) {
	// tableUserOther.setGiftGold(giftGold + value);
	// giftGoldChanged = value;
	// } else {
	// tableUserOther.setGiftGold(0);
	// giftGoldChanged = -tableUserOther.getGiftGold();
	//
	// int chargeLeft = giftGold + chargeGold + value;
	// tableUserOther.setChargeGold(chargeLeft);
	// }
	// tableUserOther.updateGold();
	// result = 0;
	// } else {
	// result = -1;
	// }
	// if (result == 0) {
	// String scenceId = null;// 暂时留空
	// ItemChangedEventType_1 type_1 = null; // 暂时留空
	// ItemChangedEventType_2 type_2 = null;// 暂时留空
	// BILogMgr.getInstance().logGiftGoldChanged(player, scenceId, type_1,
	// type_2, giftGoldChanged, tableUserOther.getGiftGold());
	// UserEventMgr.getInstance().UseGold(player, -value);
	// }
	// return result;
	// }

	// 消费钻石
	private int decrGold(MajorData majordata, int value) {
		// 先消费赠送货币，再消费充值货币
		int giftGold = majordata.getGiftGold();
		int chargeGold = majordata.getChargeGold();
		boolean hasEngoughGold = giftGold + chargeGold + value >= 0;
		int result = -1;
		int giftGoldChanged = 0;
		if (hasEngoughGold) {
			if (giftGold + value >= 0) {
				majordata.setGiftGold(giftGold + value);
				giftGoldChanged = value;
			} else {
				majordata.setGiftGold(0);
				giftGoldChanged = -majordata.getGiftGold();

				int chargeLeft = giftGold + chargeGold + value;
				majordata.setChargeGold(chargeLeft);
			}
			majordata.updateGold();
			result = 0;
		} else {
			result = -1;
		}
		if (result == 0) {
			UserEventMgr.getInstance().UseGold(player, -value);
		}
		return result;
	}

	// GM命令扣除钻石
	private int decrGoldByGm(MajorData majordata, int value) {
		// 先消费赠送货币，再消费充值货币
		int giftGold = majordata.getGiftGold();
		int chargeGold = majordata.getChargeGold();
		boolean hasEngoughGold = giftGold + chargeGold + value >= 0;
		int result = -1;
		int giftGoldChanged = 0;
		if (hasEngoughGold) {
			if (giftGold + value >= 0) {
				majordata.setGiftGold(giftGold + value);
				giftGoldChanged = value;
			} else {
				majordata.setGiftGold(0);
				giftGoldChanged = -majordata.getGiftGold();

				int chargeLeft = giftGold + chargeGold + value;
				majordata.setChargeGold(chargeLeft);
			}
			majordata.updateGold();
			result = 0;
		} else {
			result = -1;
		}

		return result;
	}

	// public void addReCharge(int addNum) {
	// UserGameData tableUserOther = userGameDataHolder.get();
	// tableUserOther.setChargeGold(tableUserOther.getChargeGold() + addNum);
	// tableUserOther.updateGold();
	// userGameDataHolder.update(player);
	// }

	public void addReCharge(int addNum) {
		MajorData marjorData = majorDataHolder.getMarjorData();
		marjorData.setChargeGold(marjorData.getChargeGold() + addNum);
		marjorData.updateGold();
		majorDataHolder.addChargeGold(player, marjorData);
	}

	public int getRookieFlag() {
		return userGameDataHolder.get().getRookieFlag();
	}

	public String getUserId() {
		return userGameDataHolder.get().getUserId();
	}

	public TableUserOtherIF getReadOnly() {
		return userGameDataHolder.get();
	}

	public long getCoin() {
		if (majorDataHolder.getMarjorData() == null) {
			return 0;
		} else {
			return majorDataHolder.getMarjorData().getCoin();
		}
	}

	public int getMagicSecretCoin() {
		return MagicSecretMgr.getInstance().getSecretGold(player);
	}

	public int addMagicSecretCoin(int count) {
		if (MagicSecretMgr.getInstance().addSecretGold(player, count))
			return 0;
		return -1;
	}

	public int getTeamBattleCoin() {
		return UserTeamBattleDataMgr.getInstance().getTeamBattleCoin(player);
	}

	public int addTeamBattleCoin(int count) {
		if (UserTeamBattleDataMgr.getInstance().addTeamBattleCoin(player, count))
			return 0;
		return -1;
	}

	public int getTowerCoin() {
		return userGameDataHolder.get().getTowerCoin();
	}

	public int addTowerCoin(int count) {
		int value = count + getTowerCoin();
		if (value >= 0) {
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

	// public int addGuildCoin(int count) {
	// int value = count + getGuildCoin();
	// if (value >= 0) {
	// userGameDataHolder.get().setGuildCoin(count);
	// userGameDataHolder.update(player);
	// return 1;
	// }
	// return 0;
	// }
	//
	// public int addGuildMaterial(int count) {
	// int value = count + userGameDataHolder.get().getGuildMaterial();
	// if (value >= 0) {
	// userGameDataHolder.get().setGuildMaterial(count);
	// userGameDataHolder.update(player);
	// return 1;
	// }
	// return 0;
	// }
	//
	// public int getGuildCoin() {
	// return userGameDataHolder.get().getGuildCoin();
	// }
	//
	// public String getGuildName() {
	// return userGameDataHolder.get().getGuildName();
	// }
	//
	//
	// public void setGuildName(String guildName) {
	// userGameDataHolder.get().setGuildName(guildName);
	// userGameDataHolder.update(player);
	// }
	//
	// public String getGuildId() {
	// return userGameDataHolder.get().getGuildId();
	// }
	//
	// public void setGuildId(String gulidUid) {
	// userGameDataHolder.get().setGuildId(gulidUid);
	// }

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

		RankingMgr.getInstance().onPlayerChange(player);
		// 通知一下监听的人，修改对应数据
		Observer observer = ObserverFactory.getInstance().getObserver(ObserverType.PLAYER_CHANER);
		if (observer != null) {
			observer.playerChangeHeadBox(player);
		}
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

	public int addWakenPiece(int value) {
		UserGameData tableUserOther = userGameDataHolder.get();
		int wakenPiece = tableUserOther.getWakenPiece();
		if (value < 0 && wakenPiece <= 0) {
			return -1;
		}
		int total = wakenPiece + value;
		if (value < 0 && total < 0) {
			total = 0;
		}
		tableUserOther.setWakenPiece(total);
		userGameDataHolder.update(player);
		return 0;
	}

	public int getWakenPiece() {
		return userGameDataHolder.get().getWakenPiece();
	}

	public int addWakenKey(int value) {
		UserGameData tableUserOther = userGameDataHolder.get();
		int wakenKey = tableUserOther.getWakenKey();
		if (value < 0 && wakenKey <= 0) {
			return -1;
		}
		int total = wakenKey + value;
		if (value < 0 && total < 0) {
			total = 0;
		}
		tableUserOther.setWakenKey(total);
		userGameDataHolder.update(player);
		return 0;
	}

	public int getWakenKey() {
		return userGameDataHolder.get().getWakenKey();
	}

	public boolean addPeakArenaCoin(int currency) {
		UserGameData tableUserOther = userGameDataHolder.get();
		int gold = tableUserOther.getPeakArenaCoin();
		if (currency < 0 && gold <= 0) {
			return false;
		}
		int total = gold + currency;
		if (currency < 0 && total < 0) {
			total = 0;
		}
		tableUserOther.setPeakArenaCoin(total);
		userGameDataHolder.update(player);
		return true;
	}

	public int getPeakArenaCoin() {
		return this.userGameDataHolder.get().getPeakArenaCoin();
	}

	public long getLastWorshipTime() {
		return this.userGameDataHolder.get().getLastWorshipTime();
	}

	public void setLastWorshipTime(long lastWorshipTime) {
		this.userGameDataHolder.get().setLastWorshipTime(lastWorshipTime);
	}

	// public int getFightingAll() {
	// return this.userGameDataHolder.get().getFightingAll();
	// }
	//
	// public void setFightingAll(int fightingAll) {
	// UserGameData gameData = this.userGameDataHolder.get();
	// int pre = gameData.getFightingAll();
	// gameData.setFightingAll(fightingAll);
	// if (pre != gameData.getFightingAll()) {
	// this.userGameDataHolder.update(player);
	// }
	// }

	public void setMapAnimationState(MapAnimationState animationState) {
		userGameDataHolder.get().setMapAnimationState(animationState);
		userGameDataHolder.update(player);
	}

	// public void notifySingleFightingChange(int newSingleValue, int preSingleValue) {
	// UserGameData gameData = this.userGameDataHolder.get();
	// int pre = gameData.getFightingAll();
	// gameData.notifySingleFightingChange(newSingleValue, preSingleValue);
	// if (pre != gameData.getFightingAll()) {
	// this.userGameDataHolder.update(player);
	// }
	// }
	//
	// public void increaseFightingAll(int value) {
	// this.userGameDataHolder.get().increaseFightingAll(value);
	// this.userGameDataHolder.update(player);
	// }
	//
	// public int getStarAll() {
	// return userGameDataHolder.get().getStarAll();
	// }
	//
	// public void setStarAll(int pStarAll) {
	// UserGameData gameData = this.userGameDataHolder.get();
	// int pre = gameData.getStarAll();
	// gameData.setStarAll(pStarAll);
	// if (pre != gameData.getStarAll()) {
	// this.userGameDataHolder.update(player);
	// }
	// }
	//
	// public void increaseStarAll(int value) {
	// this.userGameDataHolder.get().increaseStarAll(value);
	// this.userGameDataHolder.update(player);
	// }
	//
	// public void notifySingleStarChange(int newStarLv, int preStarLv) {
	// if(newStarLv == preStarLv) {
	// return;
	// }
	// UserGameData gameData = this.userGameDataHolder.get();
	// int pre = gameData.getFightingAll();
	// gameData.notifySingleStarChange(newStarLv, preStarLv);
	// if (pre != gameData.getStarAll()) {
	// this.userGameDataHolder.update(player);
	// }
	// }

	/**
	 * 扣除某种货币
	 * 
	 * @param currencyType 货币类型，参考 item.xlsx SpecialItem表 不支持增加经验，因为需要额外增加英雄ID！
	 * @param count 扣除数量，必须是非负数
	 * @return 操作成功还是失败
	 */
	public boolean deductCurrency(eSpecialItemId currencyType, int count) {
		boolean result = false;
		if (isEnoughCurrency(currencyType, count)) {
			result = directDeduct(currencyType, count);
		}
		return result;
	}

	/**
	 * 直接扣钱，不检查是否够钱！
	 * 
	 * @param currencyType
	 * @param count
	 * @param result
	 * @return
	 */
	private boolean directDeduct(eSpecialItemId currencyType, int count) {
		boolean result = false;
		int dec = -count;
		switch (currencyType) {
		case Coin:
			result = this.addCoin(dec) == 0;
			break;
		case Gold:
			result = this.addGold(dec) == 0;
			break;
		case Power:
			result = player.addPower(dec);
			break;
		case PlayerExp:
			result = false;
			break;
		case ArenaCoin:
			result = this.addArenaCoin(dec) == 0;
			break;
		case BraveCoin:
			result = this.addTowerCoin(dec) == 0;
			break;
		// case GuildCoin:
		// GuildUserMgr m_GuildUserMgr = player.getGuildUserMgr();
		// result = m_GuildUserMgr.addGuildCoin(dec) == 1;
		// break;
		case PeakArenaCoin:
			result = this.addPeakArenaCoin(dec);
			break;
		case MagicSecretCoin:
			result = this.addMagicSecretCoin(dec) == 0;
			break;
		case TEAM_BATTLE_GOLD:
			result = this.addTeamBattleCoin(dec) == 0;
			break;
		default:
			break;
		}
		return result;
	}

	/**
	 * 返回－1表示没有该货币
	 * 
	 * @param currencyType
	 * @return
	 */
	public long getCurrencyNum(eSpecialItemId currencyType) {
		long old = -1;

		switch (currencyType) {
		case Coin:
			old = this.getCoin();
			break;
		case Gold:
			old = this.getGold();
			break;
		case Power:
			old = this.getPower();
			break;
		case PlayerExp:
			old = player.getExp();
			break;
		case ArenaCoin:
			old = this.getArenaCoin();
			break;
		case BraveCoin:
			old = this.getTowerCoin();
			break;
		case GuildCoin:
			old = UserGroupAttributeDataMgr.getMgr().getUserGroupContribution(player.getUserId());
			break;
		case PeakArenaCoin:
			old = this.getPeakArenaCoin();
			break;
		case MagicSecretCoin:
			old = this.getMagicSecretCoin();
			break;
		case TEAM_BATTLE_GOLD:
			old = this.getTeamBattleCoin();
			break;
		default:
			break;
		}
		return old;
	}

	public boolean isEnoughCurrency(eSpecialItemId currencyType, int count) {
		boolean result = false;
		if (count < 0)
			return result;

		long old = getCurrencyNum(currencyType);
		if (old == -1)
			return result;

		return old >= count;
	}

	public List<String> getRandomBossIDs() {
		return userGameDataHolder.get().getRandomBossIds();
	}

	public int getFightRandomBossCount() {
		return userGameDataHolder.get().getRandomBossFightCount();
	}

	public int getKillBossRewardCount() {
		return userGameDataHolder.get().getKillBossRewardCount();
	}

	public int getCreateBossCount() {
		return userGameDataHolder.get().getCreateBossCount();
	}

	public void increaseRandomBossFightCount() {
		UserGameData data = userGameDataHolder.get();
		int count = data.getRandomBossFightCount();
		data.setRandomBossFightCount(count + 1);
		userGameDataHolder.update(player);
	}

	public void addRandomBoss(String id) {
		UserGameData data = userGameDataHolder.get();
		int count = data.getCreateBossCount();
		data.setCreateBossCount(count + 1);
		List<String> list = data.getRandomBossIds();
		list.add(id);
		userGameDataHolder.update(player);
	}

	public void addRBWithoutIncrease(String id) {
		UserGameData data = userGameDataHolder.get();
		List<String> list = data.getRandomBossIds();
		list.add(id);
		userGameDataHolder.update(player);
	}

	public void increaseBossRewardCount() {
		UserGameData data = userGameDataHolder.get();
		int count = data.getKillBossRewardCount();
		data.setKillBossRewardCount(count + 1);
		userGameDataHolder.update(player);
	}

	public void updatePeakArenaScore(int score) {
		UserGameData data = userGameDataHolder.get();
		data.setPeakArenaScore(score);
		if (score > 0) {
			userGameDataHolder.update(player, "peakArenaScore");
		} else {
			userGameDataHolder.update(player);
		}
	}
}