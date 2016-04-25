package com.playerdata;

import java.util.concurrent.TimeUnit;

import com.log.GameLog;
import com.log.LogModule;
import com.rw.service.dailyActivity.Enum.DailyActivityType;
import com.rw.service.log.BILogMgr;
import com.rw.service.log.template.ItemChangedEventType_1;
import com.rw.service.log.template.ItemChangedEventType_2;
import com.rwbase.common.enu.eSpecialItemId;
import com.rwbase.common.userEvent.UserEventMgr;
import com.rwbase.dao.power.RoleUpgradeCfgDAO;
import com.rwbase.dao.power.pojo.RoleUpgradeCfg;
import com.rwbase.dao.publicdata.PublicData;
import com.rwbase.dao.publicdata.PublicDataCfgDAO;
import com.rwbase.dao.user.UserGameData;
import com.rwbase.dao.user.UserGameDataHolder;
import com.rwbase.dao.user.readonly.TableUserOtherIF;

public class UserGameDataMgr {

	private UserGameDataHolder userGameDataHolder;
	private Player player;// 角色

	public UserGameDataMgr(Player player, String userId) {
		this.player = player;
		userGameDataHolder = new UserGameDataHolder(userId);
	}

	public void syn(int version) {
		userGameDataHolder.syn(player, version);
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

		int recoverTime = PublicDataCfgDAO.getInstance().getPublicDataValueById(PublicData.ID_POWER_RECOVER_TIME);
		RoleUpgradeCfg cfg = (RoleUpgradeCfg) RoleUpgradeCfgDAO.getInstance().getCfgById(String.valueOf(level));

		long now = System.currentTimeMillis();// 当前时间
		if (cfg == null) {
			StringBuilder errorReason = new StringBuilder("UserGameDataMgr[addPower]缺少").append(level).append("级的配置，对应表名为：roleUpgrade");
			GameLog.error(LogModule.UserGameData.getName(), userGameData.getUserId(), errorReason.toString(), null);
			userGameData.setLastAddPowerTime(now);// 上次检查时间是0
		} else {
			int curPower = userGameData.getPower();// 当前的体力
			int maxPower = cfg.getMaxPower();// 最大的体力

			if (curPower >= maxPower) {// 已经超过了最大的体力就停止检查
				userGameData.setLastAddPowerTime(now);// 上次检查时间是0
			} else {
				long lastTime = userGameData.getLastAddPowerTime();
				long flowTime = now - lastTime;// 流失的时间
				if (flowTime <= 0) {// 流失时间小于0
					userGameData.setLastAddPowerTime(now);// 上次检查时间是0
				} else {
					long hasSeconds = TimeUnit.MILLISECONDS.toSeconds(flowTime);// 过了多少秒
					int addValue = (int) Math.ceil(hasSeconds / recoverTime);// 可以增加多少个
					int tempPower = curPower + addValue;// 临时增加到多少体力
					tempPower = tempPower >= maxPower ? maxPower : tempPower;
					if (tempPower != curPower) {
						userGameData.setPower(tempPower);
						userGameData.setLastAddPowerTime(now - TimeUnit.SECONDS.toMillis(hasSeconds - addValue * recoverTime));
						// TODO 这里调用处需要做支持，检测是否存在这里的属性域，否则是不安全和没有可维护性
						userGameDataHolder.update(player, "power");
						// TODO HC 把改变数据推送到前台
						player.synPowerInfo();
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

	public int addCoin(int nValue) {
		UserGameData tableUserOther = userGameDataHolder.get();
		if (tableUserOther.getCoin() + nValue >= 0) {
			tableUserOther.setCoin(tableUserOther.getCoin() + nValue);
			userGameDataHolder.update(player);

			String scenceId = null;// 暂时留空
			ItemChangedEventType_1 type_1 = null; // 暂时留空
			ItemChangedEventType_2 type_2 = null;// 暂时留空
			BILogMgr.getInstance().logCoinChanged(player, scenceId, type_1, type_2, nValue, tableUserOther.getCoin());

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

	public int getGiftGold() {
		return userGameDataHolder.get().getGiftGold();
	}

	public int getChargeGold() {
		return userGameDataHolder.get().getChargeGold();
	}

	public int addGold(int value) {
		UserGameData tableUserOther = userGameDataHolder.get();
		int result = 0;
		if (value >= 0) {
			// 加钻石
			result = incrGold(tableUserOther, value);
		} else {
			// 扣钻石
			result = decrGold(tableUserOther, value);
			// 消耗日常任务
			if (result == 0) {
				player.getDailyActivityMgr().AddTaskTimesByType(DailyActivityType.CONST, Math.abs(value));
			}
		}

		if (result == 0) {
			userGameDataHolder.update(player);
		}

		return result;
	}

	// 增加钻石
	private int incrGold(UserGameData tableUserOther, int value) {

		tableUserOther.setGiftGold(tableUserOther.getGiftGold() + value);
		tableUserOther.updateGold();

		String scenceId = null;// 暂时留空
		ItemChangedEventType_1 type_1 = null; // 暂时留空
		ItemChangedEventType_2 type_2 = null;// 暂时留空
		BILogMgr.getInstance().logGiftGoldChanged(player, scenceId, type_1, type_2, value, tableUserOther.getGiftGold());

		return 0;
	}

	// 消费钻石
	private int decrGold(UserGameData tableUserOther, int value) {
		// 先消费赠送货币，再消费充值货币
		int giftGold = tableUserOther.getGiftGold();
		int chargeGold = tableUserOther.getChargeGold();
		boolean hasEngoughGold = giftGold + chargeGold + value >= 0;
		int result = -1;
		int giftGoldChanged = 0;
		if (hasEngoughGold) {
			if (giftGold + value >= 0) {
				tableUserOther.setGiftGold(giftGold + value);
				giftGoldChanged = value;
			} else {
				tableUserOther.setGiftGold(0);
				giftGoldChanged = -tableUserOther.getGiftGold();

				int chargeLeft = giftGold + chargeGold + value;
				tableUserOther.setChargeGold(chargeLeft);
			}
			tableUserOther.updateGold();
			result = 0;
		} else {
			result = -1;
		}
		if (result == 0) {
			String scenceId = null;// 暂时留空
			ItemChangedEventType_1 type_1 = null; // 暂时留空
			ItemChangedEventType_2 type_2 = null;// 暂时留空
			BILogMgr.getInstance().logGiftGoldChanged(player, scenceId, type_1, type_2, giftGoldChanged, tableUserOther.getGiftGold());
			UserEventMgr.getInstance().UseGold(player, -giftGoldChanged);
		}
		return result;
	}

	public void addReCharge(int addNum) {
		UserGameData tableUserOther = userGameDataHolder.get();
		tableUserOther.setChargeGold(tableUserOther.getChargeGold() + addNum);
		tableUserOther.updateGold();
		userGameDataHolder.update(player);
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

	public long getLastWorshipTime() {
		return this.userGameDataHolder.get().getLastWorshipTime();
	}

	public void setLastWorshipTime(long lastWorshipTime) {
		this.userGameDataHolder.get().setLastWorshipTime(lastWorshipTime);
	}

	/**
	 * 扣除某种货币
	 * 
	 * @param currencyType 货币类型，参考 item.xlsx SpecialItem表 不支持增加经验，因为需要额外增加英雄ID！
	 * @param count 扣除数量，必须是非负数
	 * @return 操作成功还是失败
	 */
	public boolean deductCurrency(eSpecialItemId currencyType, int count) {
		boolean result = false;
		if (count < 0)
			return result;

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
			GuildUserMgr m_GuildUserMgr = player.getGuildUserMgr();
			old = m_GuildUserMgr.getGuildCoin();
			break;
		case PeakArenaCoin:
			old = this.getPeakArenaCoin();
			break;
		case UnendingWarCoin:
			old = this.getUnendingWarCoin();
			break;
		default:
			break;
		}
		if (old == -1)
			return result;

		if (old >= count) {
			// 扣钱
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
			case GuildCoin:
				GuildUserMgr m_GuildUserMgr = player.getGuildUserMgr();
				result = m_GuildUserMgr.addGuildCoin(dec) == 1;
				break;
			case PeakArenaCoin:
				old = this.getPeakArenaCoin();
				result = this.addPeakArenaCoin(dec) == 0;
				break;
			case UnendingWarCoin:
				result = this.addUnendingWarCoin(dec) == 0;
				break;
			default:
				break;
			}
		}

		return result;
	}
}