package com.rwbase.dao.gamble.pojo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.rwbase.dao.gamble.pojo.cfg.GambleCfg;
import com.rwbase.dao.gamble.pojo.cfg.GambleCfgDAO;
import com.rwbase.dao.gamble.pojo.cfg.GambleRewardCfg;
import com.rwproto.GambleServiceProtos.EGambleType;
import com.rwproto.GambleServiceProtos.ELotteryType;

@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "mt_table_gamble")
public class TableGamble {
	@Id
	private String userId;
	private GambleItem gambleItem = new GambleItem();

	private List<GambleRewardCfg> destinyHot = new ArrayList<GambleRewardCfg>();

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public GambleItem getGambleItem() {
		return gambleItem;
	}

	public void setGambleItem(GambleItem gambleItem) {
		this.gambleItem = gambleItem;
	}

	/** 重置次数 */
	public void resetCount() {
		gambleItem.setSurplusOrdinaryCount(0);
	}

	/** 添加一次(花了钱的) */
	public void addGoldCount(EGambleType type, ELotteryType temp) {
		if (!gambleItem.getGoldCount().containsKey(type)) {
			gambleItem.getGoldCount().put(type, new HashMap<ELotteryType, Integer>());
		}
		gambleItem.getGoldCount().get(type).put(temp, getGoldCount(type, temp) + 1);
	}

	/**
	 * 获取垂钓类型抽奖次数(花了钱的)
	 * 
	 * @param type
	 *            垂钓类型
	 * @param temp
	 *            抽奖类型
	 * @return
	 */
	public int getGoldCount(EGambleType type, ELotteryType temp) {
		if (gambleItem.getGoldCount().containsKey(type)) {
			if (gambleItem.getGoldCount().get(type).containsKey(temp)) {
				return gambleItem.getGoldCount().get(type).get(temp);
			}
		}
		return 0;
	}

	/** 添加一次(免费的) */
	public void addFreeCount(EGambleType type, ELotteryType temp) {
		if (!gambleItem.getFreeCount().containsKey(type)) {
			gambleItem.getFreeCount().put(type, new HashMap<ELotteryType, Integer>());
		}
		gambleItem.getFreeCount().get(type).put(temp, getFreeCount(type, temp) + 1);
	}

	/**
	 * 获取垂钓类型抽奖次数(免费的)
	 * 
	 * @param type
	 *            垂钓类型
	 * @param temp
	 *            抽奖类型
	 * @return
	 */
	public int getFreeCount(EGambleType type, ELotteryType temp) {
		if (gambleItem.getFreeCount().containsKey(type)) {
			if (gambleItem.getFreeCount().get(type).containsKey(temp)) {
				return gambleItem.getFreeCount().get(type).get(temp);
			}
		}
		return 0;
	}

	/** 这次抽奖是否可免费 */
	public boolean isCanFree(EGambleType type, ELotteryType lotterType) {
		switch (lotterType) {
		case ONE:
			switch (type) {
			case PRIMARY:
				if (gambleCfg(type).getDayFreeCount() - gambleItem.getSurplusOrdinaryCount() > 0 && ordinaryTime() - 2 <= 0) {
					return true;
				}
				break;
			case MIDDLE:
				if (prayTime() - 2 <= 0) {// 这里的-2是为了容错，让服务端比客户端快2秒
					return true;
				}
				break;
			default:
				return false;
			}
			break;
		case SIX:
			break;
		case TEN:
			break;
		default:
			break;
		}
		return false;
	}

	/** 添加一次免费使用次数 */
	public void setOneConsumption(EGambleType type) {
		switch (type) {
		case PRIMARY:
			gambleItem.setSurplusOrdinaryCount(gambleItem.getSurplusOrdinaryCount() + 1);
			gambleItem.setLastOrdinaryTime(Calendar.getInstance().getTimeInMillis());
			break;
		case MIDDLE:
			gambleItem.setLastPrayTime(Calendar.getInstance().getTimeInMillis());
			break;
		default:
			break;
		}
	}

	public int ordinaryTime() {
		if (gambleItem.getLastOrdinaryTime() == 0) {
			return 0;
		}
		return (gambleCfg(EGambleType.PRIMARY).getRefreshTime() * 1000 - (int) (Calendar.getInstance().getTimeInMillis() - gambleItem.getLastOrdinaryTime())) / 1000;
	}

	public int prayTime() {
		if (gambleItem.getLastPrayTime() == 0) {
			return 0;
		}
		return (gambleCfg(EGambleType.MIDDLE).getRefreshTime() * 1000 - (int) (Calendar.getInstance().getTimeInMillis() - gambleItem.getLastPrayTime())) / 1000;
	}

	public GambleCfg gambleCfg(EGambleType type) {
		return GambleCfgDAO.getInstance().getGambleCfg(type);
	}

	public List<GambleRewardCfg> getDestinyHot() {
		return destinyHot;
	}

	public void setDestinyHot(List<GambleRewardCfg> destinyHot) {
		this.destinyHot = destinyHot;
	}

}
