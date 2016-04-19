package com.playerdata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.log.GameLog;
import com.playerdata.common.PlayerEventListener;
import com.rw.service.dailyActivity.Enum.DailyActivityType;
import com.rwbase.common.enu.eSpecialItemId;
import com.rwbase.dao.gamble.GambleUtils;
import com.rwbase.dao.gamble.TableGambleDAO;
import com.rwbase.dao.gamble.pojo.EGambleWeight;
import com.rwbase.dao.gamble.pojo.TableGamble;
import com.rwbase.dao.gamble.pojo.cfg.GambleCfg;
import com.rwbase.dao.gamble.pojo.cfg.GambleCfgDAO;
import com.rwbase.dao.gamble.pojo.cfg.GambleRewardCfg;
import com.rwbase.dao.gamble.pojo.cfg.GambleRewardCfgDAO;
import com.rwbase.dao.hotPoint.EHotPointType;
import com.rwproto.GambleServiceProtos.EGambleType;
import com.rwproto.GambleServiceProtos.ELotteryType;
import com.rwproto.GambleServiceProtos.GambleRewardData;

public class GambleMgr implements PlayerEventListener {

	// private TableGamble tableGamble;
	private TableGambleDAO gambleDAO = TableGambleDAO.getInstance();
	private String userId;
	// private List<GambleRewardCfg> destinyHot;

	private EGambleType[] gambleTypes = { EGambleType.ADVANCED, EGambleType.MIDDLE, EGambleType.PRIMARY };
	private ELotteryType[] lotteryTypes = { ELotteryType.ONE, ELotteryType.SIX, ELotteryType.TEN };

	private Player m_pPlayer = null;

	// 初始化
	public void init(Player pOwner) {
		m_pPlayer = pOwner;
		this.userId = pOwner.getUserId();
		return;
	}

	@Override
	public void notifyPlayerCreated(Player player) {
		
	}

	@Override
	public void notifyPlayerLogin(Player player) {
	}

	public void syncGamble() {
		if (!getHasFree()) {
			HotPointMgr.changeHotPointState(m_pPlayer.getUserId(), EHotPointType.Gamble, false);
		} else {
			HotPointMgr.changeHotPointState(m_pPlayer.getUserId(), EHotPointType.Gamble, true);
		}
	}

	/** 每分钟执行 */
	public static void minutesUpdate() {
		// List<Player> list = new ArrayList<Player>(); // ;
		// list.addAll(PlayerMgr.getInstance().getAllPlayer().values());
		// for (Player player : list) {
		// if (player != null) {
		// if (player.getGambleMgr().getHasFree()) {
		// HotPointMgr.changeHotPointState(player.getUserId(),
		// EHotPointType.Gamble, true);
		// }
		// }
		// }
	}

	/** 是否有免费次数 */
	public boolean getHasFree() {
		TableGamble gambleItem = getGambleItem();
		for (int i = 0; i < gambleTypes.length; i++) {
			for (int j = 0; j < lotteryTypes.length; j++) {
				if (gambleItem.isCanFree(gambleTypes[i], lotteryTypes[j])) {
					return true;
				}
			}
		}
		return false;
	}

	/** 5点时重置数据 */
	public void resetDestinyHot() {
		InitDestinyHot();
		getGambleItem().resetCount();
	}

	private void InitDestinyHot() {
		TableGamble tableGamble = getGambleItem();
		if (tableGamble == null) {
			GameLog.error("GambleMgr", "#InitDestinyHot()", "find TableGamble fail:"+userId);
			return;
		}
		List<GambleRewardCfg> destinyHot = new ArrayList<GambleRewardCfg>();

		List<GambleRewardCfg> list = GambleRewardCfgDAO.getInstance().getWeightGroup(EGambleWeight.MAIN_HOT, EGambleType.ADVANCED);
		int random = (int) (Math.random() * list.size());
		destinyHot.add(list.get(random));

		list = GambleRewardCfgDAO.getInstance().getWeightGroup(EGambleWeight.MINOR_HOT, EGambleType.ADVANCED);
		for (int i = 0; i < 3; i++) {
			random = (int) (Math.random() * list.size());
			destinyHot.add(list.get(random));
		}
		tableGamble.setDestinyHot(list);
		TableGambleDAO.getInstance().update(tableGamble);
	}

	/**
	 * 获取命运热点组 索引0为主要热点其它次要热点
	 */
	public List<GambleRewardCfg> getDestinyHot() {
		return gambleDAO.get(userId).getDestinyHot();
	}

	private boolean setGambleDeduct(EGambleType gambleType, ELotteryType lotterType) {
		return getCanGamble(m_pPlayer, gambleType, lotterType, true);
	}

	/**
	 * 判断是否可抽奖
	 * 
	 * @param gambleType
	 *            垂钓类型
	 * @param lotterType
	 *            抽奖类型
	 * @param isSuccessDeduct
	 *            如果可抽是否直接扣除相应金钱
	 * @return
	 */
	private boolean getCanGamble(Player player, EGambleType gambleType, ELotteryType lotterType, boolean isSuccessDeduct) {
		boolean result = false;
		GambleCfg cfg = GambleCfgDAO.getInstance().getGambleCfg(gambleType);
		int moneyCount = 0;
		switch (lotterType) {
		case ONE:
			moneyCount = cfg.getMoneyNum();
			break;
		case SIX:
			moneyCount = cfg.getMoneyNum();
			break;
		case TEN:
			moneyCount = (int) ((cfg.getMoneyNum() * 10) * 0.9);
			break;
		}
		TableGamble gambleItem = getGambleItem();
		if (gambleItem.isCanFree(gambleType, lotterType)) {// 有免费
			result = true;
			if (isSuccessDeduct) {
				gambleItem.setOneConsumption(gambleType);
			}
		} else {
			long count = player.getReward(eSpecialItemId.getDef(cfg.getMoneyType()));
			if (count >= moneyCount) {// 钱币足够
				result = true;
				if (isSuccessDeduct) {// 直接扣除
					player.getItemBagMgr().addItem(cfg.getMoneyType(), -moneyCount);
				}
			}
		}
		return result;
	}

	public boolean getCanGamble(Player player, EGambleType gambleType, ELotteryType lotterType) {
		return getCanGamble(player, gambleType, lotterType, false);
	}

	/** 获取抽奖列表 */
	public List<GambleRewardData> getRewardResult(EGambleType gambleType, ELotteryType lotterType) {
		List<GambleRewardData> rewardList;
		switch (lotterType) {
		case ONE:
			rewardList = getOneLotteryList(gambleType);
			break;
		case SIX:
			rewardList = getSixLotteryList(gambleType);
			break;
		case TEN:
			rewardList = getTenLotteryList(gambleType);
			break;
		default:
			rewardList = new ArrayList<GambleRewardData>();
			break;
		}

		m_pPlayer.getDailyActivityMgr().AddTaskTimesByType(DailyActivityType.Altar, rewardList.size());
		TableGamble gambleItem = getGambleItem();
		// 添加记录次数
		if (gambleItem.isCanFree(gambleType, lotterType)) {
			gambleItem.addFreeCount(gambleType, lotterType);
		} else {
			gambleItem.addGoldCount(gambleType, lotterType);
		}
		setGambleDeduct(gambleType, lotterType);

		if (!getHasFree()) {
			HotPointMgr.changeHotPointState(m_pPlayer.getUserId(), EHotPointType.Gamble, false);
		}

		checkRewardDataList(rewardList);
		return rewardList;
	}

	/**
	 * 配置表可能配错，随机到了某个权重，但这个权得又没有相应奖励 检查返回的奖励列表，如果有空，直接剔除掉(前端表现会少得到一个奖励)
	 */
	private void checkRewardDataList(List<GambleRewardData> list) {
		// for(GambleRewardData rewardData : list){
		// if(rewardData == null){
		// list.remove(rewardData);
		// checkRewardDataList(list);
		// return;
		// }
		// }

		for (int i = list.size() - 1; i >= 0; --i) {
			GambleRewardData rewardData = list.get(i);
			if (rewardData == null) {
				list.remove(i);
			}
		}
	}

	/** 获取单抽奖列表 */
	private List<GambleRewardData> getOneLotteryList(EGambleType gambleType) {
		List<GambleRewardData> list = new ArrayList<GambleRewardData>();
		GambleCfg cfg = GambleCfgDAO.getInstance().getGambleCfg(gambleType);
		TableGamble gambleItem = getGambleItem();
		GambleRewardData rewardData = null;
		if (gambleItem.getFreeCount(gambleType, ELotteryType.ONE) < cfg.getFreeFirst()) {// 免费抽奖有私有权重组
			List<GambleRewardCfg> cfgList = GambleRewardCfgDAO.getInstance().getWeightGroup(EGambleWeight.FREE_FIRST, gambleType, 1);
			rewardData = GambleUtils.getRandomRewardData(cfgList);
		} else if (gambleItem.getGoldCount(gambleType, ELotteryType.ONE) < cfg.getFirstCount()) {// 非免费抽奖有私有权重组
			List<GambleRewardCfg> cfgList = GambleRewardCfgDAO.getInstance().getWeightGroup(EGambleWeight.FIRST_COUNT, gambleType, 1);
			rewardData = GambleUtils.getRandomRewardData(cfgList);
		}
		if (rewardData == null) {
			list.add(getOneReward(gambleType));
		} else {
			list.add(rewardData);
		}
		return list;
	}

	/** 获取6连抽奖励列表 */
	private List<GambleRewardData> getSixLotteryList(EGambleType gambleType) {
		List<GambleRewardData> list = new ArrayList<GambleRewardData>();
		GambleRewardData rewardData = GambleUtils.getRandomRewardData(getDestinyHot());
		int randomIndex = new Random().nextInt(6);
		for (int i = 0; i < 6; i++) {
			if (i == randomIndex) {
				list.add(rewardData);
			} else {
				list.add(getOneReward(gambleType));
			}
		}
		return list;
	}

	/** 获取10连抽奖励列表 */
	private List<GambleRewardData> getTenLotteryList(EGambleType gambleType) {
		GambleCfg cfg = GambleCfgDAO.getInstance().getGambleCfg(gambleType);
		EGambleWeight gambleWeight = EGambleWeight.TEN_GUARANTEE;
		int maxGuaranteeCount = 0;
		if (getGambleItem().getGoldCount(gambleType, ELotteryType.TEN) == 0) {// 首次
			gambleWeight = EGambleWeight.FIRST_TEN_GUARANTEE;
			maxGuaranteeCount = cfg.getFirstTenCount();
		} else {
			gambleWeight = EGambleWeight.TEN_GUARANTEE;
			maxGuaranteeCount = cfg.getTenCount();
		}
		/* 获取保底 */
		List<GambleRewardData> list = new ArrayList<GambleRewardData>();
		for (int i = 1; i <= maxGuaranteeCount; i++) {
			List<GambleRewardCfg> cfgList = GambleRewardCfgDAO.getInstance().getWeightGroup(gambleWeight, gambleType, i);
			GambleRewardData rewardData = GambleUtils.getRandomRewardData(cfgList);
			if (rewardData != null) {
				list.add(rewardData);
			}
		}
		/*------*/
		int length = 10 - list.size();
		for (int i = 0; i < length; i++) {
			list.add(getOneReward(gambleType));
		}
		Collections.shuffle(list);
		return list;
	}

	/** 获取一个奖励 */
	private GambleRewardData getOneReward(EGambleType gambleType) {
		GambleCfg cfg = GambleCfgDAO.getInstance().getGambleCfg(gambleType);
		EGambleWeight weight = GambleUtils.getRandomWeightGroup(cfg.getProbabilityList());
		List<GambleRewardCfg> list = GambleRewardCfgDAO.getInstance().getWeightGroup(weight, gambleType);
		return GambleUtils.getRandomRewardData(list);
	}

	public TableGamble getGambleItem() {
		return gambleDAO.get(userId);
	}

	public boolean save() {
		// return gambleDAO.update(tableGamble);
		gambleDAO.update(userId);
		return true;
	}

}
